package oidc.security;

import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

@Component("apiCsrfProtectionMatcher")
public class CsrfProtectionMatcher implements RequestMatcher {

  private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

  @Override
  public boolean matches(HttpServletRequest request) {
    return request.getServletPath().startsWith("/api") && !allowedMethods.matcher(request.getMethod().toUpperCase()).matches();
  }
}
