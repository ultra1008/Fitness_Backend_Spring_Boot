package com.steveperkins.fitnessjiffy.repository;

import com.steveperkins.fitnessjiffy.domain.Exercise;
import com.steveperkins.fitnessjiffy.domain.ExercisePerformed;
import com.steveperkins.fitnessjiffy.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

public interface ExercisePerformedRepository extends CrudRepository<ExercisePerformed, UUID> {

    @Query(
            "SELECT exercisePerformed FROM ExercisePerformed exercisePerformed, Exercise exercise "
                + "WHERE exercisePerformed.exercise = exercise "
                + "AND exercisePerformed.user = :user "
                + "AND exercisePerformed.date = :date "
                + "ORDER BY exercise.description ASC"
    )

    List<ExercisePerformed> findByUserEqualsAndDateEquals(
            @Param("user") User user,
            @Param("date") Date date
    );

    @Query(
            "SELECT DISTINCT exercise FROM Exercise exercise, ExercisePerformed exercisePerformed "
                + "WHERE exercise = exercisePerformed.exercise "
                + "AND exercisePerformed.user = :user "
                + "AND exercisePerformed.date BETWEEN :startDate AND :endDate "
                + "ORDER BY exercise.description ASC"
    )

    List<Exercise> findByUserPerformedWithinRange(
            @Param("user") User user,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );

}
