package com.steveperkins.fitnessjiffy.dto.converter;

import com.steveperkins.fitnessjiffy.domain.FoodEaten;
import com.steveperkins.fitnessjiffy.dto.FoodEatenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class FoodEatenToFoodEatenDTO implements Converter<FoodEaten, FoodEatenDTO> {

    private final FoodToFoodDTO foodDTOConverter;

    @Autowired
    public FoodEatenToFoodEatenDTO(final FoodToFoodDTO foodDTOConverter) {
        this.foodDTOConverter = foodDTOConverter;
    }

    @Override

    public FoodEatenDTO convert(final FoodEaten foodEaten) {
        FoodEatenDTO dto = null;
        if (foodEaten != null) {
            dto = new FoodEatenDTO(
                    foodEaten.getId(),
                    foodEaten.getUser().getId(),
                    foodDTOConverter.convert(foodEaten.getFood()),
                    foodEaten.getDate(),
                    foodEaten.getServingType(),
                    foodEaten.getServingQty(),
                    foodEaten.getCalories(),
                    foodEaten.getFat(),
                    foodEaten.getSaturatedFat(),
                    foodEaten.getSodium(),
                    foodEaten.getCarbs(),
                    foodEaten.getFiber(),
                    foodEaten.getSugar(),
                    foodEaten.getProtein(),
                    foodEaten.getPoints()
            );
        }
        return dto;
    }

}
