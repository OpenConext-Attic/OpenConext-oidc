package oidc.shibboleth;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

  public static final String COLLAB_PERSON_ID_HEADER_NAME = "name-id";
  public static final String SCHAC_HOME_ORGANIZATION_HEADER_NAME = "schachomeorganization";
  public static final String DISPLAY_NAME_HEADER_NAME = "displayname";
  public static final String PERSISTENT_NAME_ID_PREFIX = "urn:collab:person:";
  public static final String SHIB_AUTHENTICATING_AUTHORITY = "Shib-Authenticating-Authority";

  private static final Logger LOG = LoggerFactory.getLogger(ShibbolethPreAuthenticatedProcessingFilter.class);
  private static final String EMPTY_HEADER_ERROR_TEMPLATE = "Header '%s' must be set";

  private String environment;

  @Override
  protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
    if ("local".equalsIgnoreCase(environment)) {
      return new ShibbolethUser("urn:collab:person:example.com:admin", "surfnet.nl", "John Doe", "http://mock-idp", "john.doe@example.org");
    }
    String uid = request.getHeader(COLLAB_PERSON_ID_HEADER_NAME);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(uid), EMPTY_HEADER_ERROR_TEMPLATE, COLLAB_PERSON_ID_HEADER_NAME);
    Preconditions.checkArgument(uid.startsWith(PERSISTENT_NAME_ID_PREFIX), "Header '%s' must start with '%s'. Actual value is '%'", COLLAB_PERSON_ID_HEADER_NAME, PERSISTENT_NAME_ID_PREFIX, uid);

    String schacHomeOrganization = request.getHeader(SCHAC_HOME_ORGANIZATION_HEADER_NAME);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(schacHomeOrganization), EMPTY_HEADER_ERROR_TEMPLATE, SCHAC_HOME_ORGANIZATION_HEADER_NAME);

    String email = request.getHeader("Shib-InetOrgPerson-mail");

    String displayName = request.getHeader(DISPLAY_NAME_HEADER_NAME);

    String authenticatingAuthorities = request.getHeader(SHIB_AUTHENTICATING_AUTHORITY);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(authenticatingAuthorities), EMPTY_HEADER_ERROR_TEMPLATE, SHIB_AUTHENTICATING_AUTHORITY);
    String authenticatingAuthority = authenticatingAuthorities.split(";")[0];

    ShibbolethUser user = new ShibbolethUser(uid, schacHomeOrganization, displayName, authenticatingAuthority, email);
    LOG.debug("Assembled Shibboleth user from headers: {}", user);
    return user;
  }

  @Override
  protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
    return "N/A";
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }
}
