package com.javabite.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String redirectUrl = "/";

        // Get user role
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            log.info("User {} logged in with role: {}", authentication.getName(), role);

            switch (role) {
                case "ROLE_ADMIN":
                    redirectUrl = "/admin/dashboard";
                    break;
                case "ROLE_CHEF":
                    redirectUrl = "/chef/dashboard";
                    break;
                case "ROLE_CUSTOMER":
                    redirectUrl = "/customer/dashboard";
                    break;
                default:
                    redirectUrl = "/";
            }
        }

        log.info("Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}