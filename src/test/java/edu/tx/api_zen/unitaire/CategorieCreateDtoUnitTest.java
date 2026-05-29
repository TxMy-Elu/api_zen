package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.CategorieCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class CategorieCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validCategorieDtoWithLibelle_shouldBeValid() {
        CategorieCreateDto categorieDto = new CategorieCreateDto();
        categorieDto.setLibelle("Santé");
        categorieDto.setDescription("Articles sur la santé");

        Set<ConstraintViolation<CategorieCreateDto>> contraintes = validator.validate(categorieDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void categorieDtoWithoutLibelle_shouldNotBeValid() {
        CategorieCreateDto categorieDto = new CategorieCreateDto();
        categorieDto.setDescription("Articles sur la santé");

        Set<ConstraintViolation<CategorieCreateDto>> contraintes = validator.validate(categorieDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("libelle")
                        && c.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName().equals("NotBlank")));
    }

    @Test
    void categorieDtoWithBlankLibelle_shouldNotBeValid() {
        CategorieCreateDto categorieDto = new CategorieCreateDto();
        categorieDto.setLibelle("   ");
        categorieDto.setDescription("Articles sur la santé");

        Set<ConstraintViolation<CategorieCreateDto>> contraintes = validator.validate(categorieDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("libelle")));
    }

    @Test
    void categorieDtoWithoutDescription_shouldBeValid() {
        CategorieCreateDto categorieDto = new CategorieCreateDto();
        categorieDto.setLibelle("Sport");

        Set<ConstraintViolation<CategorieCreateDto>> contraintes = validator.validate(categorieDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
