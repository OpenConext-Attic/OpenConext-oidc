package oidc.saml;

import oidc.ExtendedUserInfoRepository;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.impl.JpaUserInfoRepository;
import org.mitre.util.jpa.JpaUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Primary
@Repository("jpaSamlUserInfoRepository")
@Transactional(value="defaultTransactionManager")
public class SamlUserInfoRepository extends JpaUserInfoRepository implements ExtendedUserInfoRepository {

  @PersistenceContext(unitName = "defaultPersistenceUnit")
  private EntityManager manager;

  @Override
  public UserInfo saveUserInfo(UserInfo userInfo) {
    if (!(userInfo instanceof DefaultUserInfo)) {
      throw new RuntimeException("Can only persist DefaultUserInfo instances. Not " + userInfo.getClass().getName());
    }
    DefaultUserInfo defaultUserInfo = (DefaultUserInfo) userInfo;
    return JpaUtil.saveOrUpdate(defaultUserInfo.getId(), manager, defaultUserInfo);
  }
}
