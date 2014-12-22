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
package org.springframework.boot.orient.sample.shiro.shiro;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.orient.sample.shiro.model.Permission;
import org.springframework.boot.orient.sample.shiro.model.User;
import org.springframework.boot.orient.sample.shiro.repository.UserRepository;
import org.springframework.boot.orient.sample.shiro.model.Role;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Shiro authentication & authorization realm that relies on OrientDB as
 * datastore.
 */
@Component
public class OrientDbRealm extends AuthorizingRealm {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            final AuthenticationToken token)
            throws AuthenticationException {
        final UsernamePasswordToken credentials = (UsernamePasswordToken) token;
        final String email = credentials.getUsername();
        if (email == null) {
            throw new UnknownAccountException("Email not provided");
        }
        final User user = userRepository.findByEmailAndActive(email, true);
        if (user == null) {
            throw new UnknownAccountException("Account does not exist");
        }
        return new SimpleAuthenticationInfo(email, user.getPassword().toCharArray(),
                ByteSource.Util.bytes(email), getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            final PrincipalCollection principals) {
        // retrieve role names and permission names
        final String email = (String) principals.getPrimaryPrincipal();
        final User user = userRepository.findByEmailAndActive(email, true);
        if (user == null) {
            throw new UnknownAccountException("Account does not exist");
        }
        final int totalRoles = user.getRoles().size();
        final Set<String> roleNames = new LinkedHashSet<>(totalRoles);
        final Set<String> permissionNames = new LinkedHashSet<>();
        if (totalRoles > 0) {
            for (Role role : user.getRoles()) {
                roleNames.add(role.getName());
                for (Permission permission : role.getPermissions()) {
                    permissionNames.add(permission.getName());
                }
            }
        }
        final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo(roleNames);
        info.setStringPermissions(permissionNames);
        return info;
    }

}
