package oidc.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.service.UserInfoService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdminUserInfoInterceptorTest {

  private static final String NAME = "John Doe";

  @InjectMocks
  private AdminUserInfoInterceptor subject = new AdminUserInfoInterceptor();

  @Mock
  private UserInfoService userInfoService;

  @Test
  public void testPreHandleWithAdminUser() throws Exception {
    String role = "ROLE_ADMIN";
    MockHttpServletRequest request = getMockHttpServletRequest(role);

    DefaultUserInfo defaultUserInfo = new DefaultUserInfo();
    when(userInfoService.getByUsername(NAME)).thenReturn(defaultUserInfo);

    subject.preHandle(request, null, null);

    DefaultUserInfo userInfo = (DefaultUserInfo) request.getAttribute("userInfo");
    assertEquals(defaultUserInfo, userInfo);
  }

  @Test
  public void testPreHandleWithOAuthUser() throws Exception {
    MockHttpServletRequest request = getMockHttpServletRequest("ROLE_USER");

    subject.preHandle(request, null, null);

    DefaultUserInfo userInfo = (DefaultUserInfo) request.getAttribute("userInfo");
    assertNull(userInfo);
  }

  @Test
  public void testPreHandleWithNoUser() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();

    subject.preHandle(request, null, null);

    DefaultUserInfo userInfo = (DefaultUserInfo) request.getAttribute("userInfo");
    assertNull(userInfo);
  }

  private MockHttpServletRequest getMockHttpServletRequest(String role) {
    Authentication authentication = new TestingAuthenticationToken(NAME, null, role);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    return new MockHttpServletRequest();
  }

}