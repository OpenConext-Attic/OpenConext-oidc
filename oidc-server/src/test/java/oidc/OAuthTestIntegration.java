package oidc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.nimbusds.jose.JWSHeader;
import org.apache.commons.codec.binary.Base64;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class OAuthTestIntegration extends AbstractTestIntegration {

  private String callback = "http://localhost:8889/callback";
  private String serverUrl = "http://localhost:8080";
  private RestTemplate template = new RestTemplate();

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

  @Test
  public void testAuthorizationCodeFlow() throws Exception {
    wireMockRule.stubFor(get(urlMatching("/callback.*")).withQueryParam("code", matching(".*")).willReturn(aResponse().withStatus(200)));

    ResponseEntity<String> response = template.exchange(serverUrl + "/authorize?response_type=code&client_id=test_client&scope=read&redirect_uri={callback}", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class, callback);
    assertEquals(200, response.getStatusCode().value());

    List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/callback.*")));
    assertEquals(1, requests.size());

    String authorizationCode = requests.get(0).queryParameter("code").firstValue();

    MultiValueMap<String, String> bodyMap = getAuthorizationCodeFormParameters(authorizationCode);

    HttpHeaders headers = getAuthorizationHeaders();

    Map body = template.exchange(serverUrl + "/token", HttpMethod.POST, new HttpEntity<>(bodyMap, headers), Map.class).getBody();
    assertEquals("bearer", ((String) body.get("token_type")).toLowerCase());
    String accessToken = (String) body.get("access_token");
    assertAccessToken(accessToken);

    // Now for the completeness of the scenario call the Introspect endpoint (e.g. impersonating a Resource Server) using the accessCode
    String uriString = UriComponentsBuilder.fromHttpUrl(serverUrl + "/introspect").queryParam("token", accessToken).build().toUriString();
    Map<String, Object> introspect = template.exchange(uriString, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
    assertIntrospectResult(introspect);
  }

  private void assertAccessToken(String accessToken) throws Exception {
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
  }

  private void assertIntrospectResult(Map<String, Object> introspect) {
    Object active = introspect.get("active");
    if (active instanceof String) {
      assertEquals(true, Boolean.valueOf((String) active));
    }
    if (active instanceof Boolean) {
      assertEquals(true, Boolean.valueOf((Boolean) active));
    }
    assertEquals("read", introspect.get("scope"));
    assertEquals("urn:collab:person:example.com:admin", introspect.get("sub"));
    assertEquals("test_client", introspect.get("client_id"));
    assertEquals("bearer", ((String) introspect.get("token_type")).toLowerCase());
    assertEquals("John Doe", introspect.get("display_name"));
    assertEquals("john.doe@example.org", introspect.get("email"));
  }

  private MultiValueMap<String, String> getAuthorizationCodeFormParameters(String authorizationCode) {
    MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
    bodyMap.add("grant_type", "authorization_code");
    bodyMap.add("code", authorizationCode);
    bodyMap.add("redirect_uri", callback);
    return bodyMap;
  }

  private HttpHeaders getAuthorizationHeaders() {
    HttpHeaders headers = new HttpHeaders();
    String authenticationCredentials = "Basic " + new String(Base64.encodeBase64(new String("test_client" + ":" + "secret").getBytes(Charset.forName("UTF-8"))));
    headers.add("Authorization", authenticationCredentials);
    headers.add("Content-Type", "application/x-www-form-urlencoded");
    headers.add("Accept", "application/json");
    return headers;
  }

}
