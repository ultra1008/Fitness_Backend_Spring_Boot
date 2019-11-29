package com.steveperkins.fitnessjiffy.service;

import com.steveperkins.fitnessjiffy.domain.Exercise;
import com.steveperkins.fitnessjiffy.domain.ExercisePerformed;
import com.steveperkins.fitnessjiffy.domain.User;
import com.steveperkins.fitnessjiffy.dto.ExerciseDTO;
import com.steveperkins.fitnessjiffy.dto.ExercisePerformedDTO;
import com.steveperkins.fitnessjiffy.dto.WeightDTO;
import com.steveperkins.fitnessjiffy.dto.converter.ExercisePerformedToExercisePerformedDTO;
import com.steveperkins.fitnessjiffy.dto.converter.ExerciseToExerciseDTO;
import com.steveperkins.fitnessjiffy.dto.converter.UserToUserDTO;
import com.steveperkins.fitnessjiffy.repository.ExercisePerformedRepository;
import com.steveperkins.fitnessjiffy.repository.ExerciseRepository;
import com.steveperkins.fitnessjiffy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public final class ExerciseService {

    private final UserService userService;
    private final ReportDataService reportDataService;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExercisePerformedRepository exercisePerformedRepository;
    private final UserToUserDTO userDTOConverter;
    private final ExerciseToExerciseDTO exerciseDTOConverter;
    private final ExercisePerformedToExercisePerformedDTO exercisePerformedDTOConverter;

    @Autowired
    public ExerciseService(
            final UserService userService,
            final ReportDataService reportDataService,
            final UserRepository userRepository,
            final ExerciseRepository exerciseRepository,
            final ExercisePerformedRepository exercisePerformedRepository,
            final UserToUserDTO userDTOConverter,
            final ExerciseToExerciseDTO exerciseDTOConverter,
            final ExercisePerformedToExercisePerformedDTO exercisePerformedDTOConverter
    ) {
        this.userService = userService;
        this.reportDataService = reportDataService;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
        this.exercisePerformedRepository = exercisePerformedRepository;
        this.userDTOConverter = userDTOConverter;
        this.exerciseDTOConverter = exerciseDTOConverter;
        this.exercisePerformedDTOConverter = exercisePerformedDTOConverter;
    }


    public final List<ExercisePerformedDTO> findPerformedOnDate(
            final UUID userId,
            final Date date
    ) {
        final User user = userRepository.findOne(userId);
        final WeightDTO weight = userService.findWeightOnDate(userDTOConverter.convert(user), date);

        final List<ExercisePerformed> exercisesPerformed = exercisePerformedRepository.findByUserEqualsAndDateEquals(user, date);
        return exercisesPerformed.stream()
                .map( (ExercisePerformed exercisePerformed) -> {
                    final ExercisePerformedDTO dto = exercisePerformedDTOConverter.convert(exercisePerformed);
                    if (dto != null) {
                        final int caloriesBurned = calculateCaloriesBurned(
                                exercisePerformed.getExercise().getMetabolicEquivalent(),
                                exercisePerformed.getMinutes(),
                                weight.getPounds()
                        );
                        dto.setCaloriesBurned(caloriesBurned);
                        final double pointsBurned = calculatePointsBurned(
                                exercisePerformed.getExercise().getMetabolicEquivalent(),
                                exercisePerformed.getMinutes(),
                                weight.getPounds()
                        );
                        dto.setPointsBurned(pointsBurned);
                    }
                    return dto;
                })
                .collect(toList());
    }


    public final List<ExerciseDTO> findPerformedRecently(
            final UUID userId,
            final Date currentDate
    ) {
        final User user = userRepository.findOne(userId);
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -14);
        final Date twoWeeksAgo = new Date(calendar.getTime().getTime());
        final List<Exercise> recentExercises = exercisePerformedRepository.findByUserPerformedWithinRange(
                user,
                new Date(twoWeeksAgo.getTime()),
                new Date(currentDate.getTime())
        );
        return recentExercises.stream()
                .map(exerciseDTOConverter::convert)
                .collect(toList());
    }

    public final void addExercisePerformed(
            final UUID userId,
            final UUID exerciseId,
            final Date date
    ) {
        final boolean duplicate = findPerformedOnDate(userId, date).stream()
                .anyMatch( (ExercisePerformedDTO exerciseAlreadyPerformed) -> exerciseAlreadyPerformed.getExercise().getId().equals(exerciseId) );
        if (!duplicate) {
            final User user = userRepository.findOne(userId);
            final Exercise exercise = exerciseRepository.findOne(exerciseId);
            final ExercisePerformed exercisePerformed = new ExercisePerformed(
                    UUID.randomUUID(),
                    user,
                    exercise,
                    date,
                    1
            );
            exercisePerformedRepository.save(exercisePerformed);
            reportDataService.updateUserFromDate(user, date);
        }
    }

    public final void updateExercisePerformed(
            final UUID exercisePerformedId,
            final int minutes
    ) {
        final ExercisePerformed exercisePerformed = exercisePerformedRepository.findOne(exercisePerformedId);
        exercisePerformed.setMinutes(minutes);
        exercisePerformedRepository.save(exercisePerformed);
        reportDataService.updateUserFromDate(exercisePerformed.getUser(), exercisePerformed.getDate());
    }

    public final void deleteExercisePerformed(final UUID exercisePerformedId) {
        final ExercisePerformed exercisePerformed = exercisePerformedRepository.findOne(exercisePerformedId);
        exercisePerformedRepository.delete(exercisePerformed);
        reportDataService.updateUserFromDate(exercisePerformed.getUser(), exercisePerformed.getDate());
    }


    public final ExercisePerformedDTO findExercisePerformedById(final UUID exercisePerformedId) {
        final ExercisePerformed exercisePerformed = exercisePerformedRepository.findOne(exercisePerformedId);
        return exercisePerformedDTOConverter.convert(exercisePerformed);
    }


    public final List<String> findAllCategories() {
        return exerciseRepository.findAllCategories();
    }


    public final List<ExerciseDTO> findExercisesInCategory(final String category) {
        return exerciseRepository.findByCategoryOrderByDescriptionAsc(category).stream()
                .map(exerciseDTOConverter::convert)
                .collect(toList());
    }


    public final List<ExerciseDTO> searchExercises(final String searchString) {
        return exerciseRepository.findByDescriptionLike(searchString).stream()
                .map(exerciseDTOConverter::convert)
                .collect(toList());
    }

    public static int calculateCaloriesBurned(
            final double metabolicEquivalent,
            final int minutes,
            final double weightInPounds
    ) {
        final double weightInKilograms = weightInPounds / 2.2;
        return (int) (metabolicEquivalent * 3.5 * weightInKilograms / 200 * minutes);
    }

    public static double calculatePointsBurned(
            final double metabolicEquivalent,
            final int minutes,
            final double weightInPounds
    ) {
        final int caloriesBurnedPerHour = calculateCaloriesBurned(metabolicEquivalent, 60, weightInPounds);
        double pointsBurned;
        if (caloriesBurnedPerHour < 400) {
            pointsBurned = weightInPounds * minutes * 0.000232;
        } else if (caloriesBurnedPerHour < 900) {
            pointsBurned = weightInPounds * minutes * 0.000327;
        } else {
            pointsBurned = weightInPounds * minutes * 0.0008077;
        }
        return pointsBurned;
    }

}
