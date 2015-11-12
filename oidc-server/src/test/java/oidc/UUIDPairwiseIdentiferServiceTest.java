package oidc;

import org.junit.Test;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.impl.UUIDPairwiseIdentiferService;

import java.util.Arrays;
import java.util.HashSet;

public class UUIDPairwiseIdentiferServiceTest {

  private UUIDPairwiseIdentiferService pairwiseIdentiferService = new UUIDPairwiseIdentiferService();

  @Test
  public void assertBugMultipleredirectURIs() {
    ClientDetailsEntity client = new ClientDetailsEntity();
    client.setRedirectUris(new HashSet(Arrays.asList("http://redirect/1","http://redirect/2")));
    UserInfo userInfo = new DefaultUserInfo();
    pairwiseIdentiferService.getIdentifier(userInfo, client);
  }

}
