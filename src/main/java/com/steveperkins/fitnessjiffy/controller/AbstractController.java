package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public abstract class AbstractController {

    static final String EXERCISE_TEMPLATE = "exercise";
    static final String REPORT_TEMPLATE = "report";

    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    protected UserService userService;

    /**
     * Used by child class controllers to obtain the currently authenticated user from Spring Security.
     */
    @Nullable
    final UserDTO currentAuthenticatedUser(final HttpServletRequest request) {
        return userService.findByEmail((String) request.getAttribute("email"));
    }

    final java.sql.Date stringToSqlDate(@Nonnull final String dateString) {
        java.sql.Date date;
        try {
            date = java.sql.Date.valueOf(dateString);
        } catch (final IllegalArgumentException e) {
            e.printStackTrace();
            date = new java.sql.Date(new Date().getTime());
        }
        return date;
    }

    final java.sql.Date todaySqlDateForUser(@Nullable final UserDTO user) {
        if (user == null) {
            return new java.sql.Date(new Date().getTime());
        } else {
            final ZoneId timeZone = ZoneId.of(user.getTimeZone());
            final ZonedDateTime zonedDateTime = ZonedDateTime.now(timeZone);
            return new java.sql.Date(zonedDateTime.toLocalDate().atStartOfDay(timeZone).toInstant().toEpochMilli());
        }
    }

}
