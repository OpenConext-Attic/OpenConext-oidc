package oidc.oauth;

import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class OAuthTestIntegration extends AbstractTestIntegration {

  @Test
  public void testAuthorizationCodeFlow() throws Exception {
    wireMockRule.stubFor(get(urlMatching("/callback.*")).withQueryParam("code", matching(".*")).willReturn(aResponse().withStatus(200)));

    String authorizeUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/authorize")
        .queryParam("response_type", "code")
        .queryParam("client_id", TEST_CLIENT)
        .queryParam("scope", "read")
        .queryParam("redirect_uri", callback)
        .build().toUriString();
    ResponseEntity<String> response = template.exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertEquals(200, response.getStatusCode().value());

    String authorizationCode = getCodeFromCallback();

    MultiValueMap<String, String> bodyMap = getAuthorizationCodeFormParameters(authorizationCode);

    HttpHeaders headers = getAuthorizationHeadersForTokenFetch();

    Map body = template.exchange(serverUrl + "/token", HttpMethod.POST, new HttpEntity<>(bodyMap, headers), Map.class).getBody();
    assertEquals("bearer", ((String) body.get("token_type")).toLowerCase());
    String accessToken = (String) body.get("access_token");
    assertAccessToken(accessToken);

    // Call the Introspect endpoint (e.g. impersonating a Resource Server) using the accessCode
    String introspectUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/introspect")
        .queryParam("token", accessToken)
        .build().toUriString();
    Map<String, Object> introspect = template.exchange(introspectUri, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
    assertIntrospectResult(introspect, "read");

    //Call the userinfo endpoint - not allowed for oauth2
    headers = getAuthorizationHeadersForUserInfo(accessToken);
    try {
      template.exchange(serverUrl + "/userinfo", HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
      fail();
    } catch (HttpClientErrorException e) {
      assertEquals(e.getStatusCode(),HttpStatus.FORBIDDEN);
    }


  }


}
