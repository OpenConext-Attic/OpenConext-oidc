package oidc.web;

import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class AdminUserInfoInterceptor extends UserInfoInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null) {
      Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
      if (!CollectionUtils.isEmpty(authorities)) {
        for (GrantedAuthority authority : authorities) {
          if ("ROLE_ADMIN".equals(authority.getAuthority())) {
            return super.preHandle(request, response, handler);
          }
        }
      }
    }
    return true;
  }
}
