package oidc.service;

import org.mitre.openid.connect.service.ScopeClaimTranslationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Primary
@Service("federationScopeClaimTranslationService")
public class FederationScopeClaimTranslationService implements ScopeClaimTranslationService {

    private final Set<String> claims = new HashSet<>();
    private final Set<String> noClaims = new HashSet<>();

    public FederationScopeClaimTranslationService() {
        claims.add("sub");

        claims.add("name");
        claims.add("preferred_username");
        claims.add("given_name");
        claims.add("family_name");
        claims.add("middle_name");
        claims.add("nickname");
        claims.add("profile");
        claims.add("picture");
        claims.add("website");
        claims.add("gender");
        claims.add("zoneinfo");
        claims.add("locale");
        claims.add("updated_at");
        claims.add("birthdate");

        claims.add("email");
        claims.add("email_verified");

        claims.add("phone_number");
        claims.add("phone_number_verified");

        claims.add("address");

        claims.add("schac_home_organization");
        claims.add("schac_home_organization_type");
        claims.add("edu_person_scoped_affiliations");
        claims.add("edumember_is_member_of");

        claims.add("edu_person_affiliations");
        claims.add("eduperson_entitlement");

        claims.add("schac_personal_unique_codes");
        claims.add("edu_person_principal_name");
        claims.add("uids");
        claims.add("edu_person_targeted_id");

    }

    @Override
    public Set<String> getClaimsForScope(String scope) {
        return "openid".equals(scope) ? claims : noClaims;
    }

    @Override
    public Set<String> getClaimsForScopeSet(Set<String> scopes) {
        return scopes.contains("openid") ? claims : noClaims;
    }

    protected Set<String> allClaims() {
        return claims;
    }

}
