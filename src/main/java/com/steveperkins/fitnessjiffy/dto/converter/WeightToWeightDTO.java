package com.steveperkins.fitnessjiffy.dto.converter;

import com.steveperkins.fitnessjiffy.domain.Weight;
import com.steveperkins.fitnessjiffy.dto.WeightDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class WeightToWeightDTO implements Converter<Weight, WeightDTO> {

    @Override

    public WeightDTO convert(final Weight weight) {
        WeightDTO dto = null;
        if (weight != null) {
            dto = new WeightDTO(
                    weight.getId(),
                    weight.getUser().getId(),
                    weight.getDate(),
                    weight.getPounds()
            );
        }
        return dto;
    }

}
