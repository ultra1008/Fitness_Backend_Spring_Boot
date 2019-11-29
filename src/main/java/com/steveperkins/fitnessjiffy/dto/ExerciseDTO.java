package com.steveperkins.fitnessjiffy.dto;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public final class ExerciseDTO implements Serializable {

    private UUID id;
    private String code;
    private Double metabolicEquivalent;
    private String category;
    private String description;

    public ExerciseDTO(
            final UUID id,
            final String code,
            final double metabolicEquivalent,
            final String category,
            final String description
    ) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.code = code;
        this.metabolicEquivalent = metabolicEquivalent;
        this.category = category;
        this.description = description;
    }

    public ExerciseDTO() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }


    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }


    public Double getMetabolicEquivalent() {
        return metabolicEquivalent;
    }

    public void setMetabolicEquivalent(final Double metabolicEquivalent) {
        this.metabolicEquivalent = metabolicEquivalent;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(final String category) {
        this.category = category;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public boolean equals(final Object other) {
        boolean equals = false;
        if (other instanceof ExerciseDTO) {
            final ExerciseDTO that = (ExerciseDTO) other;
            equals = this.getId().equals(that.getId())
                    && this.getCode().equals(that.getCode())
                    && this.getMetabolicEquivalent().equals(that.getMetabolicEquivalent())
                    && this.getCategory().equals(that.getCategory())
                    && this.getDescription().equals(that.getDescription());
        }
        return equals;
   }

    @Override
    public int hashCode() {
        final int idHash = (id == null) ? 0 : id.hashCode();
        final int codeHash = (code == null) ? 0 : code.hashCode();
        final int metabolicEquivalentHash = (metabolicEquivalent == null) ? 0 : metabolicEquivalent.intValue();
        final int categoryHash = (category == null) ? 0 : category.hashCode();
        final int descriptionHash = (description == null) ? 0 : description.hashCode();

        return idHash + codeHash + metabolicEquivalentHash + categoryHash + descriptionHash;
   }
}
