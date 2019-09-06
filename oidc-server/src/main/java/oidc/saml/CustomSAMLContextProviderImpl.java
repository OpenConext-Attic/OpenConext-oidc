package oidc.saml;

import org.springframework.security.saml.context.SAMLContextProviderImpl;

public class CustomSAMLContextProviderImpl extends SAMLContextProviderImpl {

    public CustomSAMLContextProviderImpl() {
        storageFactory = new ConcurrentHttpSessionStorageFactory();
    }
}
