package com.pshenai.velvetdrive.configs;

import com.pshenai.velvetdrive.entities.user.UserFactory;
import com.pshenai.velvetdrive.entities.user.UserRole;
import com.pshenai.velvetdrive.entities.user.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
public class AuthHandler implements AuthenticationSuccessHandler {

    private final UserFactory factory;
    private final PasswordEncoder encoder;

    public AuthHandler(UserFactory factory, PasswordEncoder encoder) {
        this.factory = factory;
        this.encoder = encoder;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken)authentication;
        OAuth2User user = token.getPrincipal();

        Map<String, Object> attributes = user.getAttributes();

//        DriveUser driveUser = new DriveUser((String) attributes.get("email"),
//                token.getName(), UserRole.USER, (String) attributes.get("name"), "");

        factory.createUser((String) attributes.get("email"), encoder.encode(token.getName()), UserRole.USER,(String) attributes.get("name"));

        response.sendRedirect("/drive?folderList=true");

    }
}
