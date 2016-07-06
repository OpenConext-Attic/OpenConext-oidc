package oidc.openid;

import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class OIDCTestIntegration extends AbstractTestIntegration {

  private String scope = "openid profile email organization userids entitlement";

  @Test
  @SuppressWarnings("unchecked")
  public void testOpenIdCodeFlow() throws Exception {
    Map body = doTestAuthorizationCode(scope);
    String accessToken = (String) body.get("access_token");

    String idToken = (String) body.get("id_token");
    assertTokenId(idToken);

    //Call the userinfo endpoint which is allowed because of openid spec
    HttpHeaders headers = getAuthorizationHeadersForUserInfo(accessToken);
    Map<String, Object> userinfo = template.exchange(serverUrl + "/userinfo", HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
    assertUserInfoResult(userinfo);
  }

  @Test
  public void testOpenIdImplicitIdTokenFlow() throws Exception {
    String fragment = doTestOAuthImplicitFlow(scope, "id_token");
    assertTokenId(fragment);
  }

  @Test(expected = HttpClientErrorException.class)
  @SuppressWarnings("unchecked")
  public void testOpenIdCodeFlowWithUnknownScope() throws Exception {
    doTestAuthorizationCode(scope + " bogus");
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testClientCredentials() throws Exception {
    String accessToken = doTestOAuthImplicitFlow("strange", "token", "https@//client.localhost.surfconext.nl");

    // Call the Introspect endpoint (e.g. impersonating a Resource Server) using the accessCode
    String introspectUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/introspect")
        .queryParam("token", accessToken)
        .build().toUriString();
    Map<String, Object> introspect = template.exchange(introspectUri, HttpMethod.GET, new HttpEntity<>(headersForTokenFetch), Map.class).getBody();

    assertEquals(true, introspect.get("active"));
    String scope = (String) introspect.get("scope");
    assertEquals("", scope);  
  }

}
