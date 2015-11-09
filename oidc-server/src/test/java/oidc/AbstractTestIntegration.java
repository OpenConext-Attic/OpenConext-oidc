package oidc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.nimbusds.jose.JWSHeader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractTestIntegration {

  protected String callback = "http://localhost:8889/callback";
  protected String serverUrl = "http://localhost:8080";
  protected RestTemplate template = new RestTemplate();

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

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

  protected String getCodeFromCallback() {
    List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/callback.*")));
    assertEquals(1, requests.size());

    return requests.get(0).queryParameter("code").firstValue();
  }

  protected HttpHeaders getAuthorizationHeadersForTokenFetch() {
    HttpHeaders headers = new HttpHeaders();
    String authenticationCredentials = "Basic " + new String(Base64.encodeBase64(new String("test_client" + ":" + "secret").getBytes(Charset.forName("UTF-8"))));
    headers.add("Authorization", authenticationCredentials);
    headers.add("Content-Type", "application/x-www-form-urlencoded");
    headers.add("Accept", "application/json");
    return headers;
  }

  protected HttpHeaders getAuthorizationHeadersForUserInfo(String accessToken) {
    HttpHeaders headers = new HttpHeaders();
    String authenticationCredentials = "Bearer " + accessToken;
    headers.add("Authorization", authenticationCredentials);
    headers.add("Accept", "application/json");
    return headers;
  }

  protected JWKVerifier assertAccessToken(String accessToken) throws Exception {
    assertNotNull(accessToken);
    String jwkKeys = template.getForEntity(serverUrl + "/jwk", String.class).getBody();

    JWKVerifier verifier = new JWKVerifier(jwkKeys, accessToken);
    assertTrue(verifier.verifySigned("oidc"));

    JWSHeader header = verifier.header();
    assertEquals("oidc", header.getKeyID());
    assertEquals("RS256", header.getAlgorithm().getName());

    Map<String, Object> claims = verifier.claims().getClaims();
    assertEquals(Arrays.asList("test_client"), claims.get("aud"));
    assertEquals("http://localhost:8080/", claims.get("iss"));
    assertNotNull(claims.get("exp"));
    assertNotNull(claims.get("iat"));
    return verifier;
  }

  protected void assertTokenId(String tokenId) throws Exception {
    JWKVerifier verifier = this.assertAccessToken(tokenId);
    assertEquals(verifier.claims().getClaims().get("sub"), "urn:collab:person:example.com:admin");
  }

  protected void assertIntrospectResult(Map<String, Object> introspect, String scope) {
    Object active = introspect.get("active");
    if (active instanceof String) {
      assertEquals(true, Boolean.valueOf((String) active));
    }
    if (active instanceof Boolean) {
      assertEquals(true, Boolean.valueOf((Boolean) active));
    }
    assertEquals(scope, introspect.get("scope"));
    assertEquals("urn:collab:person:example.com:admin", introspect.get("sub"));
    assertEquals("test_client", introspect.get("client_id"));
    assertEquals("bearer", ((String) introspect.get("token_type")).toLowerCase());
    assertEquals("surfnet.nl", introspect.get("schac_home"));
  }

  protected void assertUserInfoResult(Map<String, Object> userInfo) {
    assertEquals("urn:collab:person:example.com:admin", userInfo.get("sub"));
    assertEquals("John Doe", userInfo.get("name"));
    assertEquals("surfnet.nl", userInfo.get("profile"));
    assertEquals("john.doe@example.org", userInfo.get("email"));
  }
  protected MultiValueMap<String, String> getAuthorizationCodeFormParameters(String authorizationCode) {
    MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
    bodyMap.add("grant_type", "authorization_code");
    bodyMap.add("code", authorizationCode);
    bodyMap.add("redirect_uri", callback);
    return bodyMap;
  }

}
