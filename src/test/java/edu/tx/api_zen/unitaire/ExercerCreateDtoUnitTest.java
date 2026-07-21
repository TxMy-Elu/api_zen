package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.ExercerCreateDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ExercerCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validExercerDtoWithAllMandatoryFields_shouldBeValid() {
        ExercerCreateDto exercerDto = new ExercerCreateDto();
        exercerDto.setUserId(1);
        exercerDto.setExerciceId(1);

        Set<ConstraintViolation<ExercerCreateDto>> contraintes = validator.validate(exercerDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void exercerDtoWithCompletedAt_shouldBeValid() {
        ExercerCreateDto exercerDto = new ExercerCreateDto();
        exercerDto.setUserId(1);
        exercerDto.setExerciceId(1);
        exercerDto.setCompletedAt("2026-04-20T10:15:30Z");

        Set<ConstraintViolation<ExercerCreateDto>> contraintes = validator.validate(exercerDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void exercerDtoWithoutUserId_shouldNotBeValid() {
        ExercerCreateDto exercerDto = new ExercerCreateDto();
        exercerDto.setExerciceId(1);

        Set<ConstraintViolation<ExercerCreateDto>> contraintes = validator.validate(exercerDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("userId")
                        && c.getConstraintDescriptor().getAnnotation() instanceof NotNull));
    }

    @Test
    void exercerDtoWithoutExerciceId_shouldNotBeValid() {
        ExercerCreateDto exercerDto = new ExercerCreateDto();
        exercerDto.setUserId(1);

        Set<ConstraintViolation<ExercerCreateDto>> contraintes = validator.validate(exercerDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("exerciceId")
                        && c.getConstraintDescriptor().getAnnotation() instanceof NotNull));
    }

    @Test
    void exercerDtoWithoutBothIds_shouldNotBeValid() {
        ExercerCreateDto exercerDto = new ExercerCreateDto();

        Set<ConstraintViolation<ExercerCreateDto>> contraintes = validator.validate(exercerDto);

        Assertions.assertTrue(contraintes.size() >= 2);
        Assertions.assertTrue(contraintes.stream().anyMatch(c -> c.getPropertyPath().toString().equals("userId")));
        Assertions.assertTrue(contraintes.stream().anyMatch(c -> c.getPropertyPath().toString().equals("exerciceId")));
    }

    @Test
    void exercerDtoWithoutCompletedAt_shouldBeValid() {
        ExercerCreateDto exercerDto = new ExercerCreateDto();
        exercerDto.setUserId(1);
        exercerDto.setExerciceId(1);

        Set<ConstraintViolation<ExercerCreateDto>> contraintes = validator.validate(exercerDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
