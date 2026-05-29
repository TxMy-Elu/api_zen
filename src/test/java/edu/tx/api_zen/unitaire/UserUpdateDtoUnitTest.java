package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.UserUpdateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class UserUpdateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserUpdateDtoWithAllFields_shouldBeValid() {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setNom("Dupont");
        userDto.setPrenom("Jean");
        userDto.setEmail("jean@example.com");
        userDto.setPassword("password123");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserUpdateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void userUpdateDtoWithOnlyNom_shouldBeValid() {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setNom("Dupont");

        Set<ConstraintViolation<UserUpdateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void userUpdateDtoWithOnlyEmail_shouldBeValid() {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setEmail("jean@example.com");

        Set<ConstraintViolation<UserUpdateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void userUpdateDtoEmpty_shouldBeValid() {
        UserUpdateDto userDto = new UserUpdateDto();

        Set<ConstraintViolation<UserUpdateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void userUpdateDtoWithOnlyPassword_shouldBeValid() {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setPassword("newPassword");

        Set<ConstraintViolation<UserUpdateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void userUpdateDtoWithOnlyRoleId_shouldBeValid() {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setRoleId(2);

        Set<ConstraintViolation<UserUpdateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
