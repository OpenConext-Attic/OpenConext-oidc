package oidc.saml;

import org.springframework.security.saml.context.SAMLContextProviderImpl;
import org.springframework.security.saml.storage.HttpSessionStorageFactory;
import org.springframework.security.saml.storage.SAMLMessageStorageFactory;

public class ConcurrentSAMLContextProviderImpl extends SAMLContextProviderImpl {

    public ConcurrentSAMLContextProviderImpl() {
        super();
        super.storageFactory = new ConcurrentSAMLMessageStorageFactory();
    }
}
