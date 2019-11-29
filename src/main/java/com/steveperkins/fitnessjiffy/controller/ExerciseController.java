package com.steveperkins.fitnessjiffy.controller;

import com.steveperkins.fitnessjiffy.dto.ExerciseDTO;
import com.steveperkins.fitnessjiffy.dto.ExercisePerformedDTO;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

@Controller
final class ExerciseController extends AbstractController {

    private final ExerciseService exerciseService;

    private final Function<ExerciseDTO, ExerciseDTO> truncateExerciseDescriptionFunction = (ExerciseDTO exerciseDTO) -> {
        if (exerciseDTO.getDescription().length() > 50) {
            final String description = exerciseDTO.getDescription().substring(0, 47) + "...";
            exerciseDTO.setDescription(description);
        }
        return exerciseDTO;
    };

    @Autowired
    public ExerciseController(final ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping(value = "/exercise")
    public final String viewMainExercisePage(
            @RequestParam(value = "date", required = false) final String dateString,
            final HttpServletRequest request,
            final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final Date date = dateString == null ? todaySqlDateForUser(userDTO) : stringToSqlDate(dateString);

        final List<ExerciseDTO> exercisesPerformedRecently = exerciseService.findPerformedRecently(userDTO.getId(), date)
                .stream()
                .map(truncateExerciseDescriptionFunction)
                .collect(toList());

        final List<String> categories = exerciseService.findAllCategories();
        final String firstCategory = (categories.size() > 0) ? categories.get(0) : "";
        final List<ExerciseDTO> exercisesInCategory = exerciseService.findExercisesInCategory(firstCategory)
                .stream()
                .map(truncateExerciseDescriptionFunction)
                .collect(toList());

        final List<ExercisePerformedDTO> exercisePerformedThisDate = exerciseService.findPerformedOnDate(userDTO.getId(), date);
        int totalMinutes = 0;
        int totalCaloriesBurned = 0;
        for (final ExercisePerformedDTO exercisePerformed : exercisePerformedThisDate) {
            totalMinutes += exercisePerformed.getMinutes();
            totalCaloriesBurned += exercisePerformed.getCaloriesBurned();
        }

        model.addAttribute("dateString", dateString);
        model.addAttribute("exercisesPerformedRecently", exercisesPerformedRecently);
        model.addAttribute("categories", categories);
        model.addAttribute("exercisesInCategory", exercisesInCategory);
        model.addAttribute("exercisesPerformedThisDate", exercisePerformedThisDate);
        model.addAttribute("totalMinutes", totalMinutes);
        model.addAttribute("totalCaloriesBurned", totalCaloriesBurned);
        return EXERCISE_TEMPLATE;
    }

    @RequestMapping(value = "/exercise/performed/add")
    public final String addExercisePerformed(
            @RequestParam(value = "exerciseId", required = true) final String exerciseIdString,
            @RequestParam(value = "date", required = false) final String dateString,
            final HttpServletRequest request,
            final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final Date date = dateString == null ? todaySqlDateForUser(userDTO) : stringToSqlDate(dateString);
        final UUID exerciseId = UUID.fromString(exerciseIdString);
        exerciseService.addExercisePerformed(userDTO.getId(), exerciseId, date);
        return viewMainExercisePage(dateString, request, model);
    }

    @RequestMapping(value = "/exercise/performed/update")
    public final String updateExercisePerformed(
            @RequestParam(value = "exercisePerformedId", required = true) final String exercisePerformedId,
            @RequestParam(value = "minutes", required = true, defaultValue = "1") final int minutes,
            @RequestParam(value = "action", required = true) final String action,
            final HttpServletRequest request,
            final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final UUID exercisePerformedUUID = UUID.fromString(exercisePerformedId);
        final ExercisePerformedDTO exercisePerformedDTO = exerciseService.findExercisePerformedById(exercisePerformedUUID);
        final String dateString = dateFormat.format(exercisePerformedDTO.getDate());
        if (!userDTO.getId().equals(exercisePerformedDTO.getUserId())) {
            System.out.println("\n\nThis user is unable to update this exercise performed\n");
        } else if (action.equalsIgnoreCase("update")) {
            exerciseService.updateExercisePerformed(exercisePerformedUUID, minutes);
        } else if (action.equalsIgnoreCase("delete")) {
            exerciseService.deleteExercisePerformed(exercisePerformedUUID);
        }
        return viewMainExercisePage(dateString, request, model);
    }

    @RequestMapping(value = "/exercise/bycategory/{category}")
    @ResponseBody
    public final List<ExerciseDTO> findExercisesInCategory(@PathVariable final String category) {
        return exerciseService.findExercisesInCategory(category);
    }

    @RequestMapping(value = "/exercise/search/{searchString}")
    @ResponseBody
    public final List<ExerciseDTO> searchExercises(@PathVariable final String searchString) {
        return exerciseService.searchExercises(searchString);
    }

}
