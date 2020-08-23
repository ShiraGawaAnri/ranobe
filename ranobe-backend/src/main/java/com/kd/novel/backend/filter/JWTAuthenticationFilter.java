package com.kd.novel.backend.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.dto.UserLoginDTO;
import com.kd.novel.backend.dto.UserLoginRequest;
import com.kd.novel.backend.resipority.UserLoginResipority;
import com.kd.novel.backend.utils.ApplicationContextHelper;
import com.kd.novel.backend.utils.JwtTokenUtils;
import com.kd.novel.backend.utils.MyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    @Value("${custom.google.enable}")
    private Boolean googleEnable;

    private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl("/auth/login");
    }

    public Validator validator;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        // 从输入流中获取到登录的信息
        try {
            String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
            Map<String, Object> res = new HashMap<>();
            ObjectMapper objectMapper = (ObjectMapper) ApplicationContextHelper.getBean("jacksonObjectMapper");

            if (!StringUtils.hasText(body)) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                res.put("data", null);
                res.put("message", "参数不能为空");
                res.put("code", DefaultConstants.ERROR_SYSTEM);
                PrintWriter out = response.getWriter();
                out.append(objectMapper.writeValueAsString(res));
                out.close();
                return null;
            }
            UserLoginRequest userLoginRequest = objectMapper.readValue(body, UserLoginRequest.class);
            String username = userLoginRequest.getUsername();
            String password = userLoginRequest.getPassword();
            String recaptchaToken = userLoginRequest.getRecaptchaToken();
            Validator validator = (Validator) ApplicationContextHelper.getBean("validator");
            assert validator != null;
            List<String> list = new ArrayList<>();
            Set<ConstraintViolation<UserLoginRequest>> violations = validator.validate(userLoginRequest);
            for (ConstraintViolation<UserLoginRequest> violation : violations) {
                list.add(violation.getMessage());
            }
            //validate failed
            if(list.size() > 0){
                throw new ServiceException(list.get(0));
            }
            if (username == null)
                username = "";
            if (password == null)
                password = "";
            username = username.trim();
            password = password.trim();
            UserLoginResipority userLoginResipority = (UserLoginResipority) ApplicationContextHelper.getBean("userLoginResipority");
            JedisCluster jedisCluster = (JedisCluster)ApplicationContextHelper.getBean("jedisCluster");
            assert userLoginResipority != null;
            assert jedisCluster != null;
            if(false && jedisCluster.exists(PreConstants.GOOGLETOKENPRE + recaptchaToken)){
                Boolean check;
                try {
                    MyUtils myUtils = new MyUtils(objectMapper);
                    check = myUtils.GoogleRecaptcha(request, recaptchaToken);
                    if(!check) throw new Exception();
                } catch (Exception e) {
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    res.put("data", null);
                    res.put("message", "人机验证结果校验失败,请重新再试!");
                    res.put("code", DefaultConstants.ERROR_IMGCODE);
                    PrintWriter out = response.getWriter();
                    out.append(objectMapper.writeValueAsString(res));
                    out.close();
                    return null;
                }
                jedisCluster.setex(PreConstants.GOOGLETOKENPRE + recaptchaToken,90, username);
            }
            Optional<UserLogin> byUsernameEquals = userLoginResipority.findByUsernameEquals(username);
            if(!byUsernameEquals.isPresent()){
                throw new BadCredentialsException("账号名或密码错误");
            }
            UserLogin userLogin = byUsernameEquals.get();
            String salt = userLogin.getSalt();
            String saltedPassword = MyUtils.CreateSaltedPassword(password, salt);
            if(!userLogin.getPassword().equals(saltedPassword)){
                throw new BadCredentialsException("账号名或密码错误");
            }
            rememberMe.set(0);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, userLogin.getPassword());
            return this.authenticationManager.authenticate(authRequest);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {


        ObjectMapper objectMapper = (ObjectMapper) ApplicationContextHelper.getBean("jacksonObjectMapper");
        UserDetails jwtUser = (UserDetails) authResult.getPrincipal();

        //System.out.println("jwtUser:" + jwtUser.toString());
        boolean isRemember = rememberMe.get() == 1;

        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();

        String token = JwtTokenUtils.createToken(jwtUser, true);
//        String token = JwtTokenUtils.createToken(jwtUser.getUsername(), false);
        // 返回创建成功的token
        // 但是这里创建的token只是单纯的token
        // 按照jwt的规定，最后请求的时候应该是 `Bearer token`
        String retToken = JwtTokenUtils.TOKEN_PREFIX + token;
        JedisCluster jedisCluster = (JedisCluster)ApplicationContextHelper.getBean("jedisCluster");
        String username = jwtUser.getUsername();
        List<String> roles = new ArrayList<>();
        authorities.forEach(x -> roles.add(x.getAuthority()));
        UserLoginDTO userVo = new UserLoginDTO();
        userVo.setUsername(username);
        userVo.setRoles(roles);
        if(jedisCluster != null){
            UserLoginResipority resipority = (UserLoginResipority)ApplicationContextHelper.getBean("userLoginResipority");
            if(resipority != null){
                Optional<UserLogin> byUsernameEquals = resipority.findByUsernameEquals(username);
                if(byUsernameEquals.isPresent()){
                    UserLogin userLogin = byUsernameEquals.get();
                    JwtTokenUtils.setUserVo(userVo,userLogin);
//                    userLogin.getNovelSubscribes().forEach(each->{
//                        Long id = each.getId();
//                    });
//                    userLogin.getNovelHistories().forEach(each->{
//                        Long id = each.getId();
//                    });
//                    userLogin.getNovelLikes().forEach(each->{
//                        Long id = each.getId();
//                    });
//                    userVo.setAvatar(userLogin.getAvatar());
//                    userVo.setId(userLogin.getId());
//                    userVo.setNickname(userLogin.getNickname());
//                    userVo.setStatus(userLogin.getStatus());
//                    userVo.setZone(userLogin.getZone());
//                    userVo.setNovelSubscribes(userLogin.getNovelSubscribes());
//                    userVo.setNovelHistories(userLogin.getNovelHistories());
//                    userVo.setNovelLikes(userLogin.getNovelLikes());
//                    userVo.setNovelCommentLikes(userLogin.getNovelCommentLikes());
//                    userVo.setNovelCommentDislikes(userLogin.getNovelCommentDislikes());

                }
            }
            jedisCluster.setex(PreConstants.USERNAMEPRE + username,DefaultConstants.ONEDAYSECOND * 3,objectMapper.writeValueAsString(userVo));
        }
        response.setHeader("token", retToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> res = new HashMap<>();
        HashMap<String, String> map = new HashMap<>();
        map.put("token", retToken);
        res.put("data", map);
        res.put("message", "welcome");
        res.put("code", DefaultConstants.SUCCESS_SERVICE);
        PrintWriter out;
        out = response.getWriter();
        out.append(objectMapper.writeValueAsString(res));
        out.close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        //response.getWriter().write("authentication failed, reason: " + failed.getMessage());
        ObjectMapper objectMapper = (ObjectMapper) ApplicationContextHelper.getBean("jacksonObjectMapper");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        String reason = "Authentication failed,Reason:" + failed.getMessage();
        Map<String, Object> res = new HashMap<>();
        res.put("data", null);
        res.put("message", reason);
        res.put("code", HttpServletResponse.SC_FORBIDDEN);
        PrintWriter out;
        out = response.getWriter();
        out.append(objectMapper.writeValueAsString(res));
        out.close();
    }

    @Bean("authenticationManager")
    public AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}
