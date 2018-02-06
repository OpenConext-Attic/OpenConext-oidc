package oidc.service;

import org.apache.commons.io.IOUtils;
import org.mitre.discovery.web.DiscoveryEndpoint;
import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

@Component
public class ExtendedDiscoveryEndpointInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ConfigurationPropertiesBean config;

    private String wellKnownConfiguration;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
        Exception {
        if (request.getRequestURI().endsWith(DiscoveryEndpoint.OPENID_CONFIGURATION_URL)) {
            PrintWriter writer = response.getWriter();
            writer.write(getWellKnownConfiguration());
            writer.flush();
            return false;
        }
        return true;
    }

    private String getWellKnownConfiguration() throws IOException {
        if (StringUtils.isEmpty(wellKnownConfiguration)) {
            String baseUrl = config.getIssuer();
            if (!baseUrl.endsWith("/")) {
                baseUrl = baseUrl.concat("/");
            }
            wellKnownConfiguration = IOUtils.toString(new ClassPathResource("well_known_configuration.json")
                .getInputStream(), Charset.defaultCharset()).replaceAll("@configuration_end_point@", baseUrl);
        }
        return wellKnownConfiguration;
    }

}
