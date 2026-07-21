package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.LogActiviteCreateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class LogActiviteCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validLogActiviteDtoWithMandatoryFields_shouldBeValid() {
        LogActiviteCreateDto logDto = new LogActiviteCreateDto();
        logDto.setTypeAction("CREATE");
        logDto.setTableConcernee("article");

        Set<ConstraintViolation<LogActiviteCreateDto>> contraintes = validator.validate(logDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void logActiviteDtoWithoutTypeAction_shouldNotBeValid() {
        LogActiviteCreateDto logDto = new LogActiviteCreateDto();
        logDto.setTableConcernee("article");

        Set<ConstraintViolation<LogActiviteCreateDto>> contraintes = validator.validate(logDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("typeAction")
                        && c.getConstraintDescriptor().getAnnotation() instanceof NotBlank));
    }

    @Test
    void logActiviteDtoWithoutTableConcernee_shouldNotBeValid() {
        LogActiviteCreateDto logDto = new LogActiviteCreateDto();
        logDto.setTypeAction("CREATE");

        Set<ConstraintViolation<LogActiviteCreateDto>> contraintes = validator.validate(logDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("tableConcernee")
                        && c.getConstraintDescriptor().getAnnotation() instanceof NotBlank));
    }

    @Test
    void logActiviteDtoWithBlankTypeAction_shouldNotBeValid() {
        LogActiviteCreateDto logDto = new LogActiviteCreateDto();
        logDto.setTypeAction("   ");
        logDto.setTableConcernee("article");

        Set<ConstraintViolation<LogActiviteCreateDto>> contraintes = validator.validate(logDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("typeAction")));
    }

    @Test
    void logActiviteDtoWithAllOptionalFields_shouldBeValid() {
        LogActiviteCreateDto logDto = new LogActiviteCreateDto();
        logDto.setTypeAction("UPDATE");
        logDto.setTableConcernee("user");
        logDto.setIdEnregistrement(42);
        logDto.setDetails("Modification d'utilisateur");
        logDto.setAdresseIp("192.168.1.100");
        logDto.setIdUtilisateur(1);

        Set<ConstraintViolation<LogActiviteCreateDto>> contraintes = validator.validate(logDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void logActiviteDtoWithoutOptionalFields_shouldBeValid() {
        LogActiviteCreateDto logDto = new LogActiviteCreateDto();
        logDto.setTypeAction("DELETE");
        logDto.setTableConcernee("categorie");

        Set<ConstraintViolation<LogActiviteCreateDto>> contraintes = validator.validate(logDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
