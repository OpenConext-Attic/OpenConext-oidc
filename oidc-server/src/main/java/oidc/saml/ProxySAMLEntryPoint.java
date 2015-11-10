package oidc.saml;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ProxySAMLEntryPoint extends SAMLEntryPoint {

  @Autowired
  private ClientDetailsEntityService clientDetailsEntityService;

  @Autowired
  private Environment environment;

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
    //TODO get the client_id and store the SP linked to the client
    if (environment.acceptsProfiles("local")) {
      return;
    }
    String clientId = request.getParameter("client_id");
    if (StringUtils.hasText(clientId)) {
      ClientDetailsEntity clientDetails = clientDetailsEntityService.loadClientByClientId(clientId);
      Set<String> acRvalues = clientDetails.getDefaultACRvalues();
    }
    super.commence(request, response, e);
  }

  @Override
  protected WebSSOProfileOptions getProfileOptions(SAMLMessageContext context, AuthenticationException exception) throws MetadataProviderException {
    WebSSOProfileOptions profileOptions = super.getProfileOptions(context, exception);
    profileOptions.setIncludeScoping(true);
    //TODO set the requester ID
    profileOptions.setRequesterIds(new HashSet<String>());
    return profileOptions;
  }
}
