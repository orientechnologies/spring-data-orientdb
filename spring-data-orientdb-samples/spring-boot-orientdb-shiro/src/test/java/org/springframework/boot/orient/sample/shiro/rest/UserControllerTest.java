/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.boot.orient.sample.shiro.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orient.sample.shiro.Application;
import org.springframework.boot.orient.sample.shiro.OrientDbConfiguration;
import org.springframework.boot.orient.sample.shiro.ShiroConfiguration;
import org.springframework.boot.orient.sample.shiro.model.Permission;
import org.springframework.boot.orient.sample.shiro.model.Role;
import org.springframework.boot.orient.sample.shiro.model.User;
import org.springframework.boot.orient.sample.shiro.repository.PermissionRepository;
import org.springframework.boot.orient.sample.shiro.repository.RoleRepository;
import org.springframework.boot.orient.sample.shiro.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest(classes = {Application.class, OrientDbConfiguration.class, ShiroConfiguration.class})
@WebAppConfiguration
@TestExecutionListeners(inheritListeners = false, listeners = {DependencyInjectionTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

    private final String BASE_URL = "http://localhost:8080/users";
    private final String USER_NAME = "Paulo Pires";
    private final String USER_EMAIL = "pjpires@gmail.com";
    private final String USER_PWD = "123qwe";
    @Autowired
    private DefaultPasswordService passwordService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PermissionRepository permissionRepo;

    @Before
    public void setUp() {
        // clean-up users, roles and permissions
        userRepo.deleteAll();
        roleRepo.deleteAll();
        permissionRepo.deleteAll();
        // define permissions
        final Permission p1 = new Permission();
        p1.setName("VIEW_USER_ROLES");
        permissionRepo.save(p1);
        // define roles
        final Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleAdmin.getPermissions().add(p1);
        roleRepo.save(roleAdmin);
        // define user
        final User user = new User();
        user.setActive(true);
        user.setCreated(System.currentTimeMillis());
        user.setEmail(USER_EMAIL);
        user.setName(USER_NAME);
        user.setPassword(passwordService.encryptPassword(USER_PWD));
        user.getRoles().add(roleAdmin);
        userRepo.save(user);
    }

    @Test
    public void test_count() {
        assertEquals(1, userRepo.count());
    }

    @Test
    public void test_authenticate_success() throws JsonProcessingException {
        // authenticate
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(USER_EMAIL, USER_PWD));
        System.out.println(json);
        final ResponseEntity<String> response = new TestRestTemplate(
                TestRestTemplate.HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat("/auth"),
                HttpMethod.POST, new HttpEntity<>(json, headers), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void test_authenticate_failure() throws JsonProcessingException {
        // authenticate
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String json = new ObjectMapper().writeValueAsString(
                new UsernamePasswordToken(USER_EMAIL, "wrong password"));
        System.out.println(json);
        final ResponseEntity<String> response = new TestRestTemplate(
                TestRestTemplate.HttpClientOption.ENABLE_COOKIES).exchange(BASE_URL.concat("/auth"),
                HttpMethod.POST, new HttpEntity<>(json, headers), String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.UNAUTHORIZED));
    }

}
