package oidc.service;

import org.mitre.oauth2.service.impl.BlacklistAwareRedirectResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Component;

@Component("customBlacklistAwareRedirectResolver")
@Primary
public class CustomBlacklistAwareRedirectResolver extends BlacklistAwareRedirectResolver {

    private static final Logger LOG = LoggerFactory.getLogger(CustomBlacklistAwareRedirectResolver.class);

    @Override
    public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {
        try {
            return super.resolveRedirect(requestedRedirect, client);
        } catch (Exception e) {
            LOG.error("Exception for resolveRedirect", e);
            throw e;
        }

    }
}
