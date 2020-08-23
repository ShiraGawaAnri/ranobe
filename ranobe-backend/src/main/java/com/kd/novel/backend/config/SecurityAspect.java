package com.kd.novel.backend.config;

import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.config.annotation.UserAccess;
import com.kd.novel.backend.config.enums.RoleEnum;
import com.kd.novel.backend.dto.UserLoginDTO;
import com.kd.novel.backend.utils.JwtTokenUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
public class SecurityAspect {

    @Autowired
    private JwtTokenUtils jwtUtils;

    private static Logger logger = LoggerFactory.getLogger(SecurityAspect.class);

    @Before(value = "@annotation(com.kd.novel.backend.config.annotation.UserAccess)")
    public void authoritiesCheck(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("user access security check for method: {}", methodName);

        try {
            UserAccess userAccess = ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(UserAccess.class);
            //如果是不需要登录就可以访问的API，则直接放行
            if (!userAccess.needLogin()) {
                return;
            }
            String message = userAccess.message();
//            if(message.equals("")){
//                message = "此分组的用户没有足够的权限操作!";
//            }
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            //String token = jwtUtils.getTokenFromCookies(request);
            String token = JwtTokenUtils.getTokenFromHeader(request);
            //需要登录才能访问的API，没有发现用户，返回禁止
            if (token == null) {
                //throw new ServiceException("Haeder 未找到登录信息!");
                throw new ServiceException("请登录后再访问!");
            }
            UserLoginDTO userInfo = jwtUtils.getUserInfo(token);
            if (userInfo == null) {
                //throw new ServiceException("Haeder 未找到登录信息!");
                throw new ServiceException("请登录后再访问!");
            }
            //value为空直接放行，代表登录即可
            String[] permitRoles = userAccess.value();
            if (ArrayUtils.isEmpty(permitRoles) || "".equals(permitRoles[0])) {
                if(userInfo.getRoles().size() == 1 && userInfo.getRoles().get(0).equals(RoleEnum.ANY.getName())){
                    throw new ServiceException(MessageFormat.format("{0}无法访问,请先进行登录",RoleEnum.ANY.getDesc()));
                }
                return;
            }
            List<String> userAuthorities = userInfo.getRoles();
            if (CollectionUtils.isEmpty(userAuthorities)) {
                throw new ServiceException(message);
            }

            Set<String> roles = new HashSet<>(userAuthorities);
            //在这里规定ROOT全部通过
            if (roles.contains("ROLE_ROOT")) {
                return;
            }
            Set<String> intersectionRoles =
                    Stream.of(permitRoles).filter(roles::contains).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(intersectionRoles)) {
                throw new ServiceException(message);
            }
        } catch (Exception e) {
            logger.error("user access security check Exception: {}", methodName, e);
            throw e;
        }

    }
}
