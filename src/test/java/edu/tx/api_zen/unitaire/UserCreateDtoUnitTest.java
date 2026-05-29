package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.UserCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class UserCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validUserDtoWithAllMandatoryInformation_shouldBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setNom("Dupont");
        userDto.setPrenom("Jean");
        userDto.setEmail("jean.dupont@example.com");
        userDto.setPassword("password123");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void userDtoWithoutNom_shouldNotBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setPrenom("Jean");
        userDto.setEmail("jean@example.com");
        userDto.setPassword("password");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("nom")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotBlank")));
    }

    @Test
    void userDtoWithoutPrenom_shouldNotBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setNom("Dupont");
        userDto.setEmail("jean@example.com");
        userDto.setPassword("password");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("prenom")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotBlank")));
    }

    @Test
    void userDtoWithoutEmail_shouldNotBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setNom("Dupont");
        userDto.setPrenom("Jean");
        userDto.setPassword("password");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("email")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotBlank")));
    }

    @Test
    void userDtoWithoutPassword_shouldNotBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setNom("Dupont");
        userDto.setPrenom("Jean");
        userDto.setEmail("jean@example.com");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("password")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotBlank")));
    }

    @Test
    void userDtoWithoutRoleId_shouldNotBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setNom("Dupont");
        userDto.setPrenom("Jean");
        userDto.setEmail("jean@example.com");
        userDto.setPassword("password");

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("roleId")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotNull")));
    }

    @Test
    void userDtoWithBlankNom_shouldNotBeValid() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setNom("   ");
        userDto.setPrenom("Jean");
        userDto.setEmail("jean@example.com");
        userDto.setPassword("password");
        userDto.setRoleId(1);

        Set<ConstraintViolation<UserCreateDto>> contraintes = validator.validate(userDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("nom")));
    }
}
