package edu.tx.api_zen.unitaire;

import edu.tx.api_zen.dto.ArticleCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ArticleCreateDtoUnitTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validArticleDtoWithAllMandatoryFields_shouldBeValid() {
        ArticleCreateDto articleDto = new ArticleCreateDto();
        articleDto.setTitre("Mon Article");
        articleDto.setTypeMedia("text");
        articleDto.setContenu("Contenu de l'article");
        articleDto.setIdCategorie(1);

        Set<ConstraintViolation<ArticleCreateDto>> contraintes = validator.validate(articleDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void articleDtoWithoutTitre_shouldNotBeValid() {
        ArticleCreateDto articleDto = new ArticleCreateDto();
        articleDto.setTypeMedia("text");
        articleDto.setContenu("Contenu");
        articleDto.setIdCategorie(1);

        Set<ConstraintViolation<ArticleCreateDto>> contraintes = validator.validate(articleDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("titre")));
    }

    @Test
    void articleDtoWithoutTypeMedia_shouldNotBeValid() {
        ArticleCreateDto articleDto = new ArticleCreateDto();
        articleDto.setTitre("Mon Article");
        articleDto.setContenu("Contenu");
        articleDto.setIdCategorie(1);

        Set<ConstraintViolation<ArticleCreateDto>> contraintes = validator.validate(articleDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("typeMedia")));
    }

    @Test
    void articleDtoWithBlankTitre_shouldNotBeValid() {
        ArticleCreateDto articleDto = new ArticleCreateDto();
        articleDto.setTitre("   ");
        articleDto.setTypeMedia("text");
        articleDto.setContenu("Contenu");
        articleDto.setIdCategorie(1);

        Set<ConstraintViolation<ArticleCreateDto>> contraintes = validator.validate(articleDto);

        Assertions.assertTrue(contraintes.stream()
                .anyMatch(c -> c.getPropertyPath().toString().equals("titre")));
    }

    @Test
    void articleDtoWithoutContenu_shouldBeValid() {
        ArticleCreateDto articleDto = new ArticleCreateDto();
        articleDto.setTitre("Mon Article");
        articleDto.setTypeMedia("text");
        articleDto.setIdCategorie(1);

        Set<ConstraintViolation<ArticleCreateDto>> contraintes = validator.validate(articleDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }

    @Test
    void articleDtoWithoutIdCategorie_shouldBeValid() {
        ArticleCreateDto articleDto = new ArticleCreateDto();
        articleDto.setTitre("Mon Article");
        articleDto.setTypeMedia("text");
        articleDto.setContenu("Contenu");

        Set<ConstraintViolation<ArticleCreateDto>> contraintes = validator.validate(articleDto);

        Assertions.assertTrue(contraintes.isEmpty());
    }
}
