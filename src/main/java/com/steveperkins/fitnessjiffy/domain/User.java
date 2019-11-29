package com.steveperkins.fitnessjiffy.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "FITNESSJIFFY_USER")
public class User {

    public enum Gender {

        MALE, FEMALE;


        public static Gender fromString(final String s) {
            Gender match = null;
            for (final Gender gender : Gender.values()) {
                if (gender.toString().equalsIgnoreCase(s)) {
                    match = gender;
                }
            }
            return match;
        }

        @Override
        public String toString() {
            return super.toString();
        }

    }

    public enum ActivityLevel {

        SEDENTARY(1.25), LIGHTLY_ACTIVE(1.3), MODERATELY_ACTIVE(1.5), VERY_ACTIVE(1.7), EXTREMELY_ACTIVE(2.0);

        private double value;

        ActivityLevel(final double value) {
            this.value = value;
        }


        public static ActivityLevel fromValue(final double value) {
            ActivityLevel match = null;
            for (final ActivityLevel activityLevel : ActivityLevel.values()) {
                if (activityLevel.getValue() == value) {
                    match = activityLevel;
                }
            }
            return match;
        }


        public static ActivityLevel fromString(final String s) {
            ActivityLevel match = null;
            for (final ActivityLevel activityLevel : ActivityLevel.values()) {
                if (activityLevel.toString().equalsIgnoreCase(s)) {
                    match = activityLevel;
                }
            }
            return match;
        }

        public double getValue() {
            return this.value;
        }

        @Override
        public String toString() {
            final StringBuilder s = new StringBuilder(super.toString().toLowerCase().replace('_', ' '));
            for (int index = 0; index < s.length(); index++) {
                if (index == 0 || s.charAt(index - 1) == ' ') {
                    final String currentCharAsString = Character.toString(s.charAt(index));
                    s.replace(index, index + 1, currentCharAsString.toUpperCase());
                }
            }
            return s.toString();
        }

    }

    @Id
    @Column(name = "ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "GENDER", length = 6, nullable = false)
    private String gender;

    @Column(name = "BIRTHDATE", nullable = false)
    private Date birthdate;

    @Column(name = "HEIGHT_IN_INCHES", nullable = false)
    private double heightInInches;

    @Column(name = "ACTIVITY_LEVEL", nullable = false)
    private double activityLevel;

    @Column(name = "EMAIL", length = 100, nullable = false)
    private String email;

    @Column(name = "PASSWORD_HASH", length = 100, nullable = true)
    private String passwordHash;

    @Column(name = "FIRST_NAME", length = 20, nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", length = 20, nullable = false)
    private String lastName;

    @Column(name = "TIMEZONE", length = 50, nullable = false)
    private String timeZone;

    @Column(name = "CREATED_TIME", nullable = false)
    private Timestamp createdTime;

    @Column(name = "LAST_UPDATED_TIME", nullable = false)
    private Timestamp lastUpdatedTime;

    @OneToMany(mappedBy = "user")
    private Set<Weight> weights = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<Food> foods = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<FoodEaten> foodsEaten = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ExercisePerformed> exercisesPerformed = new HashSet<>();

    public User(
            final UUID id,
            final Gender gender,
            final Date birthdate,
            final double heightInInches,
            final ActivityLevel activityLevel,
            final String email,
            final String passwordHash,
            final String firstName,
            final String lastName,
            final String timeZone,
            final Timestamp createdTime,
            final Timestamp lastUpdatedTime
    ) {
        this.id = Optional.ofNullable(id).orElse(UUID.randomUUID());
        this.gender = gender.toString();
        this.birthdate = (Date) birthdate.clone();
        this.heightInInches = heightInInches;
        this.activityLevel = activityLevel.getValue();
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.timeZone = timeZone;
        this.createdTime = (Timestamp) createdTime.clone();
        this.lastUpdatedTime = (Timestamp) lastUpdatedTime.clone();
    }

    public User() {
    }


    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }


    public Gender getGender() {
        return Gender.fromString(gender);
    }

    public void setGender(final Gender gender) {
        this.gender = gender.toString();
    }


    public Date getBirthdate() {
        return (Date) birthdate.clone();
    }

    public void setBirthdate(final Date birthdate) {
        this.birthdate = (Date) birthdate.clone();
    }

    public double getHeightInInches() {
        return heightInInches;
    }

    public void setHeightInInches(final double heightInInches) {
        this.heightInInches = heightInInches;
    }


    public ActivityLevel getActivityLevel() {
        return ActivityLevel.fromValue(activityLevel);
    }

    public void setActivityLevel(final ActivityLevel activityLevel) {
        this.activityLevel = activityLevel.getValue();
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }


    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }


    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(final String timeZone) {
        this.timeZone = timeZone;
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


    public Set<Weight> getWeights() {
        return weights;
    }

    public void setWeights(final Set<Weight> weights) {
        this.weights = weights;
    }


    public Set<Food> getFoods() {
        return foods;
    }

    public void setFoods(final Set<Food> foods) {
        this.foods = foods;
    }


    public Set<FoodEaten> getFoodsEaten() {
        return foodsEaten;
    }

    public void setFoodsEaten(final Set<FoodEaten> foodsEaten) {
        this.foodsEaten = foodsEaten;
    }


    public Set<ExercisePerformed> getExercisesPerformed() {
        return exercisesPerformed;
    }

    public void setExercisesPerformed(final Set<ExercisePerformed> exercisesPerformed) {
        this.exercisesPerformed = exercisesPerformed;
    }

    @Override
    public boolean equals(final Object other) {
        boolean equals = false;
        if (other instanceof User) {
            final User that = (User) other;
            equals = this.getId().equals(that.getId())
                    && this.getGender().equals(that.getGender())
                    && this.getBirthdate().toString().equals(that.getBirthdate().toString())
                    && this.getHeightInInches() == that.getHeightInInches()
                    && this.getActivityLevel() == that.getActivityLevel()
                    && this.getEmail().equals(that.getEmail())
                    && this.getPasswordHash().equals(that.getPasswordHash())
                    && this.getFirstName().equals(that.getFirstName())
                    && this.getTimeZone().equals(that.getTimeZone())
                    && this.getLastName().equals(that.getLastName())
                    && this.getCreatedTime().equals(that.getCreatedTime())
                    && this.getLastUpdatedTime().equals(that.getLastUpdatedTime());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode()
                + this.getGender().hashCode()
                + this.getBirthdate().hashCode()
                + this.getActivityLevel().hashCode()
                + this.getEmail().hashCode()
                + (this.getPasswordHash() == null ? 0 : this.getPasswordHash().hashCode())
                + this.getFirstName().hashCode()
                + this.getLastName().hashCode()
                + this.getTimeZone().hashCode()
                + this.getCreatedTime().hashCode()
                + this.getLastUpdatedTime().hashCode();
    }

}
