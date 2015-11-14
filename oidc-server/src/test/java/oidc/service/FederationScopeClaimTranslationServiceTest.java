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
  public void testGetClaimsForScopeSet() throws Exception {
    Set claims = subject.getClaimsForScopeSet(new HashSet(Arrays.asList("organization", "userids", "userids")));
    Set<String> expected = new HashSet<>(Arrays.asList(
        "schac_home_organization",
        "uid",
        "edu_person_principal_name",
        "edu_person_affiliation",
        "edu_person_scoped_affiliation",
        "edu_person_targeted_id",
        "schac_home_organization_type",
        "schac_personal_unique_code",
        "is_member_of"));

    assertEquals(expected, claims);

  }

}