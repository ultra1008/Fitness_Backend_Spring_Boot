package com.steveperkins.fitnessjiffy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(
        name = "food",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id", "owner_id"})
)
public final class Food {

    public enum ServingType {

        OUNCE(1), CUP(8), POUND(16), PINT(16), TABLESPOON(0.5), TEASPOON(0.1667), GRAM(0.03527), CUSTOM(0);

        private double value;

        ServingType(final double value) {
            this.value = value;
        }


        public static ServingType fromValue(final double value) {
            ServingType match = null;
            for (final ServingType servingType : ServingType.values()) {
                if (servingType.getValue() == value) {
                    match = servingType;
                }
            }
            return match;
        }


        public static ServingType fromString(final String s) {
            ServingType match = null;
            for (final ServingType servingType : ServingType.values()) {
                if (servingType.toString().equalsIgnoreCase(s)) {
                    match = servingType;
                }
            }
            return match;
        }

        public final double getValue() {
            return this.value;
        }

    }

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = true)
    private User owner;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "default_serving_type", length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private ServingType defaultServingType;

    @Column(name = "serving_type_qty", nullable = false)
    private Double servingTypeQty;

    @Column(name = "calories", nullable = false)
    private Integer calories;

    @Column(name = "fat", nullable = false)
    private Double fat;

    @Column(name = "saturated_fat", nullable = false)
    private Double saturatedFat;

    @Column(name = "carbs", nullable = false)
    private Double carbs;

    @Column(name = "fiber", nullable = false)
    private Double fiber;

    @Column(name = "sugar", nullable = false)
    private Double sugar;

    @Column(name = "protein", nullable = false)
    private Double protein;

    @Column(name = "sodium", nullable = false)
    private Double sodium;

    @Column(name = "created_time", nullable = false)
    private Timestamp createdTime = new Timestamp(new java.util.Date().getTime());

    @Column(name = "last_updated_time", nullable = false)
    private Timestamp lastUpdatedTime = new Timestamp(new java.util.Date().getTime());

    public Food(
            final UUID id,
            final User owner,
            final String name,
            final ServingType defaultServingType,
            final double servingTypeQty,
            final int calories,
            final double fat,
            final double saturatedFat,
            final double carbs,
            final double fiber,
            final double sugar,
            final double protein,
            final double sodium,
            final Timestamp createdTime,
            final Timestamp lastUpdatedTime
    ) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.owner = owner;
        this.name = name;
        this.defaultServingType = defaultServingType;
        this.servingTypeQty = servingTypeQty;
        this.calories = calories;
        this.fat = fat;
        this.saturatedFat = saturatedFat;
        this.carbs = carbs;
        this.fiber = fiber;
        this.sugar = sugar;
        this.protein = protein;
        this.sodium = sodium;
        this.createdTime = (Timestamp) createdTime.clone();
        this.lastUpdatedTime = (Timestamp) lastUpdatedTime.clone();
    }

    public Food() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(final User owner) {
        this.owner = owner;
    }


    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }


    public ServingType getDefaultServingType() {
        return defaultServingType;
    }

    public void setDefaultServingType(final ServingType defaultServingType) {
        this.defaultServingType = defaultServingType;
    }


    public Double getServingTypeQty() {
        return servingTypeQty;
    }

    public void setServingTypeQty(final Double servingTypeQty) {
        this.servingTypeQty = servingTypeQty;
    }


    public Integer getCalories() {
        return calories;
    }

    public void setCalories(final Integer calories) {
        this.calories = calories;
    }


    public Double getFat() {
        return fat;
    }

    public void setFat(final Double fat) {
        this.fat = fat;
    }


    public Double getSaturatedFat() {
        return saturatedFat;
    }

    public void setSaturatedFat(final Double saturatedFat) {
        this.saturatedFat = saturatedFat;
    }


    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(final Double carbs) {
        this.carbs = carbs;
    }


    public Double getFiber() {
        return fiber;
    }

    public void setFiber(final Double fiber) {
        this.fiber = fiber;
    }


    public Double getSugar() {
        return sugar;
    }

    public void setSugar(final Double sugar) {
        this.sugar = sugar;
    }


    public Double getProtein() {
        return protein;
    }

    public void setProtein(final Double protein) {
        this.protein = protein;
    }


    public Double getSodium() {
        return sodium;
    }

    public void setSodium(final Double sodium) {
        this.sodium = sodium;
    }


    public Timestamp getCreatedTime() {
        return (Timestamp) createdTime.clone();
    }

    public void setCreatedTime(final Timestamp createdTime) {
        this.createdTime = (Timestamp) createdTime.clone();
    }


    public Timestamp getLastUpdatedTime() {
        return (Timestamp) lastUpdatedTime.clone();
    }

    public void setLastUpdatedTime(final Timestamp lastUpdatedTime) {
        this.lastUpdatedTime = (Timestamp) lastUpdatedTime.clone();
    }

    public double getPoints() {
        final double fiber = (this.fiber <= 4) ? this.fiber : 4;
        final double points = (calories / 50.0) + (fat / 12.0) - (fiber / 5.0);
        return (points > 0) ? points : 0;
    }

}
