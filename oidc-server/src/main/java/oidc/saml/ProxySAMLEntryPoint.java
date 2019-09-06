package oidc.saml;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.service.ClientDetailsEntityService;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.context.SAMLMessageContext;
import org.springframework.security.saml.websso.WebSSOProfileOptions;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static oidc.security.DetailedSavedRequestAwareAuthenticationSuccessHandler.cookieToString;

public class ProxySAMLEntryPoint extends SAMLEntryPoint {

    private static final Logger LOG = LoggerFactory.getLogger(ProxySAMLEntryPoint.class);

    protected static final String CLIENT_DETAILS = ProxySAMLEntryPoint.class.getName() + "_CLIENT_DETAILS";
    protected static final String FORCE_AUTHN = ProxySAMLEntryPoint.class.getName() + "_FORCE_AUTHN";
    protected static final String IDP_SINGLE_SIGN_ON = ProxySAMLEntryPoint.class.getName() + "_IDP_SINGLE_SIGN_ON";

    @Autowired
    private ClientDetailsEntityService clientDetailsEntityService;
    @Autowired
    private ServiceProviderTranslationService serviceProviderTranslationService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws
        IOException, ServletException {
        HttpSession session = request.getSession();

        LOG.info("Starting SAML redirect with session: " + (session != null ? session.getId() : "no session"));
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            LOG.info("Starting SAML redirect: there are no cookies");
        } else {
            LOG.info("Cookies: " +
                    Stream.of(cookies).map(c -> cookieToString(c)).collect(Collectors.joining(",")));
        }
        String clientId = request.getParameter("client_id");
        if (StringUtils.hasText(clientId)) {
            ClientDetailsEntity clientDetails = clientDetailsEntityService.loadClientByClientId(clientId);
            if (clientDetails != null) {
                request.setAttribute(CLIENT_DETAILS, clientDetails.getClientId());
            }
        }
        String forceAuthn = request.getParameter("prompt");
        if (StringUtils.hasText(forceAuthn) && forceAuthn.equalsIgnoreCase("login")) {
            request.setAttribute(FORCE_AUTHN, true);
        }
        String idpSingleSignOn = request.getParameter("idp-single-sign-on");
        if (StringUtils.hasText(idpSingleSignOn)) {
            request.setAttribute(IDP_SINGLE_SIGN_ON, idpSingleSignOn);
        }
        doCommence(request, response, e);
    }

    //only here for test override
    protected void doCommence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
        throws IOException, ServletException {
        super.commence(request, response, e);
    }


    @Override
    protected WebSSOProfileOptions getProfileOptions(SAMLMessageContext context, AuthenticationException exception)
        throws MetadataProviderException {
        WebSSOProfileOptions profileOptions = super.getProfileOptions(context, exception);
        String clientId = (String) context.getInboundMessageTransport().getAttribute(CLIENT_DETAILS);
        boolean clientIdCall = StringUtils.hasText(clientId);
        String relayState = clientIdCall ? clientId : context.getLocalEntityId();
        // So it can be picked up when provisioning the user
        profileOptions.setRelayState(relayState);

        Boolean forceAuthn = (Boolean) context.getInboundMessageTransport().getAttribute(FORCE_AUTHN);
        if (forceAuthn != null && forceAuthn) {
            profileOptions.setForceAuthN(true);
        }
        String idpSingleSignOn = (String) context.getInboundMessageTransport().getAttribute(IDP_SINGLE_SIGN_ON);
        if (StringUtils.hasText(idpSingleSignOn)) {
            profileOptions = new ExtendedWebSSOProfileOptions(profileOptions, idpSingleSignOn);
        }

        // We are a trusted proxy and scoping will be done by EB on WAYF and ARP
        if (clientIdCall) {
            profileOptions.setIncludeScoping(true);
            String spEntityId = serviceProviderTranslationService.translateClientId(clientId);
            profileOptions.setRequesterIds(new HashSet<>(Arrays.asList(spEntityId)));
        }
        return profileOptions;
    }

    public void setServiceProviderTranslationService(ServiceProviderTranslationService
                                                         serviceProviderTranslationService) {
        this.serviceProviderTranslationService = serviceProviderTranslationService;
    }

    public void setClientDetailsEntityService(ClientDetailsEntityService clientDetailsEntityService) {
        this.clientDetailsEntityService = clientDetailsEntityService;
    }
}
