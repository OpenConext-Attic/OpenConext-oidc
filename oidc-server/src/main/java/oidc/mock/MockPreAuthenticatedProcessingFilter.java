package oidc.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import oidc.model.FederatedUserInfo;
import oidc.saml.SAMLUser;
import oidc.user.FederatedUserInfoService;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MockPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  private static final String CLIENT_ID = "https@//oidc.localhost.surfconext.nl";

  @Autowired
  private FederatedUserInfoService extendedUserInfoService;

  private FederatedUserInfo federatedUserInfo;

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    UserInfo existingUserInfo = extendedUserInfoService.getByUsernameAndClientId(federatedUserInfo.getSub(), CLIENT_ID);

    if (existingUserInfo == null) {
      extendedUserInfoService.saveUserInfo(federatedUserInfo);
    }

    return new SAMLUser(federatedUserInfo.getSub(), true);
  }


  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      this.federatedUserInfo = objectMapper.readValue(new ClassPathResource("model/federated_user_info.json").getInputStream(), FederatedUserInfo.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
