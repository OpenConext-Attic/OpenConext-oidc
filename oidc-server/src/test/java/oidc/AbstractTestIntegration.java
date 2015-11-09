package oidc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class AbstractTestIntegration {

  @BeforeClass
  public static void beforeClass() throws IOException {
    BasicDataSource ds = getBasicDataSource();

    Flyway flyway = new Flyway();
    flyway.setDataSource(ds);
    flyway.setLocations("db.migration", "db.test.migration");

    flyway.migrate();
  }

  @AfterClass
  public static void afterClass() throws IOException {
    BasicDataSource ds = getBasicDataSource();
    JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
    List<String> tables = Arrays.asList(
        "access_token",
        "approved_site",
        "authentication_holder",
        "authentication_holder_authority",
        "authentication_holder_extension",
        "authentication_holder_request_parameter",
        "authentication_holder_resource_id",
        "authentication_holder_response_type",
        "authentication_holder_scope",
        "saved_user_auth",
        "saved_user_auth_authority",
        "token_scope");
    for (String table : tables) {
      jdbcTemplate.update("delete from " + table);
    }

  }

  private static BasicDataSource getBasicDataSource() throws IOException {
    Properties props = getProperties();
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName(props.getProperty("jdbc.driverClassName"));
    ds.setUrl(props.getProperty("jdbc.url"));
    ds.setUsername(props.getProperty("jdbc.username"));
    ds.setPassword(props.getProperty("jdbc.password"));
    return ds;
  }

  private static Properties getProperties() throws IOException {
    Properties props = new Properties();
    props.load(new ClassPathResource("application.oidc.properties").getInputStream());
    return props;
  }
}
