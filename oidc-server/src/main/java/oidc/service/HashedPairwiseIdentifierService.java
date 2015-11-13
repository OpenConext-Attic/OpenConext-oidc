package oidc.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.PairwiseIdentiferService;

public interface HashedPairwiseIdentifierService extends PairwiseIdentiferService {

  String getIdentifier(String unspecifiedNameId, String clientId);
}
