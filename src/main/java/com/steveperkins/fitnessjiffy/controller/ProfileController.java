package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.domain.User;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.dto.WeightDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
final class ProfileController extends AbstractController {

    @GetMapping(value = "/")
    public final void handleRootUrl(final HttpServletResponse response) throws IOException {
        response.sendRedirect("/profile.html");
    }

    @GetMapping(value = "/api/user")
    public final UserDTO loadProfile(final HttpServletRequest request) {
        return currentAuthenticatedUser(request);
    }

    @PostMapping(value = "/api/user")
    public final void saveProfile(
            @RequestBody final Map<String, Object> payload,
            final HttpServletRequest request
    ) throws IOException {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        userDTO.setGender(User.Gender.fromString((String) payload.get("gender")));
        userDTO.setBirthdate(stringToSqlDate((String) payload.get("birthdate")));
        userDTO.setHeightInInches(Double.parseDouble((String) payload.get("heightInInches")));
        userDTO.setActivityLevel(User.ActivityLevel.fromString((String) payload.get("activityLevel")));
        userDTO.setFirstName((String) payload.get("firstName"));
        userDTO.setLastName((String) payload.get("lastName"));
        userDTO.setTimeZone((String) payload.get("timeZone"));
        userService.updateUser(userDTO);
    }

    @PostMapping(value = "/api/user/password")
    public final void savePassword(
            @RequestBody final Map<String, Object> payload,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) {
        final String currentPassword = (String) payload.get("currentPassword");
        final String newPassword = (String) payload.get("newPassword");
        final String reenterNewPassword = (String) payload.get("reenterNewPassword");

        final UserDTO userDTO = currentAuthenticatedUser(request);
        if (!newPassword.equals(reenterNewPassword)) {
            // "New Password" and "Re-enter New Password" fields don't match
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        } else if (!userService.verifyPassword(userDTO, currentPassword)) {
            // "Current Password" field doesn't match the user's current password
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        userService.updateUser(userDTO, newPassword);
    }

    @GetMapping(value = "/api/user/weight/{date}")
    public final Double loadWeight(
            @Nullable @PathVariable(name = "date", required = false) final String dateString,
            final HttpServletRequest request
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final java.sql.Date date = (dateString == null || dateString.isEmpty())
                ? todaySqlDateForUser(userDTO)
                : stringToSqlDate(dateString);
        final WeightDTO weightDTO = userService.findWeightOnDate(userDTO, date);
        return weightDTO == null ? null : weightDTO.getPounds();
    }

    @PostMapping(value = "/api/user/weight/{date}", consumes = "application/json")
    public final void saveWeight(
            @Nullable @PathVariable(name = "date", required = false) final String dateString,
            @Nullable @RequestBody final Map<String, Object> payload,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) throws IOException {
        double weight;
        try {
            weight = Double.parseDouble(payload.get("weight").toString());
        } catch (NullPointerException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final java.sql.Date date = (dateString == null || dateString.isEmpty())
                ? todaySqlDateForUser(userDTO)
                : stringToSqlDate(dateString);
        userService.updateWeight(userDTO, date, weight);
    }

}
