package oidc.saml;

import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.util.Assert;

public class FederatedSAMLAuthenticationProvider extends SAMLAuthenticationProvider {

  @Override
  protected Object getPrincipal(SAMLCredential credential, Object userDetail) {
    Assert.isInstanceOf(SAMLUser.class, userDetail);
    return ((SAMLUser)userDetail).getUsername();
  }
}
