package oidc.security;

import oidc.saml.SAMLUser;
import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

public class ClientIdFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String clientId = request.getParameter("client_id");
        if (currentAuthentication != null && currentAuthentication.getDetails() instanceof SAMLUser) {
            SAMLUser samlUser = SAMLUser.class.cast(currentAuthentication.getDetails());
            if (StringUtils.hasText(clientId) && !samlUser.getClientId().equals(clientId)) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(servletRequest, response);
    }
}
