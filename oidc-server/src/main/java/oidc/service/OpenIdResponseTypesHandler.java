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
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.response.CustomResponseTypesHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Service("oidcCustomResponseTypesHandler")
public class OpenIdResponseTypesHandler implements CustomResponseTypesHandler {

  @Autowired
  private OIDCTokenService connectTokenService;

  @Autowired
  private ClientDetailsEntityService clientService;

  private Set<String> idTokenResponseType = Collections.singleton("id_token");

  @Override
  public boolean canHandleResponseTypes(Set<String> responseTypes) {
    return idTokenResponseType.equals(responseTypes);
  }

  @Override
  public ModelAndView handleApprovedAuthorizationRequest(AuthorizationRequest authorizationRequest, Authentication authentication) {
    String clientId = authorizationRequest.getClientId();
    Set<String> scopes = authorizationRequest.getScope();
    if (CollectionUtils.isEmpty(scopes) || !scopes.contains(SystemScopeService.OPENID_SCOPE)) {
      throw new InvalidScopeException("Invalid scope. Required:" + SystemScopeService.OPENID_SCOPE, scopes);
    }
    ClientDetailsEntity client = clientService.loadClientByClientId(clientId);
    String redirectUri = authorizationRequest.getRedirectUri();
    OAuth2Request request = authorizationRequest.createOAuth2Request();

    //this is a flaw in the OIDCTokenService as there will be no access token for the id_token flow
    OAuth2AccessTokenEntity accessToken = new OAuth2AccessTokenEntity();
    OAuth2AccessTokenEntity idToken = connectTokenService.createIdToken(client, request, new Date(), authentication.getName(), accessToken);

    String redirect = fromHttpUrl(redirectUri).fragment("id_token=" + idToken.getValue()).build().toUriString();
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
}
