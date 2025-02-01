package de.freeschool.api;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test setup needed for all REST API tests
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FreeSchoolApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public @interface GeneralTestSetup {
}
