package net.steveperkins.fitnessjiffy.dto.converter;

import net.steveperkins.fitnessjiffy.domain.Food;
import org.springframework.core.convert.converter.Converter;

public class FoodToFoodDTO implements Converter<Food, FoodDTO> {

    @Override
    public FoodDTO convert(Food food) {
        if(food == null) {
            return null;
        }
        return new FoodDTO(
                food.getId(),
                food.getOwner().getId(),
                food.getName(),
                food.getDefaultServingType(),
                food.getServingTypeQty(),
                food.getCalories(),
                food.getFat(),
                food.getSaturatedFat(),
                food.getCarbs(),
                food.getFiber(),
                food.getSugar(),
                food.getProtein(),
                food.getSodium()
        );
    }

}
