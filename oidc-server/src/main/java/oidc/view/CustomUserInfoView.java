/*******************************************************************************
 * Copyright 2016 The MITRE Corporation
 *   and the MIT Internet Trust Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
