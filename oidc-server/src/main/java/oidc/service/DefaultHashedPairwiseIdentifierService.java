package oidc.service;

import oidc.model.FederatedUserInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.nio.ByteBuffer;
import java.util.UUID;

@Primary
@Service("hashedPairwiseIdentifierService")
public class DefaultHashedPairwiseIdentifierService implements HashedPairwiseIdentifierService {

  @Override
  public String getIdentifier(UserInfo userInfo, ClientDetailsEntity client) {
    Assert.isInstanceOf(FederatedUserInfo.class, userInfo);
    return getIdentifier(((FederatedUserInfo) userInfo).getUnspecifiedNameId(), client.getClientId());
  }

  @Override
  public String getIdentifier(String unspecifiedNameId, String clientId) {
    Assert.notNull(unspecifiedNameId, "unspecifiedNameId is null");
    Assert.notNull(clientId, "clientId is null");
    String identifier = unspecifiedNameId + "_" + clientId;
    if (identifier.length() < 16) {
      //otherwise the bb.getLong fails
      identifier = String.format("%-16s", identifier).replace(' ', '*');
    }
    ByteBuffer bb = ByteBuffer.wrap(identifier.getBytes());
    return new UUID(bb.getLong(), bb.getLong()).toString();
  }

}
