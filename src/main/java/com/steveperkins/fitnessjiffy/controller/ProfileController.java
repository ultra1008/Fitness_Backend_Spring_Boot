package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.dto.WeightDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
final class ProfileController extends AbstractController {

    @GetMapping(value = "/")
    public final void handleRootUrl(final HttpServletResponse response) throws IOException {
        response.sendRedirect("/profile.html");
    }

    @GetMapping(value = "/api/user/weight/{date}")
    @ResponseBody
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

//    @GetMapping(value = "/profile")
//    public final String viewMainProfilePage(
//            @Nullable @RequestParam(value = "date", required = false) final String dateString,
//            final HttpServletRequest request,
//            final Model model
//    ) {
//        final UserDTO userDTO = currentAuthenticatedUser(request);
//        final Date date = dateString == null ? todaySqlDateForUser(userDTO) : stringToSqlDate(dateString);
//        final WeightDTO weight = userService.findWeightOnDate(userDTO, date);
//        final String weightEntry = (weight == null) ? "" : String.valueOf(weight.getPounds());
//        final int heightFeet = (int) (userDTO.getHeightInInches() / 12);
//        final int heightInches = (int) userDTO.getHeightInInches() % 12;
//
//        model.addAttribute("allActivityLevels", User.ActivityLevel.values());
//        model.addAttribute("allGenders", User.Gender.values());
//        model.addAttribute("allTimeZones", new TreeSet<String>(ZoneId.getAvailableZoneIds()));
//        model.addAttribute("user", userDTO);
//        model.addAttribute("dateString", dateString);
//        model.addAttribute("weightEntry", weightEntry);
//        model.addAttribute("heightFeet", heightFeet);
//        model.addAttribute("heightInches", heightInches);
//        return PROFILE_TEMPLATE;
//    }

    @PostMapping(value = "/profile/save")
    public final void updateProfile(
            @Nonnull @RequestParam(value = "date", required = false) final String dateString,
            @Nonnull @RequestParam(value = "currentPassword") final String currentPassword,
            @Nonnull @RequestParam(value = "newPassword") final String newPassword,
            @Nonnull @RequestParam(value = "reenterNewPassword") final String reenterNewPassword,
            @RequestParam(value = "heightFeet") final int heightFeet,
            @RequestParam(value = "heightInches") final int heightInches,
            @Nonnull @ModelAttribute("user") final UserDTO userDTO,
            final HttpServletResponse response,
            final Model model
    ) throws IOException {
        if (currentPassword == null || currentPassword.isEmpty()) {
            model.addAttribute("profileSaveError", "You must verify the current password in order to make any changes to this profile.");
        } else if (!userService.verifyPassword(userDTO, currentPassword)) {
            model.addAttribute("profileSaveError", "The password entered does not match the current password.");
        } else if (newPassword != null && !newPassword.isEmpty() && reenterNewPassword != null && !reenterNewPassword.equals(newPassword)) {
            model.addAttribute("profileSaveError", "The 'New Password' and 'Re-enter New Password' fields did not match.");
        } else {
            // Apply the height fields to the user entity
            userDTO.setHeightInInches( (heightFeet * 12) + heightInches );

            // Update user in the database
            if (newPassword.isEmpty()) {
                userService.updateUser(userDTO);
            } else {
                userService.updateUser(userDTO, newPassword);
            }
        }
        response.sendRedirect("/profile.html");
    }

}
