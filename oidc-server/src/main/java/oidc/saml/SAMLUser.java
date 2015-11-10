package oidc.saml;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 *
 */
public class SAMLUser extends User {
  private final String schacHomeOrganization;
  private final String displayName;
  private final String authenticatingAuthority;
  private final String email;

  public SAMLUser(String username, String schacHomeOrganization, String displayName, String authenticatingAuthority, String email) {
    //TODO move authorities out of this class and inject at level where can be decided what to do
    super(username, "N/A", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));
    this.schacHomeOrganization = schacHomeOrganization;
    this.displayName = displayName;
    this.authenticatingAuthority = authenticatingAuthority;
    this.email = email;
  }

  public String getSchacHomeOrganization() {
    return schacHomeOrganization;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getAuthenticatingAuthority() {
    return authenticatingAuthority;
  }

  public String getEmail() {
    return email;
  }
}
