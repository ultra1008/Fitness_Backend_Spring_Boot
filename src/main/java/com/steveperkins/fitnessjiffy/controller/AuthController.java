package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
public class AuthController extends AbstractController {

    @PostMapping(value = "/userpass")
    public void doLogin(
            @RequestParam final String username,
            @RequestParam final String password,
            final HttpServletResponse response
    ) throws ServletException, IOException {
        final UserDTO userDTO = userService.findByEmail(username);
        if (userDTO == null || !userService.verifyPassword(userDTO, password)) {
            response.sendRedirect("/profile?error=true");
        }
        // TODO: Make the secret key value dynamic... pulled from environment variable or properties file or whatever
        final String token = Jwts.builder()
                .setSubject(username)
                .claim("email", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + TimeUnit.DAYS.toMillis(1)))
                .signWith(SignatureAlgorithm.HS256, "secretkey")
                .compact();
        response.addCookie(new Cookie("Authorization", "Bearer " + token));
        response.sendRedirect("/profile.html");
    }

    @PostMapping(value = "/logout")
    public void doLogout(
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws IOException {
        final Optional<Cookie> jwtCookie =
                Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(cookie -> cookie.getName().equals("Authorization"))
                .findAny();
        if (jwtCookie.isPresent()) {
            final Cookie deletedCookie = jwtCookie.get();
            deletedCookie.setValue("");
            deletedCookie.setPath("/");
            deletedCookie.setMaxAge(0);
            response.addCookie(deletedCookie);
        }
        response.sendRedirect("/login.html?logout=true");
    }

}
