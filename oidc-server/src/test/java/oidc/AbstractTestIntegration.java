package oidc;

import org.flywaydb.core.Flyway;
import org.junit.BeforeClass;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderSupport;

import java.util.Properties;

public class AbstractTestIntegration {

  @BeforeClass
  public static void beforeClass() throws Exception {
    Properties props = new Properties();
    props.load(new ClassPathResource("application.oidc.properties").getInputStream());

    Flyway flyway = new Flyway();
    flyway.setDataSource(props.getProperty("jdbc.url"),props.getProperty("jdbc.username"),props.getProperty("jdbc.password"));
    flyway.setLocations("db.migration","db.test.migration");

    flyway.migrate();
  }
}
