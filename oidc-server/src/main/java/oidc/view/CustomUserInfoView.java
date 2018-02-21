package oidc.view;

import com.google.gson.JsonObject;
import org.mitre.openid.connect.view.UserInfoView;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

@Component(CustomUserInfoView.VIEW_NAME)
@Primary
public class CustomUserInfoView extends UserInfoView {

    public static final String VIEW_NAME = "Custom" + CustomUserInfoView.VIEWNAME;

    @Override
    protected void writeOut(JsonObject json, Map<String, Object> model, HttpServletRequest request,
                            HttpServletResponse response) {
        try {
            response.setCharacterEncoding("UTF-8");
            Writer out = response.getWriter();
            gson.toJson(json, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
