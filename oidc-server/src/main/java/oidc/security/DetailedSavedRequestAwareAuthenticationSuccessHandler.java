package oidc.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DetailedSavedRequestAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DetailedSavedRequestAwareAuthenticationSuccessHandler.class);

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String sessionId = session != null ? session.getId() : "session is null";
        LOG.info("Session ID {} after successful authentication for {} ", sessionId, authentication.getPrincipal());

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            LOG.info("There are no cookies after successful authentication");
        } else {
            LOG.info("Cookies after successful authentication: " +
                    Stream.of(cookies).map(c -> cookieToString(c)).collect(Collectors.joining(",")));
        }
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest == null) {
            LOG.warn("Could not find saved request in session {} after successful authentication for {}", sessionId, authentication.getName());
        }
        String targetUrlParameter = getTargetUrlParameter();
        if (isAlwaysUseDefaultTargetUrl() || (targetUrlParameter != null && StringUtils.hasText(request.getParameter(targetUrlParameter)))) {
            LOG.warn(String.format("TargetUrlParameter %s is used. Will generate a 404", targetUrlParameter));
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    public static String cookieToString(Cookie cookie) {
        return String.format("Name %s, Domain %s, Value %s, HttpOnly %s, Secure %s,",
                cookie.getName(),
                cookie.getDomain(),
                cookie.getValue(),
                cookie.isHttpOnly(),
                cookie.getSecure());
    }
}

