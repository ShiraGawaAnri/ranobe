package com.kd.novel.backend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.constants.DefaultConstants;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:没有访问权限
 * @author: maxiao1
 * @date: 2019/9/13 17:41
 */
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String reason = "Authentication failed --";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> res = new HashMap<>();
        res.put("data",null);
        res.put("message",reason);
        res.put("code",DefaultConstants.ERROR_TOKEN_ACCESSDENIED);
        PrintWriter out;
        out = response.getWriter();
        out.append(objectMapper.writeValueAsString(res));
        out.close();
    }
}
