package oidc.oauth;

import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class OAuthTestIntegration extends AbstractTestIntegration {

  @Test
  public void testAuthorizationCodeFlow() throws Exception {
    String scope = "read";

    Map body = doTestAuthorizationCode(scope);
    String accessToken = (String) body.get("access_token");

    //Call the userinfo endpoint - not allowed for oauth2
    HttpHeaders headers = getAuthorizationHeadersForUserInfo(accessToken);
    try {
      template.exchange(serverUrl + "/userinfo", HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
      fail();
    } catch (HttpClientErrorException e) {
      assertEquals(e.getStatusCode(),HttpStatus.FORBIDDEN);
    }
  }

  @Test
  public void testOAuthImplicitFlow() throws Exception {
    String scope = "openid profile email organization userids entitlement";
    String fragment = doTestOAuthImplicitFlow(scope, "token");
    assertAccessToken(fragment, false);
  }
}
