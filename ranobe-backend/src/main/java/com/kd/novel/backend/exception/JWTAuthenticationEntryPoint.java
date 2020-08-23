package com.kd.novel.backend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.utils.ApplicationContextHelper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by echisan on 2018/6/24
 *
 * @description:没有携带token或者token无效
 */
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String reason = "Authentication failed,Reason -+:" + authException.getMessage();
        ObjectMapper objectMapper = (ObjectMapper) ApplicationContextHelper.getBean("jacksonObjectMapper");
        Map<String, Object> res = new HashMap<>();
        res.put("data", null);
        res.put("message", reason);
        res.put("code", DefaultConstants.ERROR_TOKEN_ILLEGEAL);
        PrintWriter out;
        out = response.getWriter();
        out.append(objectMapper.writeValueAsString(res));
        out.close();
    }
}
