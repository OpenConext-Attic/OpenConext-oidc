package oidc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.nimbusds.jose.JWSHeader;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.junit.*;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class AbstractTestIntegration {

  private static final String SECRET = "secret";

  protected String callback = "http://localhost:8889/callback";
  protected String serverUrl = "http://localhost:8080";
  protected RestTemplate template = new RestTemplate();
  protected HttpHeaders headersForTokenFetch;

  public static final String TEST_CLIENT = "https@//oidc.localhost.surfconext.nl";
  public static final String SUB = "75726e3a-636f-6c6c-6162-3a706572736f";

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
    List<String> tables = asList(
        "access_token",
        "refresh_token",
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

  @Before
  public void before() {
    this.headersForTokenFetch = getAuthorizationHeadersForTokenFetch();

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
    String basicAuthz = TEST_CLIENT + ":" + SECRET;
    String authenticationCredentials = "Basic " + new String(Base64.encodeBase64(basicAuthz.getBytes(Charset.forName("UTF-8"))));
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

  protected JWKVerifier assertAccessToken(String accessToken, boolean verifySignature) throws Exception {
    assertNotNull(accessToken);
    String jwkKeys = template.getForEntity(serverUrl + "/jwk", String.class).getBody();

    JWKVerifier verifier = new JWKVerifier(jwkKeys, accessToken);
    if (verifySignature) {
      assertTrue(verifier.verifySigned("oidc"));
    }

    JWSHeader header = verifier.header();
    assertEquals("oidc", header.getKeyID());
    assertEquals("RS256", header.getAlgorithm().getName());

    Map<String, Object> claims = verifier.claims().getClaims();
    assertEquals(singletonList(TEST_CLIENT), claims.get("aud"));
    assertEquals("http://localhost:8080/", claims.get("iss"));
    assertNotNull(claims.get("exp"));
    assertNotNull(claims.get("iat"));
    return verifier;
  }

  protected void assertTokenId(String tokenId) throws Exception {
    JWKVerifier verifier = this.assertAccessToken(tokenId, true);
    Map<String, Object> claims = verifier.claims().getClaims();
    assertEquals(SUB, claims.get("sub"));
  }

  @SuppressWarnings("unchecked")
  protected void assertIntrospectResult(Map<String, Object> introspect, String scope) {
    Boolean active = (Boolean) introspect.get("active");
    assertTrue(active);
    //the order might be different, so we want to use Set#equals
    Set<String> actual = new HashSet(asList(StringUtils.split((String) introspect.get("scope"), " ")));
    Set<String> expected = new HashSet(asList(StringUtils.split(scope, " ")));
    assertEquals(expected, actual);

    assertEquals(SUB, introspect.get("sub"));
    assertEquals(TEST_CLIENT, introspect.get("client_id"));
    assertEquals("bearer", ((String) introspect.get("token_type")).toLowerCase());
    assertEquals("surfnet.nl", introspect.get("schac_home"));
    assertEquals("urn:collab:person:example.com:local", introspect.get("unspecified_id"));
    assertEquals("http://mock-idp", introspect.get("authenticating_authority"));
  }

  protected void assertUserInfoResult(Map<String, Object> userInfo) {
    assertEquals(SUB, userInfo.get("sub"));
    assertEquals("John Doe", userInfo.get("name"));
    assertEquals("John Doe", userInfo.get("preferred_username"));
    assertEquals("John", userInfo.get("given_name"));
    assertEquals("Doe", userInfo.get("family_name"));
    assertEquals("NL", userInfo.get("locale"));
    assertEquals("john.doe@example.org", userInfo.get("email"));

    assertEquals("surfnet.nl", userInfo.get("schac_home_organization"));
    assertEquals("institution", userInfo.get("schac_home_organization_type"));
    assertEquals("principal_name", userInfo.get("edu_person_principal_name"));
    assertEquals("fd9021b35ce0e2bb4fc28d1781e6cbb9eb720fed", userInfo.get("edu_person_targeted_id"));
    assertEquals(asList("student", "faculty"), userInfo.get("edu_person_affiliations"));
    assertEquals(asList("student", "faculty"), userInfo.get("edu_person_scoped_affiliations"));
    assertEquals(singletonList("surfnet"), userInfo.get("is_member_ofs"));
    assertEquals(singletonList("personal"), userInfo.get("schac_personal_unique_codes"));
    assertEquals(asList("uid2", "uid1"), userInfo.get("uids"));
  }

  protected MultiValueMap<String, String> getAuthorizationCodeFormParameters(String authorizationCode) {
    MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
    bodyMap.add("grant_type", "authorization_code");
    bodyMap.add("code", authorizationCode);
    bodyMap.add("redirect_uri", callback);
    return bodyMap;
  }

  @SuppressWarnings("unchecked")
  protected Map doTestAuthorizationCode(String scope) throws Exception {
    wireMockRule.stubFor(get(urlMatching("/callback.*")).withQueryParam("code", matching(".*")).willReturn(aResponse().withStatus(200)));

    String authorizeUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/authorize")
        .queryParam("response_type", "code")
        .queryParam("client_id", TEST_CLIENT)
        .queryParam("scope", scope)
        .queryParam("redirect_uri", callback)
        .build().toUriString();
    ResponseEntity<String> response = template.exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertEquals(200, response.getStatusCode().value());

    String authorizationCode = getCodeFromCallback();

    MultiValueMap<String, String> bodyMap = getAuthorizationCodeFormParameters(authorizationCode);

    Map body = template.exchange(serverUrl + "/token", HttpMethod.POST, new HttpEntity<>(bodyMap, headersForTokenFetch), Map.class).getBody();
    assertEquals("bearer", ((String) body.get("token_type")).toLowerCase());
    String accessToken = (String) body.get("access_token");
    assertAccessToken(accessToken, true);

    // Call the Introspect endpoint (e.g. impersonating a Resource Server) using the accessCode
    String introspectUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/introspect")
        .queryParam("token", accessToken)
        .build().toUriString();
    Map<String, Object> introspect = template.exchange(introspectUri, HttpMethod.GET, new HttpEntity<>(headersForTokenFetch), Map.class).getBody();
    assertIntrospectResult(introspect, scope);

    return body;

  }

  protected String doTestOAuthImplicitFlow(String scope, String responseType) throws Exception {
    String authorizeUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/authorize")
        .queryParam("response_type", responseType)
        .queryParam("client_id", TEST_CLIENT)
        .queryParam("scope", scope)
        .queryParam("redirect_uri", callback)
        .build().toUriString();
    //we don't want follow redirects so we use the TestRestTemplate
    ResponseEntity<String> response = new TestRestTemplate().exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertEquals(302, response.getStatusCode().value());

    URI location = response.getHeaders().getLocation();
    return location.getFragment().split("=")[1];
  }

}
