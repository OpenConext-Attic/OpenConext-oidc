package oidc.user;

import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.UserInfoService;

public interface ExtendedUserInfoService extends UserInfoService {

  UserInfo saveUserInfo(UserInfo userInfo);

}
