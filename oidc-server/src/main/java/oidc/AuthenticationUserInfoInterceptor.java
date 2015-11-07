package oidc;

import oidc.shibboleth.ShibbolethUser;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationUserInfoInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Object userInfo = request.getAttribute("userInfo");
    Object userInfoJson = request.getAttribute("userInfoJson");
    if (userInfo == null && userInfoJson == null && auth.getPrincipal() instanceof ShibbolethUser) {
      ShibbolethUser shibbolethUser = (ShibbolethUser) auth.getPrincipal();
      UserInfo user = new DefaultUserInfo();
      user.setSub(shibbolethUser.getUsername());
      user.setEmail(shibbolethUser.getEmail());
      user.setName(shibbolethUser.getDisplayName());
      user.setPreferredUsername(shibbolethUser.getDisplayName());
      request.setAttribute("userInfo", user);
      request.setAttribute("userInfoJson", user.toJson());
    }
    return true;
  }
}
