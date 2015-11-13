package oidc.service;

import oidc.model.FederatedUserInfo;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.impl.DefaultIntrospectionResultAssembler;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Service
@Primary
public class ExtendedIntrospectionResultAssembler extends DefaultIntrospectionResultAssembler {

  @Override
  public Map<String, Object> assembleFrom(OAuth2AccessTokenEntity accessToken, UserInfo userInfo, Set<String> authScopes) {
    Map<String, Object> result = super.assembleFrom(accessToken, userInfo, authScopes);
    if (userInfo != null && userInfo instanceof FederatedUserInfo) {
      FederatedUserInfo federatedUserInfo = (FederatedUserInfo) userInfo;
      result.put("schac_home", federatedUserInfo.getSchacHomeOrganization());
      result.put("unspecified_id", federatedUserInfo.getUnspecifiedNameId());
    }
    return result;
  }
}
