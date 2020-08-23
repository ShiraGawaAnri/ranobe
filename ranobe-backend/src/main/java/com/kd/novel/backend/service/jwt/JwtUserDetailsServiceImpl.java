package com.kd.novel.backend.service.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.jwt.MyJwtUserDetails;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.entity.UserLoginRole;
import com.kd.novel.backend.resipority.UserLoginResipority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserLoginResipority userLoginResipority;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<UserLogin> byUsernameEquals = userLoginResipority.findByUsernameEquals(username);
            if(!byUsernameEquals.isPresent()) throw new Exception();
            UserLogin user = byUsernameEquals.get();
            Set<UserLoginRole> loginRole = user.getLoginRole();
            List<String> rolesCollect = loginRole
                    .stream()
                    .map(each -> each.getUserRole().getUserRolePermissions()
                            .stream()
                            .map(p -> p.getUserPermission().getRole()).collect(Collectors.toSet())).flatMap(Collection::stream).collect(Collectors.toList());
            if(rolesCollect.size() == 0){
                throw new BadCredentialsException("此账号无任何权限");
            }
            String[] roles = new String[rolesCollect.size()];
            rolesCollect.stream().map(roleStr->{
                roleStr = roleStr.replace("ROLE_","");
                return roleStr;
            }).collect(Collectors.toList()).toArray(roles);
            return User.builder().username(user.getUsername()).password(passwordEncoder.encode(user.getPassword())).roles(roles).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BadCredentialsException("无法获取到相应的结果");
    }


    public UserDetails getUserLoginInfo(String username) {
        String key = PreConstants.AUTHPRE + username;
        String s = jedisCluster.get(key);
        if(s != null){
            try {
                MyJwtUserDetails tempUserDetails = objectMapper.readValue(s,MyJwtUserDetails.class);
                jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND,objectMapper.writeValueAsString(tempUserDetails));
                List<String> roles = tempUserDetails.getRoles();
                String[] roleArray = new String[roles.size()];
                roles.toArray(roleArray);
                return User.builder().username(username).password(tempUserDetails.getPassword()).roles(roleArray).build();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        UserDetails userDetails = loadUserByUsername(username);
        MyJwtUserDetails tempUserDetails = new MyJwtUserDetails();
        tempUserDetails.setUsername(username);
        tempUserDetails.setPassword(userDetails.getPassword());
        List<String> roles = new ArrayList<>();
        userDetails.getAuthorities().forEach(each->{
            String authority = each.getAuthority().replace("ROLE_","");
            roles.add(authority);
        });
        tempUserDetails.setRoles(roles);
        //将salt放到password字段返回
        try {
            jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND,objectMapper.writeValueAsString(tempUserDetails));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return userDetails;
    }
}
