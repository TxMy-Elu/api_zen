package edu.tx.api_zen.unitaire;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("API Zen - Suite de Tests Unitaires Complète")
@SelectClasses({
    UserCreateDtoUnitTest.class,
    UserUpdateDtoUnitTest.class,
    CategorieCreateDtoUnitTest.class,
    ExerciceCreateDtoUnitTest.class,
    ArticleCreateDtoUnitTest.class,
    ConsulterCreateDtoUnitTest.class,
    ExercerCreateDtoUnitTest.class,
    LogActiviteCreateDtoUnitTest.class
})
public class ApiZenTestSuite {

}

