package oidc.saml;

import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

import java.util.Collection;
import java.util.Set;

public class ExtendedWebSSOProfileOptions extends WebSSOProfileOptions {

    private WebSSOProfileOptions options;
    private String idpSingleSignOn;

    public ExtendedWebSSOProfileOptions(WebSSOProfileOptions options, String idpSingleSignOn) {
        this.options = options;
        this.idpSingleSignOn = idpSingleSignOn;
    }

    public String getIdpSingleSignOn() {
        return idpSingleSignOn;
    }

    @Override
    public String getBinding() {
        return this.options.getBinding();
    }

    @Override
    public Boolean getPassive() {
        return this.options.getPassive();
    }

    @Override
    public Boolean getForceAuthN() {
        return this.options.getForceAuthN();
    }

    @Override
    public Boolean isIncludeScoping() {
        return this.options.isIncludeScoping();
    }

    @Override
    public Integer getProxyCount() {
        return this.options.getProxyCount();
    }

    @Override
    public Collection<String> getAuthnContexts() {
        return this.options.getAuthnContexts();
    }

    @Override
    public String getNameID() {
        return this.options.getNameID();
    }

    @Override
    public Boolean isAllowCreate() {
        return this.options.isAllowCreate();
    }

    @Override
    public AuthnContextComparisonTypeEnumeration getAuthnContextComparison() {
        return this.options.getAuthnContextComparison();
    }

    @Override
    public Set<String> getAllowedIDPs() {
        return this.options.getAllowedIDPs();
    }

    @Override
    public String getProviderName() {
        return this.options.getProviderName();
    }

    @Override
    public Integer getAssertionConsumerIndex() {
        return this.options.getAssertionConsumerIndex();
    }

    @Override
    public String getRelayState() {
        return this.options.getRelayState();
    }

    @Override
    public Set<String> getRequesterIds() {
        return this.options.getRequesterIds();
    }
}
