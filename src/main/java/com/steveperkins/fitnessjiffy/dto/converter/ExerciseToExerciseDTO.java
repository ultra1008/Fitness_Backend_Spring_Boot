package com.steveperkins.fitnessjiffy.dto.converter;

import com.steveperkins.fitnessjiffy.domain.Exercise;
import com.steveperkins.fitnessjiffy.dto.ExerciseDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class ExerciseToExerciseDTO implements Converter<Exercise, ExerciseDTO> {

    @Override

    public ExerciseDTO convert(final Exercise exercise) {
        ExerciseDTO dto = null;
        if (exercise != null) {
            dto = new ExerciseDTO(
                    exercise.getId(),
                    exercise.getCode(),
                    exercise.getMetabolicEquivalent(),
                    exercise.getCategory(),
                    exercise.getDescription()
            );
        }
        return dto;
    }

}
