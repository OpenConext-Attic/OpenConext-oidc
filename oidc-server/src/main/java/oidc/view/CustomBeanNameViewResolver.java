package oidc.view;

import org.mitre.openid.connect.view.UserInfoView;
import org.springframework.beans.BeansException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.util.Locale;

public class CustomBeanNameViewResolver extends BeanNameViewResolver {

    @Override
    public View resolveViewName(String viewName, Locale locale) throws BeansException {
        if (UserInfoView.VIEWNAME.equals(viewName)) {
            return super.resolveViewName(CustomUserInfoView.VIEW_NAME, locale);
        }
        return super.resolveViewName(viewName, locale);
    }
}
