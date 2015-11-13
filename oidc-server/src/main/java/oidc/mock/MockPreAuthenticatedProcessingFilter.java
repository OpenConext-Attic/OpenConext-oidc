package oidc.mock;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import oidc.saml.SAMLUser;
import oidc.service.HashedPairwiseIdentifierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public class MockPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  @Autowired
  private HashedPairwiseIdentifierService hashedPairwiseIdentifierService;

  private static final String UNSPECIFIED_NAMEID = "urn:collab:person:example.com:local";
  private static final String SCHAC_HOMEORGANIZATION = "surfnet.nl";

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
      String sub = hashedPairwiseIdentifierService.getIdentifier(UNSPECIFIED_NAMEID, SCHAC_HOMEORGANIZATION);
      return new SAMLUser(sub, UNSPECIFIED_NAMEID, SCHAC_HOMEORGANIZATION);
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

}
