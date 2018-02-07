package oidc.saml;

import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

import java.util.Collection;
import java.util.Set;

public class ExtendedWebSSOProfileOptions extends WebSSOProfileOptions {

    private WebSSOProfileOptions options;
    private String idpSingleSignOn;

    public ExtendedWebSSOProfileOptions(WebSSOProfileOptions options, String idpSingleSignOn) {
        setBinding(options.getBinding());
        setPassive(options.getPassive());
        setForceAuthN(options.getForceAuthN());
        setIncludeScoping(options.isIncludeScoping());
        setProxyCount(options.getProxyCount());
        setAuthnContexts(options.getAuthnContexts());
        setNameID(options.getNameID());
        setAllowCreate(options.isAllowCreate());
        setAuthnContextComparison(options.getAuthnContextComparison());
        setAllowedIDPs(options.getAllowedIDPs());
        setProviderName(options.getProviderName());
        setAssertionConsumerIndex(options.getAssertionConsumerIndex());
        setRelayState(options.getRelayState());
        setRequesterIds(options.getRequesterIds());
        this.idpSingleSignOn = idpSingleSignOn;
    }

    public String getIdpSingleSignOn() {
        return idpSingleSignOn;
    }

}
