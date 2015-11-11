package oidc.mock;

import oidc.saml.SAMLUser;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class MockAuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  @Override
  public UserDetails loadUserDetails(final PreAuthenticatedAuthenticationToken authentication) throws UsernameNotFoundException {
    return (SAMLUser) authentication.getPrincipal();
  }

}
