package edu.tx.api_zen.integration;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("API Zen - Suite de Tests d'Intégration Complète")
@SelectClasses({
    UserControllerIntegrationTest.class,
    CategorieControllerIntegrationTest.class,
    ExerciceControllerIntegrationTest.class,
    ArticleControllerIntegrationTest.class,
    ConsulterControllerIntegrationTest.class,
    ExercerControllerIntegrationTest.class,
    LogActiviteControllerIntegrationTest.class
})
public class ApiZenIntegrationTestSuite {

}

