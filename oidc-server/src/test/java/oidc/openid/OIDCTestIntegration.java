package oidc.openid;

import oidc.AbstractTestIntegration;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        Map<String, Object> userinfo = template.exchange(serverUrl + "/userinfo", HttpMethod.GET, new HttpEntity<>
            (headers), Map.class).getBody();
        assertUserInfoResult(userinfo);
    }

    @Test
    public void testOpenIdImplicitIdTokenFlow() throws Exception {
        String fragment = doTestOAuthImplicitFlow(scope, "id_token");
        assertTokenId(fragment);
    }

    @Test
    public void testOpenIdImplicitIdTokenFlowWithIdToken() throws Exception {
        String authorizeUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/authorize")
            .queryParam("response_type", "token")
            .queryParam("client_id", TEST_CLIENT)
            .queryParam("scope", scope)
            .queryParam("redirect_uri", callback)
            .build().toUriString();
        //we don't want follow redirects so we use the TestRestTemplate
        ResponseEntity<String> response = new TestRestTemplate().exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        assertEquals(302, response.getStatusCode().value());

        String fragment = response.getHeaders().getLocation().getFragment();
        assertTrue(fragment.contains("id_token="));
        assertTrue(fragment.contains("access_token="));
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
        Map<String, Object> introspect = template.exchange(introspectUri, HttpMethod.GET, new HttpEntity<>
            (headersForTokenFetch), Map.class).getBody();
        System.out.println(introspect);
        assertEquals(true, introspect.get("active"));
        assertEquals(1, List.class.cast(introspect.get("edumember_is_member_of")).size());
        assertEquals(2, List.class.cast(introspect.get("eduperson_entitlement")).size());
        String scope = (String) introspect.get("scope");
        assertEquals("", scope);
    }

    @Test
    public void testOpenIdResponseModeFormPost() throws Exception {
        String authorizeUri = UriComponentsBuilder.fromHttpUrl(serverUrl + "/authorize")
            .queryParam("response_type", "id_token")
            .queryParam("client_id", TEST_CLIENT)
            .queryParam("scope", scope)
            .queryParam("redirect_uri", callback)
            .queryParam("state", "preserveState")
            .queryParam("response_mode", "form_post")
            .build().toUriString();
        ResponseEntity<String> response = template.exchange(authorizeUri, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
        assertEquals(200, response.getStatusCode().value());
        String html = response.getBody();

        assertTrue(html.contains("<body onload=\"javascript:document.forms[0].submit()\">"));
        assertTrue(html.contains("<form method=\"post\" action=\"http://localhost:8889/callback\">"));
        assertTrue(html.contains("<input type=\"hidden\" name=\"id_token\" value=\""));
        assertTrue(html.contains("<input type=\"hidden\" name=\"state\" value=\"preserveState\""));
    }


}
