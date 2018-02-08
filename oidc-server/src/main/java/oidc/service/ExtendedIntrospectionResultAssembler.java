package oidc.service;

import oidc.model.FederatedUserInfo;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.impl.DefaultIntrospectionResultAssembler;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@Primary
public class ExtendedIntrospectionResultAssembler extends DefaultIntrospectionResultAssembler {

    @Override
    public Map<String, Object> assembleFrom(OAuth2AccessTokenEntity accessToken, UserInfo userInfo, Set<String>
        authScopes) {
        Map<String, Object> result = super.assembleFrom(accessToken, userInfo, authScopes);
        if (userInfo != null && userInfo instanceof FederatedUserInfo) {
            FederatedUserInfo federatedUserInfo = (FederatedUserInfo) userInfo;
            result.put("schac_home", federatedUserInfo.getSchacHomeOrganization());
            result.put("unspecified_id", federatedUserInfo.getUnspecifiedNameId());
            result.put("authenticating_authority", federatedUserInfo.getAuthenticatingAuthority());
            result.put("edu_person_principal_name", federatedUserInfo.getEduPersonPrincipalName());
            result.put("eduperson_entitlement", federatedUserInfo.getEduPersonEntitlements());
            result.put("edumember_is_member_of", federatedUserInfo.getIsMemberOfs());
        }
        return result;
    }
}
