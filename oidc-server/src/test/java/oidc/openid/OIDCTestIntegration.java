package oidc.openid;

import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.util.Map;

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

}
