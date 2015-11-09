package oidc;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
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

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class OAuthTestIntegration extends AbstractTestIntegration {

  private String callback = "http://localhost:8889/callback";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

  @Test
  public void test_skip_confirmation_autoapprove_true() throws InterruptedException {
    HttpHeaders headers = getAuthorizationHeaders();

    String serverUrl = "http://localhost:8080";

    RestTemplate template = new RestTemplate();

    wireMockRule.stubFor(get(urlMatching("/callback.*")).withQueryParam("code", matching(".*")).willReturn(aResponse().withStatus(200)));

    ResponseEntity<String> response = template.exchange(serverUrl + "/authorize?response_type=code&client_id=test_client&scope=read&redirect_uri={callback}", HttpMethod.GET, new HttpEntity<>(headers), String.class, callback);
    assertEquals(200, response.getStatusCode().value());

    List<LoggedRequest> requests = findAll(getRequestedFor(urlMatching("/callback.*")));
    assertEquals(1, requests.size());

    String authorizationCode = requests.get(0).queryParameter("code").firstValue();

    MultiValueMap<String, String> bodyMap = getAuthorizationCodeFormParameters(authorizationCode);

    Map body = template.exchange(serverUrl + "/token", HttpMethod.POST, new HttpEntity<>(bodyMap, headers), Map.class).getBody();
    assertEquals("bearer", body.get("token_type"));
    String accessToken = (String) body.get("access_token");
    assertNotNull(accessToken);

    // Now for the completeness of the scenario retrieve the Principal (e.g. impersonating a Resource Server) using the accessCode
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("token", accessToken);
    Map principal = template.exchange(serverUrl + "/oauth/check_token", HttpMethod.POST, new HttpEntity<>(formData, headers), Map.class).getBody();
    assertEquals("urn:collab:person:example.com:mock-user", principal.get("user_name"));

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
