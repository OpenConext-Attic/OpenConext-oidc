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

  private final String unspecifiedNameId;
  private final String schacHomeOrganization;

  public SAMLUser(String sub, String unspecifiedNameId, String schacHomeOrganization) {
    super(sub, "N/A", Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")));
    this.unspecifiedNameId = unspecifiedNameId;
    this.schacHomeOrganization = schacHomeOrganization;
  }

  public String getSchacHomeOrganization() {
    return schacHomeOrganization;
  }

  public String getUnspecifiedNameId() {
    return unspecifiedNameId;
  }
}
