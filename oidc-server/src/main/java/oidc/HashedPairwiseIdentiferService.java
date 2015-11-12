package oidc;

import org.apache.commons.codec.digest.DigestUtils;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.PairwiseIdentiferService;

public class HashedPairwiseIdentiferService implements PairwiseIdentiferService {
  @Override
  public String getIdentifier(UserInfo userInfo, ClientDetailsEntity client) {
    String identifier = client.getClientId() + "_" + userInfo.getSub();
    return DigestUtils.md5Hex(identifier);
  }
}
