package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.ExerciceCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ExerciceCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validExerciceDtoWithAllMandatoryFields_shouldBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setNom("Respiration Profonde");
        exerciceDto.setDureeInspiration(4);
        exerciceDto.setDureeApnee(4);
        exerciceDto.setDureeExpiration(6);
        exerciceDto.setDureeSession(120);
        exerciceDto.setDescription("Technique de respiration");

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void exerciceDtoWithoutNom_shouldNotBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setDureeInspiration(4);
        exerciceDto.setDureeApnee(4);
        exerciceDto.setDureeExpiration(6);
        exerciceDto.setDescription("Technique de respiration");

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("nom")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotBlank")));
    }

    @Test
    void exerciceDtoWithoutDureeInspiration_shouldNotBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setNom("Respiration Profonde");
        exerciceDto.setDureeApnee(4);
        exerciceDto.setDureeExpiration(6);
        exerciceDto.setDescription("Technique de respiration");

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("dureeInspiration")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotNull")));
    }

    @Test
    void exerciceDtoWithoutDureeApnee_shouldNotBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setNom("Respiration Profonde");
        exerciceDto.setDureeInspiration(4);
        exerciceDto.setDureeExpiration(6);
        exerciceDto.setDescription("Technique de respiration");

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("dureeApnee")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotNull")));
    }

    @Test
    void exerciceDtoWithoutDureeExpiration_shouldNotBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setNom("Respiration Profonde");
        exerciceDto.setDureeInspiration(4);
        exerciceDto.setDureeApnee(4);
        exerciceDto.setDescription("Technique de respiration");

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("dureeExpiration")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotNull")));
    }

    @Test
    void exerciceDtoWithBlankNom_shouldNotBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setNom("   ");
        exerciceDto.setDureeInspiration(4);
        exerciceDto.setDureeApnee(4);
        exerciceDto.setDureeExpiration(6);
        exerciceDto.setDescription("Technique de respiration");

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("nom")));
    }

    @Test
    void exerciceDtoWithoutDescription_shouldBeValid() {
        ExerciceCreateDto exerciceDto = new ExerciceCreateDto();
        exerciceDto.setNom("Respiration Profonde");
        exerciceDto.setDureeInspiration(4);
        exerciceDto.setDureeApnee(4);
        exerciceDto.setDureeExpiration(6);
        exerciceDto.setDureeSession(120);

        Set<ConstraintViolation<ExerciceCreateDto>> contraintes = validator.validate(exerciceDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
