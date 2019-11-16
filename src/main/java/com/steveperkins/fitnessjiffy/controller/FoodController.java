package com.steveperkins.fitnessjiffy.controller;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.steveperkins.fitnessjiffy.domain.Food;

import com.steveperkins.fitnessjiffy.dto.FoodDTO;
import com.steveperkins.fitnessjiffy.dto.FoodEatenDTO;
import com.steveperkins.fitnessjiffy.dto.UserDTO;
import com.steveperkins.fitnessjiffy.service.ExerciseService;
import com.steveperkins.fitnessjiffy.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
final class FoodController extends AbstractController {

    private final FoodService foodService;
    private final ExerciseService exerciseService;

    @Autowired
    public FoodController(
            @Nonnull final FoodService foodService,
            @Nonnull final ExerciseService exerciseService
    ) {
        this.foodService = foodService;
        this.exerciseService = exerciseService;
    }

    @GetMapping(value = "/api/foodeaten/{date}")
    public final List<FoodEatenDTO> loadFoodsEaten(
            @PathVariable(name = "date") final String dateString,
            final HttpServletRequest request
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final Date date = dateString == null ? todaySqlDateForUser(userDTO) : stringToSqlDate(dateString);
        return foodService.findEatenOnDate(userDTO.getId(), date);
    }

    @PostMapping(value = "/api/foodeaten/{id}")
    public final void updateFoodEaten(
            @PathVariable(name = "id") final String idString,
            @RequestBody final Map<String, Object> payload,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) {
        final UUID foodEatenId = UUID.fromString(idString);
        final FoodEatenDTO foodEatenDTO = foodService.findFoodEatenById(foodEatenId);
        if (foodEatenDTO == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final UserDTO userDTO = currentAuthenticatedUser(request);
        if (!foodEatenDTO.getUserId().equals(userDTO.getId())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        Food.ServingType servingType;
        Double servingQty;
        try {
            servingType = Food.ServingType.fromString((String) payload.get("servingType"));
            servingQty = Double.parseDouble((String) payload.get("servingQty"));
            foodService.updateFoodEaten(foodEatenId, servingQty, servingType);
        } catch (final NullPointerException | NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        foodService.updateFoodEaten(foodEatenId, servingQty, servingType);
    }

    @DeleteMapping(value = "/api/foodeaten/{id}")
    public final void deleteFoodEaten(
            @PathVariable(name = "id") final String idString,
            final HttpServletRequest request,
            final HttpServletResponse response
    ) {
        final UUID foodEatenId = UUID.fromString(idString);
        final FoodEatenDTO foodEatenDTO = foodService.findFoodEatenById(foodEatenId);
        if (foodEatenDTO == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final UserDTO userDTO = currentAuthenticatedUser(request);
        if (!foodEatenDTO.getUserId().equals(userDTO.getId())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        foodService.deleteFoodEaten(foodEatenId);
    }












    @RequestMapping(value = "/food/eaten/add")
    public final String addFoodEaten(
            @Nonnull @RequestParam(value = "foodId", required = true) final String foodIdString,
            @Nonnull @RequestParam(value = "date", required = false) final String dateString,
            final HttpServletRequest request,
            final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        final Date date = dateString == null ? todaySqlDateForUser(userDTO) : stringToSqlDate(dateString);
        final UUID foodId = UUID.fromString(foodIdString);
        foodService.addFoodEaten(userDTO.getId(), foodId, date);
//        return viewMainFoodPage(dateString, request, model);
        return "";
    }

    @RequestMapping(value = "/food/search/{searchString}")
    @ResponseBody
    public final List<FoodDTO> searchFoods(
            @Nonnull @PathVariable final String searchString,
            final HttpServletRequest request
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        return foodService.searchFoods(userDTO.getId(), searchString);
    }

    @RequestMapping(value = "/food/get/{foodId}")
    @ResponseBody
    public final FoodDTO getFood(
            @Nonnull @PathVariable final String foodId,
            final HttpServletRequest request
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        FoodDTO foodDTO = foodService.getFoodById(UUID.fromString(foodId));
        // Only return foods that are visible to the requesting user
        if (foodDTO.getOwnerId() != null && !foodDTO.getOwnerId().equals(userDTO.getId())) {
            foodDTO = null;
        }
        return foodDTO;
    }

    @RequestMapping(value = "/food/update")
    @ResponseBody
    public final String createOrUpdateFood(
            @Nonnull @ModelAttribute final FoodDTO foodDTO,
            final HttpServletRequest request,
            final Model model
    ) {
        final UserDTO userDTO = currentAuthenticatedUser(request);
        String resultMessage;
        if (foodDTO.getId() == null) {
            resultMessage = foodService.createFood(foodDTO, userDTO);
        } else {
            resultMessage = foodService.updateFood(foodDTO, userDTO);
        }
        return resultMessage;
    }

}
