package oidc.saml;

import oidc.AbstractTestIntegration;
import org.junit.Test;

import static oidc.AbstractTestIntegration.*;
import static org.junit.Assert.*;

public class DefaultServiceProviderTranslationServiceTest {

  private ServiceProviderTranslationService translationService = new DefaultServiceProviderTranslationService();

  @Test
  public void testTranslates() throws Exception {
    String spEntityId = translationService.translateClientId(TEST_CLIENT);
    assertEquals("https://oidc.localhost.surfconext.nl", spEntityId);

    String clientId = translationService.translateServiceProviderEntityId(spEntityId);
    assertEquals(TEST_CLIENT, clientId);
  }

}