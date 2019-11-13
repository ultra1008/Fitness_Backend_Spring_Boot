package com.steveperkins.fitnessjiffy.service;

import com.steveperkins.fitnessjiffy.domain.User;
import com.steveperkins.fitnessjiffy.domain.Weight;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.dto.WeightDTO;
import com.steveperkins.fitnessjiffy.dto.converter.UserToUserDTO;
import com.steveperkins.fitnessjiffy.dto.converter.WeightToWeightDTO;
import com.steveperkins.fitnessjiffy.repository.UserRepository;
import com.steveperkins.fitnessjiffy.repository.WeightRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
public final class UserService {

    private final ReportDataService reportDataService;
    private final UserRepository userRepository;
    private final WeightRepository weightRepository;
    private final UserToUserDTO userDTOConverter;
    private final WeightToWeightDTO weightDTOConverter;

    @Autowired
    public UserService(
            @Nonnull final ReportDataService reportDataService,
            @Nonnull final UserRepository userRepository,
            @Nonnull final WeightRepository weightRepository,
            @Nonnull final UserToUserDTO userDTOConverter,
            @Nonnull final WeightToWeightDTO weightDTOConverter
    ) {
        this.reportDataService = reportDataService;
        this.userRepository = userRepository;
        this.weightRepository = weightRepository;
        this.userDTOConverter = userDTOConverter;
        this.weightDTOConverter = weightDTOConverter;
    }

    @Nullable
    public UserDTO findByEmail(@Nullable final String email) {
        if (email == null) {
            return null;
        }
        final User user = userRepository.findByEmailEquals(email);
        return userDTOConverter.convert(user);
    }

    public void createUser(
            @Nonnull final UserDTO userDTO,
            @Nonnull final String password
    ) {
        final User user = new User(
                userDTO.getId(),
                userDTO.getGender(),
                userDTO.getBirthdate(),
                userDTO.getHeightInInches(),
                userDTO.getActivityLevel(),
                userDTO.getEmail(),
                encryptPassword(password),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getTimeZone(),
                new Timestamp(new java.util.Date().getTime()),
                new Timestamp(new java.util.Date().getTime())
        );
        userRepository.save(user);
        reportDataService.updateUserFromDate(user, new Date(System.currentTimeMillis()));
    }

    public void updateUser(@Nonnull final UserDTO userDTO) {
        updateUser(userDTO, null);
    }

    /**
     * TODO: Document
     * TODO: Require logout and re-login after changing the username (or password?)
     * TODO: Don't allow email changes at all when using an external identity provider (e.g. Google)
     * TODO: On second thought, maybe just don't allow email changes period?
     *
     * @param userDTO
     * @param newPassword
     */
    public void updateUser(
            @Nonnull final UserDTO userDTO,
            @Nullable final String newPassword
    ) {
        final User user = userRepository.findOne(userDTO.getId());
        user.setGender(userDTO.getGender());
        user.setBirthdate(userDTO.getBirthdate());
        user.setHeightInInches(userDTO.getHeightInInches());
        user.setActivityLevel(userDTO.getActivityLevel());
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setTimeZone(userDTO.getTimeZone());
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPasswordHash(encryptPassword(newPassword));
        }
        final java.util.Date lastUpdatedDate = reportDataService.adjustDateForTimeZone(new Date(new java.util.Date().getTime()), ZoneId.of(userDTO.getTimeZone()));
        user.setLastUpdatedTime(new Timestamp(lastUpdatedDate.getTime()));
        userRepository.save(user);
        reportDataService.updateUserFromDate(user, new Date(System.currentTimeMillis()));
    }

    @Nullable
    public WeightDTO findWeightOnDate(
            @Nonnull final UserDTO userDTO,
            @Nonnull final Date date
    ) {
        final User user = userRepository.findOne(userDTO.getId());
        final Weight weight = weightRepository.findByUserMostRecentOnDate(user, date);
        return weightDTOConverter.convert(weight);
    }

    public void updateWeight(
            @Nonnull final UserDTO userDTO,
            @Nonnull final Date date,
            final double pounds
    ) {
        final User user = userRepository.findOne(userDTO.getId());
        Weight weight = weightRepository.findByUserAndDate(user, date);
        if (weight == null) {
            weight = new Weight(
                    UUID.randomUUID(),
                    user,
                    date,
                    pounds
            );
        } else {
            weight.setPounds(pounds);
        }
        weightRepository.save(weight);
        reportDataService.updateUserFromDate(user, date);
    }

    public boolean verifyPassword(
            @Nonnull final UserDTO userDTO,
            @Nonnull final String password
    ) {
        final User user = userRepository.findOne(userDTO.getId());
        return BCrypt.checkpw(password, user.getPasswordHash());
    }

    @Nonnull
    public String encryptPassword(@Nonnull final String rawPassword) {
        final String salt = BCrypt.gensalt(10, new SecureRandom());
        return BCrypt.hashpw(rawPassword, salt);
    }

    @Nullable
    public String getPasswordHash(@Nonnull final UserDTO userDTO) {
        final User user = userRepository.findOne(userDTO.getId());
        return user.getPasswordHash();
    }

}
