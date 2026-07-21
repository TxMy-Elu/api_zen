package edu.tx.api_zen.unitaire;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("API Zen - Suite de Tests Unitaires Complète")
@SelectClasses({
    // Validation des objets de transfert
    UserCreateDtoUnitTest.class,
    UserUpdateDtoUnitTest.class,
    CategorieCreateDtoUnitTest.class,
    ExerciceCreateDtoUnitTest.class,
    ArticleCreateDtoUnitTest.class,
    ConsulterCreateDtoUnitTest.class,
    ExercerCreateDtoUnitTest.class,
    LogActiviteCreateDtoUnitTest.class,
    // Sécurité : émission et vérification des jetons, chargement des comptes
    JwtUtilsUnitTest.class,
    JwtAuthenticationFilterUnitTest.class,
    CustomUserDetailsServiceUnitTest.class,
    RateLimitFilterUnitTest.class
})
public class ApiZenTestSuite {

}

