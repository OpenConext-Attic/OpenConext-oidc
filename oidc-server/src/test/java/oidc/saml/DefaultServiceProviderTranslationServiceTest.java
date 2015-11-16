package oidc.saml;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultServiceProviderTranslationServiceTest {

  private DefaultServiceProviderTranslationService translationService = new DefaultServiceProviderTranslationService();

  @Test
  public void testTranslates() throws Exception {
    String spEntityId = "https://urn:some@user:com";

    String clientId = translationService.translateServiceProviderEntityId(spEntityId);
    assertEquals("https@//urn@some@@user@com", clientId);

    String toEntityIdAgain = translationService.translateClientId(clientId);
    assertEquals(spEntityId, toEntityIdAgain);
  }

}