package oidc.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultHashedPairwiseIdentifierServiceTest {

  private HashedPairwiseIdentifierService identifierService = new DefaultHashedPairwiseIdentifierService();

  @Test
  public void testGetIdentifier() throws Exception {
    String identifier = identifierService.getIdentifier("urn:collab:person:example.com:local", "https://mock-sp");
    String another = identifierService.getIdentifier("urn:collab:person:example.com:local", "https://mock-sp");
    assertEquals(identifier, another);
  }

  @Test
  public void getIdentifierUrn() throws Exception {
    String identifier = "urn:collab:person:example.com:mary.steward" + "_" + "https@//oidc.test.surfconext.nl";
    String[] identifierSplit= identifier.split("_");
    assertEquals("5e491345-653b-362d-884f-a6017b65b214", identifierService.getIdentifier(identifierSplit[0], identifierSplit[1]));

    String identifierOther = "urn:collab:person:example.com:mary.steward2" + "_" + "https@//oidc.test.surfconext.nl";
    String[] identifierOtherSplit= identifierOther.split("_");
    assertEquals("d176b156-3e5b-3817-afe5-53fd62c19374", identifierService.getIdentifier(identifierOtherSplit[0], identifierOtherSplit[1]));
  }

  @Test
  public void testIdentifierInsufficientLength() throws Exception {
    String identifier = identifierService.getIdentifier("urn", "s");
    String another = identifierService.getIdentifier("urn", "s");
    assertEquals(identifier, another);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIdentifierClientIdNull() throws Exception {
    identifierService.getIdentifier("urn", null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIdentifierUrnNull() throws Exception {
    identifierService.getIdentifier(null, "clientId");
  }
}