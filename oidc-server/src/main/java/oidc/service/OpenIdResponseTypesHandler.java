package oidc.service;

import org.apache.commons.collections.CollectionUtils;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.mitre.oauth2.service.SystemScopeService;
import org.mitre.openid.connect.service.OIDCTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.response.DefaultResponseTypesHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service("openIdResponseTypesHandler")
public class OpenIdResponseTypesHandler extends DefaultResponseTypesHandler {

    private OIDCTokenService connectTokenService;

    private ClientDetailsEntityService clientService;

    private Set<String> idTokenResponseType = Collections.singleton("id_token");

    @Autowired
    public OpenIdResponseTypesHandler(CompositeTokenGranter tokenGranter, OAuth2RequestFactory oAuth2RequestFactory,
                                      OIDCTokenService connectTokenService, ClientDetailsEntityService clientService) {
        super(tokenGranter, oAuth2RequestFactory);
        this.connectTokenService = connectTokenService;
        this.clientService = clientService;
    }

    @Override
    public boolean canHandleResponseTypes(Set<String> responseTypes) {
        return idTokenResponseType.contains("id_token") || super.canHandleResponseTypes(responseTypes);
    }


    @Override
    public ModelAndView handleApprovedAuthorizationRequest(Set<String> responseTypes, AuthorizationRequest
        authorizationRequest, Authentication authentication, AuthorizationCodeServices authorizationCodeServices) {
        if (!responseTypes.contains("id_token")) {
            return super.handleApprovedAuthorizationRequest(responseTypes, authorizationRequest, authentication,
                authorizationCodeServices);
        }
        String clientId = authorizationRequest.getClientId();
        Set<String> scopes = authorizationRequest.getScope();
        if (CollectionUtils.isEmpty(scopes) || !scopes.contains(SystemScopeService.OPENID_SCOPE)) {
            throw new InvalidScopeException("Invalid scope. Required:" + SystemScopeService.OPENID_SCOPE, scopes);
        }
        ClientDetailsEntity client = clientService.loadClientByClientId(clientId);
        String redirectUri = authorizationRequest.getRedirectUri();
        OAuth2Request request = authorizationRequest.createOAuth2Request();

        OAuth2AccessTokenEntity accessToken = responseTypes.contains("token") ?
            (OAuth2AccessTokenEntity) super.createOAuth2AccessToken(authorizationRequest) : new OAuth2AccessTokenEntity();
        OAuth2AccessTokenEntity idToken = connectTokenService.createIdToken(client, request, new Date(),
            authentication.getName(), accessToken);

        String responseMode = authorizationRequest.getRequestParameters().get("response_mode");
        if (StringUtils.hasText(responseMode) && responseMode.equalsIgnoreCase("form_post")) {
            return formPostView(accessToken, responseTypes, authorizationRequest, redirectUri, idToken);
        } else {
            return fragmentRedirectView(accessToken, responseTypes, authorizationRequest, redirectUri, idToken);
        }
    }


    private ModelAndView fragmentRedirectView(OAuth2AccessTokenEntity accessToken, Set<String> responseTypes,
                                              AuthorizationRequest authorizationRequest, String redirectUri,
                                              OAuth2AccessTokenEntity idToken) {
        String fragment = "id_token=" + idToken.getValue();
        if (responseTypes.contains("token")) {
            fragment = fragment.concat("&access_token=" + accessToken.getValue());
        }
        String redirect = fromHttpUrl(redirectUri).fragment(fragment).build().toUriString();
        String state = authorizationRequest.getRequestParameters().get("state");
        if (StringUtils.hasText(state)) {
            try {
                redirect = redirect + "&state=" + UriUtils.encodeQueryParam(state, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return new ModelAndView(new RedirectView(redirect, false, true, false));
    }

    private ModelAndView formPostView(OAuth2AccessTokenEntity accessToken, Set<String> responseTypes,
                                      AuthorizationRequest authorizationRequest, String redirectUri,
                                      OAuth2AccessTokenEntity idToken) {
        String state = authorizationRequest.getRequestParameters().get("state");

        Map<String, String> model = new HashMap<>();
        model.put("redirect_uri", redirectUri);
        model.put("state", state);
        model.put("id_token", idToken.getValue());
        if (responseTypes.contains("token")) {
            model.put("access_token", accessToken.getValue());
        }

        return new ModelAndView("form_post", model);
    }
}
