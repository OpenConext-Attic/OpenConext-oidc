package oidc.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.mitre.openid.connect.service.ScopeClaimTranslationService;
import org.mitre.openid.connect.service.impl.DefaultScopeClaimTranslationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Set;

@Primary
@Service("federationScopeClaimTranslationService")
public class FederationScopeClaimTranslationService extends DefaultScopeClaimTranslationService implements ScopeClaimTranslationService {

  private SetMultimap<String, String> federatedScopesToClaims = HashMultimap.create();

  public FederationScopeClaimTranslationService() {
    super();

    federatedScopesToClaims.put("organization", "schac_home_organization");
    federatedScopesToClaims.put("organization", "schac_home_organization_type");
    federatedScopesToClaims.put("organization", "edu_person_affiliation");
    federatedScopesToClaims.put("organization", "edu_person_scoped_affiliation");
    federatedScopesToClaims.put("organization", "is_member_of");

    federatedScopesToClaims.put("entitlement", "edu_person_affiliation");

    federatedScopesToClaims.put("userids", "schac_personal_unique_code");
    federatedScopesToClaims.put("userids", "edu_person_principal_name");
    federatedScopesToClaims.put("userids", "uid");
    federatedScopesToClaims.put("userids", "edu_person_targeted_id");
  }

  @Override
  public Set<String> getClaimsForScope(String scope) {
    Set<String> claims = super.getClaimsForScope(scope);
    addScope(scope, claims);
    return claims;
  }

  @Override
  public Set<String> getClaimsForScopeSet(Set<String> scopes) {
    Set<String> claims = super.getClaimsForScopeSet(scopes);
    for (String scope : scopes) {
      addScope(scope, claims);
    }
    return claims;
  }

  private void addScope(String scope, Set<String> claims) {
    Set<String> federatedClaims = federatedScopesToClaims.get(scope);
    if (federatedClaims != null) {
      claims.addAll(federatedClaims);
    }
  }

}
