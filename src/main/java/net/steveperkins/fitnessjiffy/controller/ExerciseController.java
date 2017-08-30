package net.steveperkins.fitnessjiffy.controller;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.steveperkins.fitnessjiffy.dto.ExerciseDTO;
import net.steveperkins.fitnessjiffy.dto.ExercisePerformedDTO;
import net.steveperkins.fitnessjiffy.dto.UserDTO;
import net.steveperkins.fitnessjiffy.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class ExerciseController extends AbstractController {

    @Autowired
    ExerciseService exerciseService;

    @RequestMapping(value = "/exercise", method = RequestMethod.GET)
    @Nonnull
    public String viewMainExercisePage(
            @Nonnull
            @RequestParam(value = "date", defaultValue = TODAY)
            final String dateString,

            @Nonnull final Model model
    ) {
        final UserDTO user = currentAuthenticatedUser();
        final Date date = stringToSqlDate(dateString);

        final List<ExerciseDTO> exercisesPerformedRecently = exerciseService.findPerformedRecently(user.getId(), date);
        final List<ExercisePerformedDTO> exercisePerformedThisDate = exerciseService.findPerformedOnDate(user.getId(), date);
        int totalMinutes = 0;
        int totalCaloriesBurned = 0;
        for (final ExercisePerformedDTO exercisePerformed : exercisePerformedThisDate) {
            totalMinutes += exercisePerformed.getMinutes();
            totalCaloriesBurned += exercisePerformed.getCaloriesBurned();
        }

        final List<String> categories = exerciseService.findAllCategories();
        final String firstCategory = (categories.size() > 0) ? categories.get(0) : "";
        final List<ExerciseDTO> exercisesInCategory = Lists.transform(
                exerciseService.findExercisesInCategory(firstCategory),
                new Function<ExerciseDTO, ExerciseDTO>() {
                    @Nullable
                    @Override
                    public ExerciseDTO apply(final @Nonnull ExerciseDTO exerciseDTO) {
                        if (exerciseDTO.getDescription().length() > 50) {
                            String description = exerciseDTO.getDescription().substring(0, 47) + "...";
                            exerciseDTO.setDescription(description);
                        }
                        return exerciseDTO;
                    }
                }
        );

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
    @Nonnull
    public String addExercisePerformed(
            @Nonnull @RequestParam(value = "exerciseId", required = true) final String exerciseIdString,
            @Nonnull @RequestParam(value = "date", defaultValue = TODAY) final String dateString,
            @Nonnull final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser();
        final Date date = stringToSqlDate(dateString);
        final UUID exerciseId = UUID.fromString(exerciseIdString);
        exerciseService.addExercisePerformed(userDTO.getId(), exerciseId, date);
        return viewMainExercisePage(dateString, model);
    }

    @RequestMapping(value = "/exercise/performed/update")
    @Nonnull
    public String updateExercisePerformed(
            @Nonnull @RequestParam(value = "exercisePerformedId", required = true) final String exercisePerformedId,
            @Nonnull @RequestParam(value = "minutes", required = true, defaultValue = "1") final int minutes,
            @Nonnull @RequestParam(value = "action", required = true) final String action,
            @Nonnull final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser();
        final UUID exercisePerformedUUID = UUID.fromString(exercisePerformedId);
        final ExercisePerformedDTO exercisePerformedDTO = exerciseService.findExercisePerformedById(exercisePerformedUUID);
        final String dateString = dateFormat.format(exercisePerformedDTO.getDate());
        if (!userDTO.getId().equals(exercisePerformedDTO.getUserId())) {
            // TODO: Add logging, and flash message on view template
            System.out.println("\n\nThis user is unable to update this exercise performed\n");
        } else if (action.equalsIgnoreCase("update")) {
            exerciseService.updateExercisePerformed(exercisePerformedUUID, minutes);
        } else if (action.equalsIgnoreCase("delete")) {
            exerciseService.deleteExercisePerformed(exercisePerformedUUID);
        }
        return viewMainExercisePage(dateString, model);
    }

    @RequestMapping(value = "/exercise/bycategory/{category}")
    @Nonnull
    public
    @ResponseBody
    List<ExerciseDTO> findExercisesInCategory(@Nonnull @PathVariable final String category) {
        return exerciseService.findExercisesInCategory(category);
    }

}
