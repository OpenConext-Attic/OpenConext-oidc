package oidc.security;

import oidc.saml.SAMLUser;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletRequest;

import static oidc.security.ClientIdFilter.BLACK_HOLE_ATTRIBUTE;
import static org.junit.Assert.*;

public class ClientIdFilterTest {

    private ClientIdFilter subject = new ClientIdFilter();

    @Test
    public void clearAuthenticationOnClientIdChange() throws Exception {
        MockHttpServletRequest request  = new MockHttpServletRequest();
        request.setParameter("client_id", "oidc");

        setAuthentication();

        subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void setAuthentication() {
        SAMLUser samlUser = new SAMLUser("sub", false, "different");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(samlUser.getUsername(), "N/A");
        authentication.setDetails(samlUser);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void doNotClearAuthenticationOnNewLogin() throws Exception {
        MockHttpServletRequest request  = new MockHttpServletRequest();
        request.setServletPath("/authorize");

        subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        boolean isBlackHole = Boolean.class.cast(request.getSession().getAttribute(BLACK_HOLE_ATTRIBUTE)).booleanValue();
        assertTrue(isBlackHole);

        setAuthentication();
        subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());

        assertNull(request.getSession().getAttribute(BLACK_HOLE_ATTRIBUTE));
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void clearAuthenticationWhenReAuthenticating() throws Exception {
        setAuthentication();

        MockHttpServletRequest request  = new MockHttpServletRequest();
        request.setServletPath("/authorize");
        subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        setAuthentication();
        subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

}