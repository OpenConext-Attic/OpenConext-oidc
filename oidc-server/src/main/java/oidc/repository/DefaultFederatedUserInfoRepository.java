package oidc.repository;

import oidc.model.FederatedUserInfo;
import oidc.repository.FederatedUserInfoRepository;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.impl.JpaUserInfoRepository;
import org.mitre.util.jpa.JpaUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.List;

import static org.mitre.util.jpa.JpaUtil.getSingleResult;

@Primary
@Repository("jpaDefaultFederatedUserInfoRepository")
@Transactional(value="defaultTransactionManager")
public class DefaultFederatedUserInfoRepository implements FederatedUserInfoRepository {

  @PersistenceContext(unitName = "defaultPersistenceUnit")
  private EntityManager manager;

  @Override
  public UserInfo saveUserInfo(UserInfo userInfo) {
    if (!(userInfo instanceof FederatedUserInfo)) {
      throw new RuntimeException("Can only persist FederatedUserInfo instances. Not " + userInfo.getClass().getName());
    }
    FederatedUserInfo federatedUserInfo = (FederatedUserInfo) userInfo;
    return JpaUtil.saveOrUpdate(federatedUserInfo.getId(), manager, federatedUserInfo);
  }

  @Override
  public UserInfo getByUsername(String sub) {
    TypedQuery<FederatedUserInfo> query = manager.createQuery("select u from FederatedUserInfo u WHERE u.sub = :sub", FederatedUserInfo.class);
    query.setParameter("sub", sub);
    return getSingleResult(query.getResultList());
  }

  @Override
  public UserInfo getByEmailAddress(String email) {
    TypedQuery<FederatedUserInfo> query = manager.createQuery("select u from FederatedUserInfo u WHERE u.email = :email", FederatedUserInfo.class);
    query.setParameter("email", email);
    List<FederatedUserInfo> results = query.getResultList();
    //does not matter here, email is not unique within our system
    return results.isEmpty() ? null : results.get(0);
  }

}
