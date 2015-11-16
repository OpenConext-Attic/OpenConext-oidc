package oidc.openid;

import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class OIDCTestIntegration extends AbstractTestIntegration {

  @Test
  public void testOpenIdCodeFlow() throws Exception {
    wireMockRule.stubFor(get(urlMatching("/callback.*")).withQueryParam("code", matching(".*")).willReturn(aResponse().withStatus(200)));

    String scope = "openid profile email organization userids entitlement";
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

    HttpHeaders headers = getAuthorizationHeadersForTokenFetch();

    Map body = template.exchange(serverUrl + "/token", HttpMethod.POST, new HttpEntity<>(bodyMap, headers), Map.class).getBody();
    assertEquals("bearer", ((String) body.get("token_type")).toLowerCase());

    String accessToken = (String) body.get("access_token");
    assertAccessToken(accessToken);

    String idToken = (String) body.get("id_token");
    assertTokenId(idToken);

    // Call the introspect endpoint (e.g. impersonating a Resource Server) using the accessCode
    String introspectUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/introspect")
        .queryParam("token", accessToken)
        .build().toUriString();
    Map<String, Object> introspect = template.exchange(introspectUri, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
    assertIntrospectResult(introspect, scope);

    //Call the userinfo endpoint
    headers = getAuthorizationHeadersForUserInfo(accessToken);
    Map<String, Object> userinfo = template.exchange(serverUrl + "/userinfo", HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
    assertUserInfoResult(userinfo);
  }

  @Test
  public void testOpenIdImplicitIdTokenFlow() throws Exception {
    String scope = "openid profile email organization userids entitlement";
    String authorizeUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/authorize")
        .queryParam("response_type", "id_token")
        .queryParam("client_id", TEST_CLIENT)
        .queryParam("scope", scope)
        .queryParam("redirect_uri", callback)
        .build().toUriString();
    //we don't want follow redirects so we use the TestRestTemplate
    ResponseEntity<String> response = new TestRestTemplate().exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
    assertEquals(302, response.getStatusCode().value());

    URI location = response.getHeaders().getLocation();
    String fragment = location.getFragment().split("=")[1];
    assertTokenId(fragment);
  }

}
