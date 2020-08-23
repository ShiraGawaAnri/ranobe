package com.kd.novel.backend.config.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kd.novel.backend.service.jwt.JwtUserDetailsServiceImpl;
import com.kd.novel.backend.utils.JwtTokenUtils;
import com.kd.novel.backend.service.jwt.JwtUserDetailsServiceImpl;
import com.kd.novel.backend.utils.JwtTokenUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import java.util.Collection;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtUserDetailsServiceImpl userService;

    public JwtAuthenticationProvider(JwtUserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DecodedJWT jwt = ((JwtAuthenticationToken) authentication).getToken();
        authentication.getPrincipal();
        String username = jwt.getSubject();
//        JwtAuthenticationToken jwtToken = JwtTokenUtils.checkRedisToken(username);
//        if (jwtToken != null) {
//            return jwtToken;
//        }
        UserDetails user = userService.getUserLoginInfo(username);
        if (user == null || user.getPassword() == null)
            throw new NonceExpiredException("登录凭证无法找到,请重新登录");
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        try {
            Algorithm algorithm = Algorithm.HMAC256(JwtTokenUtils.SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(username)
                    .build();
            verifier.verify(jwt.getToken());
        } catch (Exception e) {
            throw new BadCredentialsException("登录凭证无法确认,请重新登录", e);
        }
        JwtAuthenticationToken token = new JwtAuthenticationToken(user, jwt, user.getAuthorities());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }

}
