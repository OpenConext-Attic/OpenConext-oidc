package oidc.user.saml;

import oidc.user.ExtendedUserInfoRepository;
import oidc.user.ExtendedUserInfoService;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.mitre.openid.connect.service.impl.DefaultUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class SamlUserInfoService extends DefaultUserInfoService implements ExtendedUserInfoService {

  @Autowired
  private ExtendedUserInfoRepository userInfoRepository;

  @Override
  public UserInfo saveUserInfo(UserInfo userInfo) {
    return userInfoRepository.saveUserInfo(userInfo);
  }
}