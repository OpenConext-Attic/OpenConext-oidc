package oidc.saml;

import org.opensaml.common.SAMLObjectBuilder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.springframework.security.saml.websso.WebSSOProfileImpl;
import org.springframework.security.saml.websso.WebSSOProfileOptions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SingleSignOnWebSSOProfile extends WebSSOProfileImpl {

    @Override
    protected SingleSignOnService getSingleSignOnService(WebSSOProfileOptions options, IDPSSODescriptor
        idpssoDescriptor, SPSSODescriptor spDescriptor) throws MetadataProviderException {
        SingleSignOnService singleSignOnService = super.getSingleSignOnService(options, idpssoDescriptor, spDescriptor);
        if (options instanceof ExtendedWebSSOProfileOptions) {
            ExtendedWebSSOProfileOptions extendedOptions = ExtendedWebSSOProfileOptions.class.cast(options);
            String location = singleSignOnService.getLocation();
            String idpSingleSignOn = extendedOptions.getIdpSingleSignOn();
            if (!location.endsWith(idpSingleSignOn)) {
                //need to instantiate new one - copy
                SingleSignOnService copy = copySingleSignOnService(singleSignOnService);
                copy.setLocation(location + "/" + idpSingleSignOn);
                return copy;
            }
        }
        return singleSignOnService;
    }

    /**
     * Bit of a hack, but we need to clone the SingleSignOnService as they are referenced and if we change the
     * location it will change it for all subsequent uses
     */
    SingleSignOnService copySingleSignOnService(SingleSignOnService ssoService) {
        SAMLObjectBuilder<SingleSignOnService> builder = (SAMLObjectBuilder<SingleSignOnService>) builderFactory.getBuilder(SingleSignOnService.DEFAULT_ELEMENT_NAME);
        SingleSignOnService copy = builder.buildObject();
        copy.setLocation(ssoService.getLocation());
        copy.setBinding(ssoService.getBinding());
        copy.setDOM(ssoService.getDOM());
        copy.setParent(ssoService.getParent());
        copy.setNoNamespaceSchemaLocation(ssoService.getNoNamespaceSchemaLocation());
        copy.setResponseLocation(ssoService.getResponseLocation());
        copy.setSchemaLocation(ssoService.getSchemaLocation());
        return copy;
    }
}
