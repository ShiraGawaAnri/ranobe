package com.kd.novel.backend.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OptionsRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //response.setHeader("Access-Control-Allow-Origin", "*");//* or origin as u prefer
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        //response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Headers","token,Origin, X-Requested-With, Content-Type, Accept,Authorization");
        response.setCharacterEncoding("UTF-8");

        if (request.getMethod().equals("OPTIONS"))
            response.setStatus(HttpServletResponse.SC_OK);
        else
            filterChain.doFilter(request, response);
    }
}
