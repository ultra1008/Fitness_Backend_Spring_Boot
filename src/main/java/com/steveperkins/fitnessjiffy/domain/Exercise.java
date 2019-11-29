package com.steveperkins.fitnessjiffy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "exercise")
public final class Exercise {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "code", length = 5, nullable = false)
    private String code;

    @Column(name = "metabolic_equivalent", nullable = false)
    private Double metabolicEquivalent;

    @Column(name = "category", length = 25, nullable = false)
    private String category;

    @Column(name = "description", length = 250, nullable = false)
    private String description;

    public Exercise(
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

    public Exercise() {
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
        return description.trim();
    }

    public void setDescription(final String description) {
        this.description = (description == null) ? "" : description.trim();
    }

}
