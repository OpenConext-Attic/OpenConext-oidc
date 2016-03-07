package oidc.service;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FederationScopeClaimTranslationServiceTest {

  private FederationScopeClaimTranslationService subject = new FederationScopeClaimTranslationService();

  @Test
  public void testGetEmptyClaimsForScopeSet() throws Exception {
    Set claims = subject.getClaimsForScopeSet(new HashSet(Arrays.asList("bogus")));
    assertTrue(claims.isEmpty());
  }

  @Test
  public void testGetEmptyClaimsForScope() throws Exception {
    Set claims = subject.getClaimsForScope("bogus");
    assertTrue(claims.isEmpty());
  }

  @Test
  public void testGetClaimsForScopeSet() throws Exception {
    Set claims = subject.getClaimsForScopeSet(new HashSet(Arrays.asList("bogus", "openid")));
    assertEquals(subject.allClaims(), claims);
  }

  @Test
  public void testGetClaimsForScope() throws Exception {
    Set claims = subject.getClaimsForScope("openid");
    assertEquals(subject.allClaims(), claims);
  }
}