package oidc.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SetCharacterEncodingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SetCharacterEncodingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("Initializing SetCharacterEncodingFilter");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LOG.info("The before character encoding for the response " + response.getCharacterEncoding());
        LOG.info("The before character encoding for the request " + request.getCharacterEncoding());

        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        LOG.info("The after character encoding for the response " + response.getCharacterEncoding());
        LOG.info("The after character encoding for the request " + request.getCharacterEncoding());

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        LOG.info("Destroying SetCharacterEncodingFilter");
    }
}
