package com.steveperkins.fitnessjiffy.repository;

import com.steveperkins.fitnessjiffy.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {


    User findByEmailEquals(String email);

}
