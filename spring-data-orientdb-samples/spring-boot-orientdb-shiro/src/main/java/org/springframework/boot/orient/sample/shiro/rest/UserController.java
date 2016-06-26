/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.boot.orient.sample.shiro.rest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orient.sample.shiro.model.Permission;
import org.springframework.boot.orient.sample.shiro.model.Role;
import org.springframework.boot.orient.sample.shiro.model.User;
import org.springframework.boot.orient.sample.shiro.repository.PermissionRepository;
import org.springframework.boot.orient.sample.shiro.repository.RoleRepository;
import org.springframework.boot.orient.sample.shiro.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * TODO add description
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.
            getLogger(UserController.class);

    @Autowired
    private DefaultPasswordService passwordService;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PermissionRepository permissionRepo;

    @RequestMapping(value = "/auth", method = POST)
    public void authenticate(@RequestBody final UsernamePasswordToken credentials) {
        LOGGER.info("Authenticating {} with password {}", credentials.getUsername(),
                credentials.getPassword());
        final Subject subject = SecurityUtils.getSubject();
        subject.login(credentials);
        // set attribute that will allow session querying
        subject.getSession().setAttribute("email", credentials.getUsername());
    }

    @RequestMapping(method = GET)
    @RequiresAuthentication
    @RequiresRoles("ADMIN")
    public List<User> getAll() {
        return userRepo.findAll();
    }

    @RequestMapping(value = "do_something", method = GET)
    @RequiresAuthentication
    @RequiresRoles("DO_SOMETHING")
    public List<User> dontHavePermission() {
        return userRepo.findAll();
    }

    @RequestMapping(method = PUT)
    public void initScenario() {
        LOGGER.info("Initializing scenario..");
        // clean-up users, roles and permissions
        userRepo.deleteAll();
        roleRepo.deleteAll();
        permissionRepo.deleteAll();
        // define permissions
        final Permission p1 = new Permission();
        p1.setName("VIEW_ALL_USERS");
        permissionRepo.save(p1);
        final Permission p2 = new Permission();
        p2.setName("DO_SOMETHING");
        permissionRepo.save(p2);
        // define roles
        final Role roleAdmin = new Role();
        roleAdmin.setName("ADMIN");
        roleAdmin.getPermissions().add(p1);
        roleRepo.save(roleAdmin);
        // define user
        final User user = new User();
        user.setActive(true);
        user.setCreated(System.currentTimeMillis());
        user.setEmail("pires@gmail.com");
        user.setName("Paulo Pires");
        user.setPassword(passwordService.encryptPassword("123qwe"));
        user.getRoles().add(roleAdmin);
        userRepo.save(user);
        LOGGER.info("Scenario initiated.");
    }

}
