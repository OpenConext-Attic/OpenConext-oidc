package oidc.saml;

import org.springframework.security.saml.storage.SAMLMessageStorage;
import org.springframework.security.saml.storage.SAMLMessageStorageFactory;

import javax.servlet.http.HttpServletRequest;

public class ConcurrentHttpSessionStorageFactory implements SAMLMessageStorageFactory {
    @Override
    public SAMLMessageStorage getMessageStorage(HttpServletRequest request) {
        return new ConcurrentSAMLMessageStorage(request);
    }
}
