package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
public class AuthController extends AbstractController {

    /**
     * TODO: Make this more RESTful... just return the JWT, and let the client worry about storing it and redirecting to an appropriate landing page
     * TODO: Change the endpoint URL to be more consistent with the other new ones (i.e. "/api/...").
     *
     * @param username
     * @param password
     * @param response
     * @throws ServletException
     * @throws IOException
     */
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

    /**
     * TODO: Make this more RESTful.  If the client handles deletion of the JWT token, then is this endpoint even necessary at all?
     *
     * @param request
     * @param response
     * @throws IOException
     */
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
