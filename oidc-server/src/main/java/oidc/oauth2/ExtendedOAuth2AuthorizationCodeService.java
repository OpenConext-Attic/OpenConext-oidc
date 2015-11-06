package oidc.oauth2;

import oidc.shibboleth.ShibbolethUser;
import org.mitre.oauth2.model.AuthenticationHolderEntity;
import org.mitre.oauth2.model.AuthorizationCodeEntity;
import org.mitre.oauth2.repository.AuthenticationHolderRepository;
import org.mitre.oauth2.repository.AuthorizationCodeRepository;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Primary
@Service("extendedOAuth2AuthorizationCodeService")
public class ExtendedOAuth2AuthorizationCodeService extends DefaultOAuth2AuthorizationCodeService {

  private RandomValueStringGenerator generator = new RandomValueStringGenerator();

  @Autowired
  private AuthorizationCodeRepository repository;

  @Autowired
  private AuthenticationHolderRepository authenticationHolderRepository;

  /*
   * We need to store information from the ShibbolethUser in the database and this is the hook,
   * but the extensibility of MITREid is poor, so we ended up copy & pasting code
   */
  @Override
  public String createAuthorizationCode(OAuth2Authentication authentication) {
    String code = generator.generate();

    // attach the authorization so that we can look it up later
    AuthenticationHolderEntity authHolder = new AuthenticationHolderEntity();
    authHolder.setAuthentication(authentication);

    Map<String, Serializable> extensions = addExtensions(authentication, authHolder);

    authHolder.setExtensions(extensions);
    authHolder = authenticationHolderRepository.save(authHolder);

    // set the auth code to expire
    Date expiration = new Date(System.currentTimeMillis() + (getAuthCodeExpirationSeconds() * 1000L));

    AuthorizationCodeEntity entity = new AuthorizationCodeEntity(code, authHolder, expiration);
    repository.save(entity);

    return code;
  }

  private Map<String, Serializable> addExtensions(OAuth2Authentication authentication, AuthenticationHolderEntity authHolder) {
    Map<String, Serializable> extensions = authHolder.getExtensions();
    Object principal = authentication.getPrincipal();
    if (principal instanceof ShibbolethUser) {
      ShibbolethUser shibbolethUser = (ShibbolethUser) principal;
      if (extensions == null) {
        extensions = new HashMap<>();
      }
      extensions.put("schac_home", shibbolethUser.getSchacHomeOrganization());
      extensions.put("display_name", shibbolethUser.getDisplayName());
      extensions.put("email", shibbolethUser.getEmail());
    }
    return extensions;
  }
}
