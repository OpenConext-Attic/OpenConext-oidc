package oidc.user;

import oidc.repository.FederatedUserInfoRepository;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.impl.DefaultUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DefaultFederatedUserInfoService extends DefaultUserInfoService implements FederatedUserInfoService {

  @Autowired
  private FederatedUserInfoRepository userInfoRepository;

  @Override
  public UserInfo saveUserInfo(UserInfo userInfo) {
    return userInfoRepository.saveUserInfo(userInfo);
  }

  @Override
  public UserInfo getByUsernameAndClientId(String username, String clientId) {
    return getByUsername(username);
  }

  public void setUserInfoRepository(FederatedUserInfoRepository userInfoRepository) {
    this.userInfoRepository = userInfoRepository;
  }
}
