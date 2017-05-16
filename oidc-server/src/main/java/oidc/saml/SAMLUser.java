package oidc.saml;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;

public class SAMLUser extends User {

    private final String clientId;

    public SAMLUser(String sub, boolean isAdmin, String clientId) {
    super(
        sub,
        "N/A",
        isAdmin ?
            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN")) :
            Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))
    );
    this.clientId = clientId;
  }

    public String getClientId() {
        return clientId;
    }
}
