package com.kd.novel.backend.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.exception.TokenIsExpiredException;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.config.jwt.JwtAuthenticationToken;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.utils.ApplicationContextHelper;
import com.kd.novel.backend.utils.JwtTokenUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by echisan on 2018/6/23
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<RequestMatcher> permissiveRequestMatchers;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

//        String requestURI = request.getServletPath();
//        boolean matchedURI = false;
//        AntPathMatcher antPathMatcher = new AntPathMatcher();
//        for(String uri : path){
//            if(antPathMatcher.match(uri,requestURI)){
//                matchedURI = true;
//                break;
//            }
//        }
//        if(!matchedURI){
//            chain.doFilter(request, response);
//            return;
//        }
        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息
        Authentication authResult = null;
        JwtAuthenticationToken authToken = null;
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        ObjectMapper objectMapper = (ObjectMapper) ApplicationContextHelper.getBean("jacksonObjectMapper");
        try {
            if (StringUtils.isNotBlank(token)) {
                DecodedJWT decode = JWT.decode(token);
                if (decode.getExpiresAt().before(Calendar.getInstance().getTime()))
                    throw new TokenIsExpiredException("登录凭证已过期,请重新登录");
                authToken = new JwtAuthenticationToken(decode);
                authResult = this.getAuthenticationManager().authenticate(authToken);
            }
        } catch (TokenIsExpiredException e) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            String reason = "Authentication failed,Reason:" + e.getMessage();
            Map<String, Object> res = new HashMap<>();
            res.put("data", null);
            res.put("message", reason);
            res.put("code", DefaultConstants.ERROR_TOKEN_TIMEOUT);
            PrintWriter out;
            out = response.getWriter();
            out.append(objectMapper.writeValueAsString(res));
            out.close();
            return;
        } catch (IllegalArgumentException | JWTDecodeException e) {
            logger.error("非法Token的用户尝试登录,{}", token, e);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            String reason = "Authentication failed,Reason:" + "凭证不合规范,请重新登录";
            Map<String, Object> res = new HashMap<>();
            res.put("data", null);
            res.put("message", reason);
            res.put("code", DefaultConstants.ERROR_TOKEN_ILLEGEAL);
            PrintWriter out;
            out = response.getWriter();
            out.append(objectMapper.writeValueAsString(res));
            out.close();
            return;
        } catch (AuthenticationException e) {
            logger.warn("验证失败的用户尝试登录,{}", token, e);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            String reason = "Authentication failed,Reason:" + e.getMessage();
            Map<String, Object> res = new HashMap<>();
            res.put("data", null);
            res.put("message", reason);
            res.put("code", DefaultConstants.ERROR_TOKEN_ILLEGEAL);
            PrintWriter out;
            out = response.getWriter();
            out.append(objectMapper.writeValueAsString(res));
            out.close();
            return;
        } catch (ServiceException e) {
            e.printStackTrace();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String reason = "Authentication failed,Reason(Service):" + e.getMessage();
            Map<String, Object> res = new HashMap<>();
            res.put("data", null);
            res.put("message", reason);
            res.put("code", DefaultConstants.ERROR_SERVICE);
            PrintWriter out;
            out = response.getWriter();
            out.append(objectMapper.writeValueAsString(res));
            out.close();
            return;
        }
        if(JwtTokenUtils.tokenShouldRefresh(token)){
            String newToken = JwtTokenUtils.createToken((UserDetails) authResult.getPrincipal(),true);
            Cookie[] cookies = request.getCookies();
            if(cookies.length > 0){
                for(Cookie cookie : cookies){
                    if(cookie.getName().equals("token")){
                        cookie.setValue(JwtTokenUtils.TOKEN_PREFIX.replace(" ","%20")+newToken);
                        cookie.setMaxAge((int)JwtTokenUtils.EXPIRATION_REMEMBER);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        super.doFilterInternal(request, response, chain);
    }

    // 这里从token中获取用户信息并新建一个token
//    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) throws TokenIsExpiredException {
//        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
//        boolean expiration = JwtTokenUtils.isExpiration(token);
//        if (expiration) {
//            throw new TokenIsExpiredException("token超时了");
//        } else {
//            String username = JwtTokenUtils.getUsername(token);
//            String role = JwtTokenUtils.getUserRole(token);
//            if (username != null) {
//                return new UsernamePasswordAuthenticationToken(username, null,
//                        Collections.singleton(new SimpleGrantedAuthority(role))
//                );
//            }
//        }
//        return null;
//    }

    protected boolean permissiveRequest(HttpServletRequest request) {
        if (permissiveRequestMatchers == null)
            return false;
        for (RequestMatcher permissiveMatcher : permissiveRequestMatchers) {
            if (permissiveMatcher.matches(request))
                return true;
        }
        return false;
    }

    public void setPermissiveUrl(String... urls) {
        if (permissiveRequestMatchers == null)
            permissiveRequestMatchers = new ArrayList<>();
        for (String url : urls)
            permissiveRequestMatchers.add(new AntPathRequestMatcher(url));
    }
}
