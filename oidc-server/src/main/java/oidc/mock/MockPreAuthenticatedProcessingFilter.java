package oidc.mock;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import oidc.saml.SAMLUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

public class MockPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
      return new SAMLUser("urn:collab:person:example.com:admin", "surfnet.nl", "John Doe", "http://mock-idp", "john.doe@example.org");
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  @Override
  public UserDetails loadUserDetails(final PreAuthenticatedAuthenticationToken authentication) throws UsernameNotFoundException {
    return (SAMLUser) authentication.getPrincipal();
  }

}
