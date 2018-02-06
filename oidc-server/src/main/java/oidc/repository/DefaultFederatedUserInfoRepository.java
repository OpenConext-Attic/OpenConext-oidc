package oidc.repository;

import oidc.model.FederatedUserInfo;
import oidc.repository.FederatedUserInfoRepository;
import org.apache.commons.lang.StringUtils;
import org.mitre.oauth2.model.SavedUserAuthentication;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.repository.impl.JpaUserInfoRepository;
import org.mitre.util.jpa.JpaUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  @Override
  public Set<FederatedUserInfo> findOrphanedFederatedUserInfos() {
    //need to do this because of http://stackoverflow.com/questions/1557085/setting-a-parameter-as-a-list-for-an-in-expression
    Query nativeQuery = manager.createNativeQuery("select sua.name from saved_user_auth sua");
    List namesList = nativeQuery.getResultList();
    String names = StringUtils.join(namesList, ",");
    TypedQuery<FederatedUserInfo> query = manager.createQuery("select u from FederatedUserInfo u where u.sub not in (:names)", FederatedUserInfo.class);
    query.setParameter("names", names);
    List<FederatedUserInfo> resultList = query.getResultList();
    return new HashSet<>(resultList);
  }

  @Override
  public void removeFederatedUserInfo(FederatedUserInfo federatedUserInfo) {
    FederatedUserInfo merged = manager.merge(federatedUserInfo);
    manager.remove(merged);
  }

    public EntityManager getManager() {
        return manager;
    }
}
