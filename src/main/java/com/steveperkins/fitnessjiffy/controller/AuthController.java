package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController extends AbstractController {

    @PostMapping("/userpass")
    public LoginResponse doLogin(
            @RequestBody final Map<String, Object> payload,
            final HttpServletResponse response
    ) throws ServletException, IOException {
        // Validate inputs
        final LoginResponse loginResponse = new LoginResponse();
        if (payload.get("username") == null || !(payload.get("username") instanceof String)
                || payload.get("password") == null || !(payload.get("password") instanceof String)) {
            loginResponse.setError("Username and password are required");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return loginResponse;
        }

        // Check credentials
        final String username = (String) payload.get("username");
        final String password = (String) payload.get("password");
        final UserDTO userDTO = userService.findByEmail(username);
        if (userDTO == null || !userService.verifyPassword(userDTO, password)) {
            loginResponse.setError("The username and password do not match");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return loginResponse;
        }

        // Authentication success, return JWT
        // TODO: Make the secret key value dynamic... pulled from environment variable or properties file or whatever
        final String token = Jwts.builder()
                .setSubject(username)
                .claim("email", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.HS256, "secretkey")
                .compact();
        loginResponse.setToken(token);
        return loginResponse;
    }

    private static class LoginResponse {
        private String token;
        private String error;
        public String getToken() {
            return token;
        }
        public void setToken(final String token) {
            this.token = token;
        }
        public String getError() {
            return error;
        }
        public void setError(final String error) {
            this.error = error;
        }
    }

}
