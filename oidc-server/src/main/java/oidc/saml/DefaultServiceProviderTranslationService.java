package oidc.saml;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("defaultServiceProviderTranslationService")
public class DefaultServiceProviderTranslationService implements ServiceProviderTranslationService{

  @Override
  public String translateServiceProviderEntityId(String entityId) {
    Assert.notNull(entityId);
    return entityId.replace("@","@@").replaceAll(":","@");
  }

  @Override
  public String translateClientId(String clientId) {
    Assert.notNull(clientId);
    return clientId.replaceAll("(?<!@)@(?!@)", ":").replaceAll("@@","@");
  }

}
