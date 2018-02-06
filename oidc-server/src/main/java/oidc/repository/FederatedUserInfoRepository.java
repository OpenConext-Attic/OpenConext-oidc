package oidc.repository;

import oidc.model.FederatedUserInfo;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.mitre.util.jpa.JpaUtil;

import javax.persistence.EntityManager;
import java.util.Set;

public interface FederatedUserInfoRepository extends UserInfoRepository {

  UserInfo saveUserInfo(UserInfo userInfo);

  Set<FederatedUserInfo> findOrphanedFederatedUserInfos();

  void removeFederatedUserInfo(FederatedUserInfo federatedUserInfo);

  EntityManager getManager();
}
