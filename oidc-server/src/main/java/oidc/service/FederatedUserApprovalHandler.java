package oidc.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Primary
@Component("federatedUserApprovalHandler")
public class FederatedUserApprovalHandler implements UserApprovalHandler {

  @Override
  public boolean isApproved(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
    return true;
  }

  @Override
  public AuthorizationRequest checkForPreApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
    return authorizationRequest;
  }

  @Override
  public AuthorizationRequest updateAfterApproval(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
    return authorizationRequest;
  }

  @Override
  public Map<String, Object> getUserApprovalRequest(AuthorizationRequest authorizationRequest, Authentication userAuthentication) {
    return Collections.EMPTY_MAP;
  }
}
