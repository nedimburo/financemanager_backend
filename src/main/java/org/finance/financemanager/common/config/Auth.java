package org.finance.financemanager.common.config;

import org.finance.financemanager.accessibility.users.entities.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.naming.AuthenticationException;

public class Auth {

    public static String getUserId() throws AuthenticationException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = (UserEntity) auth.getPrincipal();
        if (auth.isAuthenticated()) {
            return user.getId();
        } else {
            throw new AuthenticationException("Authentication failed");
        }
    }
}
