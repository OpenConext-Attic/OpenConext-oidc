package oidc.security;

import oidc.saml.SAMLUser;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletRequest;

import static org.junit.Assert.*;

public class ClientIdFilterTest {

    private ClientIdFilter subject = new ClientIdFilter();

    @Test
    public void doFilter() throws Exception {
        MockHttpServletRequest request  = new MockHttpServletRequest();
        request.setParameter("client_id", "oidc");
        SAMLUser samlUser = new SAMLUser("sub", false, "different");
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(samlUser.getUsername(), "N/A");
        authentication.setDetails(samlUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        subject.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}