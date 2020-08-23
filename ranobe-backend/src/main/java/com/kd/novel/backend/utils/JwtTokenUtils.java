package com.kd.novel.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.config.jwt.MyJwtUserDetails;
import com.kd.novel.backend.dto.UserLoginDTO;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.entity.UserLoginRole;
import com.kd.novel.backend.entity.UserSettings;
import com.kd.novel.backend.resipority.UserLoginResipority;
import com.kd.novel.backend.config.jwt.JwtAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String SECRET = "jwtsecret";
    public static final String ISS = "jwtiss";
    // 选择了记住我之后的过期时间为3天
    public static final long EXPIRATION_REMEMBER = DefaultConstants.ONEDAYSECOND * 3;
    // 角色的key
    private static final String ROLE_CLAIMS = "rol";
    // 过期时间是3600秒，既是1个小时
    private static final long EXPIRATION = 3600L;
    //刷新间隔60分钟
    private static final int TokenRefreshInterval = 3600;

//    @Autowired
//    private JwtUserDetailsServiceImpl userService;

    private final ObjectMapper objectMapper;

    private final JedisCluster jedisCluster;

    @Autowired
    private UserLoginResipority userLoginResipority;

    @Autowired
    public JwtTokenUtils(ObjectMapper objectMapper,JedisCluster jedisCluster) {
        this.objectMapper = objectMapper;
        this.jedisCluster = jedisCluster;
    }

    // 创建token
    public static String createToken(UserDetails user, boolean isRememberMe) {
        long expiration = isRememberMe ? EXPIRATION_REMEMBER : EXPIRATION;
        HashMap<String, Object> map = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        map.put(ROLE_CLAIMS, authorities);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String username = user.getUsername();
        String sign = JWT.create()
                .withSubject(username)
                .withClaim("isRememberMe", isRememberMe)
                .withIssuer(ISS)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000L))
                .withIssuedAt(new Date())
                .sign(algorithm);
        return sign;
    }

    public static String createToken(MyJwtUserDetails user, Integer days) {
        long expiration = EXPIRATION_REMEMBER / 3 * days;
        HashMap<String, Object> map = new HashMap<>();
        List<String> roles = user.getRoles();
        map.put(ROLE_CLAIMS, roles);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        String username = user.getUsername();
        String sign = JWT.create()
                .withSubject(username)
                .withClaim("isRememberMe", true)
                .withIssuer(ISS)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration * 1000L))
                .withIssuedAt(new Date())
                .sign(algorithm);
        return sign;
    }

    public static String getTokenFromHeader(HttpServletRequest request) {
        String token = null;
        String authInfo = request.getHeader("Authorization");
        if (authInfo != null) {
            token = StringUtils.removeStart(authInfo, "Bearer ");
        }
        return token;
    }

    public static boolean tokenShouldRefresh(String authcatiedToken) {
        //構造用戶信息
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(JWT.decode(authcatiedToken));
        DecodedJWT jwt = authToken.getToken();
        LocalDateTime issueTime = LocalDateTime.ofInstant(jwt.getIssuedAt().toInstant(), ZoneId.systemDefault());
        return issueTime.plusSeconds(TokenRefreshInterval).isBefore(LocalDateTime.now());
    }

    // 从token中获取账号名
    public static String getUsername(String token) {
        JWTVerifier build = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT verify = build.verify(token);
        return verify.getSubject();
    }

    public UserLoginDTO getUserInfo(String token) {
        UserLoginDTO userVo;
        String username;
        try{
            if(token == null) throw new Exception();
            username = JwtTokenUtils.getUsername(token);
            String needUpdateKey = PreConstants.REDISUSERINFONEEDUPDATE + username;
            Boolean needUpdate = jedisCluster.get(needUpdateKey) != null;
            if(needUpdate){
                jedisCluster.del(needUpdateKey);
                throw new Exception();
            }else{
                String str = jedisCluster.get(PreConstants.USERNAMEPRE + username);
                if(str == null) throw new Exception();
                userVo = objectMapper.readValue(str, UserLoginDTO.class);
            }
        } catch (Exception e){
            //例如由于/user/**是被要求验证的，因此可以直接调用已验证的结果
            Authentication authResult = SecurityContextHolder.getContext().getAuthentication();
            if(authResult.getPrincipal() instanceof String) return null;
            UserDetails details = (UserDetails) authResult.getPrincipal();
            username = details.getUsername();
            Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
            List<String> roles = new ArrayList<>();
            authorities.forEach(x -> roles.add(x.getAuthority()));
            userVo = new UserLoginDTO();
            userVo.setUsername(username);
            userVo.setUname(username);
            userVo.setRoles(roles);
            UserLoginResipority resipority = (UserLoginResipority)ApplicationContextHelper.getBean("userLoginResipority");
            if(resipority != null){
                Optional<UserLogin> byUsernameEquals = resipority.findByUsernameEquals(username);
                if(byUsernameEquals.isPresent()){
                    UserLogin userLogin = byUsernameEquals.get();
                    setUserVo(userVo, userLogin);
                }
            }
        }
        try {
            if(userVo != null && userVo.getId() != null){
                jedisCluster.setex(PreConstants.USERNAMEPRE + username,DefaultConstants.ONEDAYSECOND * 3,objectMapper.writeValueAsString(userVo));
            }
        } catch (JsonProcessingException ignored) {

        }
        return userVo;
    }

    public static void setUserVo(UserLoginDTO userVo, UserLogin userLogin) {
        userLogin.getNovelSubscribes().forEach(each->{
            Long id = each.getId();
        });
        userLogin.getNovelHistories().forEach(each->{
            Long id = each.getId();
        });
        userLogin.getNovelLikes().forEach(each->{
            Long id = each.getId();
        });
        UserSettings userSettings = userLogin.getUserSettings();
        if(userSettings !=null){
            Long settingsId = userSettings.getId();
        }
        userVo.setUserSettings(userSettings);
        userVo.setUname(userLogin.getUsername());
        userVo.setUsername(userLogin.getUsername());
        userVo.setAvatar(userLogin.getAvatar());
        userVo.setId(userLogin.getId());
        userVo.setNickname(userLogin.getNickname());
        userVo.setStatus(userLogin.getStatus());
        userVo.setZone(userLogin.getZone());
        userVo.setNovelSubscribes(userLogin.getNovelSubscribes());
        userVo.setNovelHistories(userLogin.getNovelHistories());
        userVo.setNovelLikes(userLogin.getNovelLikes());
        userVo.setNovelCommentLikes(userLogin.getNovelCommentLikes());
        userVo.setNovelCommentDislikes(userLogin.getNovelCommentDislikes());

    }

    public UserLogin getUserLogin(String token) {
        return getUserLogin(token,false);
    }

    public UserLogin getUserLogin(String token,Boolean forceGetFromSql) {
        if(forceGetFromSql){
            Authentication authResult = SecurityContextHolder.getContext().getAuthentication();
            if(authResult.getPrincipal() instanceof String) return null;
            UserDetails details = (UserDetails) authResult.getPrincipal();
            String username = details.getUsername();
            return userLoginResipority.findByUsernameEquals(username).orElse(null);
        }
        try{
            UserLoginDTO userInfo = getUserInfo(token);
            UserLogin userLogin = new UserLogin();
            BeanUtils.copyProperties(userInfo,userLogin);
            //object references an unsaved transient instance
            //有可能是version == null引起的
            userLogin.setUsername(userInfo.getUname());
            if(userLogin.getVersion() == null){
                userLogin.setVersion(0L);
            }
            return userLogin;
        }catch (Exception e){
            return null;
        }
    }

    public Boolean hasPermission(UserLogin userLogin,Iterable<String> permissions){
        Set<UserLoginRole> loginRole = userLogin.getLoginRole();
        if(loginRole == null) return false;
        List<String> rolesCollect = loginRole
                .stream()
                .map(each -> each.getUserRole().getUserRolePermissions()
                        .stream()
                        .map(p -> p.getUserPermission().getRole()).collect(Collectors.toSet()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        AtomicReference<Boolean> flag = new AtomicReference<>(false);
        permissions.forEach(each->{
            if(rolesCollect.contains(each)){
                flag.set(true);
            }
        });
        return flag.get();
    }

    public Boolean hasPermission(String token,Iterable<String> permissions){
        UserLoginDTO userInfo = this.getUserInfo(token);
        List<String> roles = userInfo.getRoles();
        AtomicReference<Boolean> flag = new AtomicReference<>(false);
        permissions.forEach(each->{
            if(roles.contains(each)){
                flag.set(true);
            }
        });
        return flag.get();
    }
}
