package oidc.security;

import oidc.saml.SAMLUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ClientIdFilter extends GenericFilterBean {

    public static final String BLACK_HOLE_ATTRIBUTE = "BLACK_HOLE_ATTRIBUTE";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        String servletPath = request.getServletPath();

        if (servletPath != null && servletPath.endsWith("authorize")) {
            if (currentAuthentication != null && currentAuthentication.getDetails() instanceof SAMLUser) {
                if (!isBlackHole(request)) {
                    SecurityContextHolder.clearContext();
                    request.getSession().setAttribute(BLACK_HOLE_ATTRIBUTE, Boolean.TRUE);
                } else {
                    request.getSession().removeAttribute(BLACK_HOLE_ATTRIBUTE);
                }
            } else {
                request.getSession().setAttribute(BLACK_HOLE_ATTRIBUTE, Boolean.TRUE);
            }
        }

        String clientId = request.getParameter("client_id");

        if (currentAuthentication != null && currentAuthentication.getDetails() instanceof SAMLUser) {
            SAMLUser samlUser = SAMLUser.class.cast(currentAuthentication.getDetails());
            if (StringUtils.hasText(clientId) && !samlUser.getClientId().equals(clientId)) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(servletRequest, response);
    }

    boolean isBlackHole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Boolean blackHole = (Boolean) session.getAttribute(BLACK_HOLE_ATTRIBUTE);
            return blackHole != null && blackHole.booleanValue();
        }
        return false;
    }
}
