package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.ConsulterCreateDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ConsulterCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validConsulterDtoWithAllMandatoryFields_shouldBeValid() {
        ConsulterCreateDto consulterDto = new ConsulterCreateDto();
        consulterDto.setIdUtilisateur(1);
        consulterDto.setIdArticle(1);

        Set<ConstraintViolation<ConsulterCreateDto>> contraintes = validator.validate(consulterDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void consulterDtoWithoutIdUtilisateur_shouldNotBeValid() {
        ConsulterCreateDto consulterDto = new ConsulterCreateDto();
        consulterDto.setIdArticle(1);

        Set<ConstraintViolation<ConsulterCreateDto>> contraintes = validator.validate(consulterDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("idUtilisateur")
                        && c.getConstraintDescriptor().getAnnotation() instanceof NotNull));
    }

    @Test
    void consulterDtoWithoutIdArticle_shouldNotBeValid() {
        ConsulterCreateDto consulterDto = new ConsulterCreateDto();
        consulterDto.setIdUtilisateur(1);

        Set<ConstraintViolation<ConsulterCreateDto>> contraintes = validator.validate(consulterDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("idArticle")
                        && c.getConstraintDescriptor().getAnnotation() instanceof NotNull));
    }

    @Test
    void consulterDtoWithoutBothIds_shouldNotBeValid() {
        ConsulterCreateDto consulterDto = new ConsulterCreateDto();

        Set<ConstraintViolation<ConsulterCreateDto>> contraintes = validator.validate(consulterDto);

        Assertions.assertTrue(contraintes.size() >= 2);
        Assertions.assertTrue(contraintes.stream().anyMatch(c -> c.getPropertyPath().toString().equals("idUtilisateur")));
        Assertions.assertTrue(contraintes.stream().anyMatch(c -> c.getPropertyPath().toString().equals("idArticle")));
    }

    @Test
    void consulterDtoWithZeroIdUtilisateur_shouldBeValid() {
        ConsulterCreateDto consulterDto = new ConsulterCreateDto();
        consulterDto.setIdUtilisateur(0);
        consulterDto.setIdArticle(1);

        Set<ConstraintViolation<ConsulterCreateDto>> contraintes = validator.validate(consulterDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
