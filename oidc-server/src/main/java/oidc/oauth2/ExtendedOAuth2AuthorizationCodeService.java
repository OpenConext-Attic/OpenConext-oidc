package oidc.oauth2;

import oidc.shibboleth.ShibbolethUser;
import oidc.user.ExtendedUserInfoService;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

@Primary
@Service("extendedOAuth2AuthorizationCodeService")
public class ExtendedOAuth2AuthorizationCodeService extends DefaultOAuth2AuthorizationCodeService {

  @Autowired
  private ExtendedUserInfoService extendedUserInfoService;

  /*
   * We need to create - if necessary - a UserInfo instance
   */
  @Override
  public String createAuthorizationCode(OAuth2Authentication authentication) {
    String code = super.createAuthorizationCode(authentication);
    this.provisionUserInfo(authentication);
    return code;
  }


  private void provisionUserInfo(OAuth2Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof ShibbolethUser) {
      ShibbolethUser shibbolethUser = (ShibbolethUser) principal;

      UserInfo existingUserInfo = extendedUserInfoService.getByUsername(shibbolethUser.getUsername());
      if (existingUserInfo == null) {
        UserInfo userInfo = new DefaultUserInfo();
        userInfo.setSub(shibbolethUser.getUsername());
        userInfo.setEmail(shibbolethUser.getEmail());
        userInfo.setName(shibbolethUser.getDisplayName());
        userInfo.setPreferredUsername(shibbolethUser.getUsername());
        userInfo.setProfile(shibbolethUser.getSchacHomeOrganization());
        extendedUserInfoService.saveUserInfo(userInfo);
      }
    }
  }
}
