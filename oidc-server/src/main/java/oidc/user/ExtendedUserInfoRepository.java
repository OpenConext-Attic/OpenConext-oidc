package oidc.user;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.mitre.util.jpa.JpaUtil;

public interface ExtendedUserInfoRepository extends UserInfoRepository {

  UserInfo saveUserInfo(UserInfo userInfo);

}
