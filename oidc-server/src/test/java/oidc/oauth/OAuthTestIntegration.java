package oidc.oauth;

import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URI;
import java.util.Map;

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
            assertEquals(e.getStatusCode(), HttpStatus.FORBIDDEN);
        }
    }

    @Test
    public void testOAuthImplicitFlow() throws Exception {
        String scope = "openid profile email organization userids entitlement";
        URI uri = doTestOAuthImplicitFlow(scope, "token");
        String accessToken = super.parseTokenFromString(uri.getFragment(), "access_token");
        assertAccessToken(accessToken, false);
    }

}
