package oidc.service;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.UUID;

import static org.junit.Assert.*;

public class DefaultHashedPairwiseIdentifierServiceTest {

  private HashedPairwiseIdentifierService identifierService = new DefaultHashedPairwiseIdentifierService();

  @Test
  public void testGetIdentifier() throws Exception {
    String identifier = identifierService.getIdentifier("urn:collab:person:example.com:local", "https://mock-sp");
    String another = identifierService.getIdentifier("urn:collab:person:example.com:local", "https://mock-sp");
    assertEquals(identifier, another);
  }

}