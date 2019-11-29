package com.steveperkins.fitnessjiffy.repository;

import com.steveperkins.fitnessjiffy.domain.Exercise;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends CrudRepository<Exercise, UUID> {

    @Query("SELECT DISTINCT(exercise.category) FROM Exercise exercise ORDER BY exercise.category")

    List<String> findAllCategories();


    List<Exercise> findByCategoryOrderByDescriptionAsc(String category);

    @Query(
            "SELECT exercise FROM Exercise exercise "
                + "WHERE LOWER(exercise.description) LIKE LOWER(CONCAT('%', :description, '%')) "
                + "ORDER BY exercise.description ASC"
    )

    List<Exercise> findByDescriptionLike(@Param("description") String description);

}
