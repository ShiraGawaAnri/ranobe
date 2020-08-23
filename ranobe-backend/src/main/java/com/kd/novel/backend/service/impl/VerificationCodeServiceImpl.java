package com.kd.novel.backend.service.impl;

import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.entity.UserLogin;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.resipority.UserLoginResipority;
import com.kd.novel.backend.service.VerificationCodeService;
import com.kd.novel.backend.utils.HttpUtils;
import com.kd.novel.backend.utils.IpUtils;
import com.kd.novel.backend.utils.JwtTokenUtils;
import com.kd.novel.backend.utils.MyUtils;
import com.kd.novel.backend.vo.JsonResult;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraintvalidators.RegexpURLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {


    @Autowired
    private UserLoginResipority userLoginResipority;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private MyUtils myUtils;

    private Boolean vCodeExists(VerificationCodeCheckRequest entity){
        String vcode = entity.getVcode();
        String code = entity.getCode();
        String s = jedisCluster.get(PreConstants.VCODEPREEMAIL + code);
        String s1 = jedisCluster.get(PreConstants.VCODEPREPHONE + code);
        vcode = vcode.toLowerCase();
        return vcode.equals(s) || vcode.equals(s1);
    }

    @Override
    public JsonResult vCodeCheck(VerificationCodeCheckRequest entity) {
        if(!vCodeExists(entity)){
            throw new ServiceException("验证码不存在,请重新输入");
        }
        return JsonResult.oK();
    }

    @Override
    public JsonResult vCodeCheckByType(VerificationCodeCheckByTypeRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token,true);
        Integer type = entity.getType();
        String vcode = entity.getVcode();
        String code = "";
        if(type.equals(1)){
            //phone
            String phone = userLogin.getPhone();
            if(StringUtils.isBlank(phone)){
                throw new ServiceException("移动电话为空,无法验证,请咨询管理员!");
            }
            code = phone;
        }else if(type.equals(2)){
            //email
            String email = userLogin.getEmail();
            if(StringUtils.isBlank(email)){
                throw new ServiceException("邮箱地址为空,无法验证,请咨询管理员!");
            }
            code = email;
        }
        VerificationCodeCheckRequest verifyEntity = new VerificationCodeCheckRequest();
        verifyEntity.setCode(code);
        verifyEntity.setVcode(vcode);
        if(!vCodeExists(verifyEntity)){
            throw new ServiceException("验证码不存在,请重新输入");
        }
        return JsonResult.oK();
    }

    @Override
    public JsonResult vCodeSendByType(VerificationCodeSendByTypeRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token,true);
        Integer type = entity.getType();
        if(type.equals(1)){
        //phone
            String phone = userLogin.getPhone();
            if(StringUtils.isBlank(phone)){
                throw new ServiceException("移动电话为空,无法发送,请咨询管理员!");
            }
            VerificationCodeSendRequest requestEntity = new VerificationCodeSendRequest();
            requestEntity.setCode(phone);
            return vCodeSendToPhoneRequest(requestEntity);
        }else if(type.equals(2)){
        //email
            String email = userLogin.getEmail();
            if(StringUtils.isBlank(email)){
                throw new ServiceException("邮箱地址为空,无法发送,请咨询管理员!");
            }
            VerificationCodeSendRequest requestEntity = new VerificationCodeSendRequest();
            requestEntity.setCode(email);
            return vCodeSendToEmailRequest(requestEntity);
        }
        return JsonResult.oK();
    }

    @Override
    public JsonResult vCodeSendToPhoneRequest(VerificationCodeSendRequest entity) {
        String phone = entity.getCode();
//        String regExp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
//        Pattern compile = Pattern.compile(regExp);
//        Matcher m = compile.matcher(phone);
//        if(!m.matches()){
//            throw new ServiceException("邮箱格式不符合规范");
//        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        String keyWord1 = phone;
        String keyWord2;
        if(StringUtils.isNotEmpty(token)){
            keyWord2 = JwtTokenUtils.getUsername(token);
        }else{
            keyWord2 = IpUtils.getIpAddr(request);
        }
        //Check cooldown
        String cooldownCode = jedisCluster.get(PreConstants.VCODECOOLDOWNPRE + keyWord1);
        String cooldownSymbol = jedisCluster.get(PreConstants.VCODECOOLDOWNPRE + keyWord2);
        if(!StringUtils.isAllEmpty(cooldownCode,cooldownSymbol)){
            throw new ServiceException("请求过快,请等待一段时间后再试!");
        }
        //CHECK LIMIT AND INCR
        CheckTotalTimeLimit(keyWord1);
        CheckTotalTimeLimit(keyWord2);

        //Save Random Code
        //String randomCode = DigestUtils.md5Hex(UUID.randomUUID().toString() + phone + MyUtils.getRandomString(5, 6));
        String randomCode = MyUtils.getRandomString(5, 6).toLowerCase();
        String key = PreConstants.VCODEPREEMAIL + phone;
        jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND * 3,randomCode);

        //TODO:Send
        //Send Random Code

        //60s cooldown
        jedisCluster.setex(PreConstants.VCODECOOLDOWNPRE + keyWord1,DefaultConstants.VCODECOOLDOWN,phone);
        jedisCluster.setex(PreConstants.VCODECOOLDOWNPRE + keyWord2,DefaultConstants.VCODECOOLDOWN,phone);
        return JsonResult.oK(randomCode);
    }

    @Override
    public JsonResult vCodeSendToEmailRequest(VerificationCodeSendRequest entity) {
        String email = entity.getCode();
        String regExp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        Pattern compile = Pattern.compile(regExp);
        Matcher m = compile.matcher(email);
        if(!m.matches()){
            throw new ServiceException("邮箱格式不符合规范");
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        String keyWord1 = email;
        String keyWord2;
        if(StringUtils.isNotEmpty(token)){
            keyWord2 = JwtTokenUtils.getUsername(token);
        }else{
            keyWord2 = IpUtils.getIpAddr(request);
        }
        //Check cooldown
        String cooldownCode = jedisCluster.get(PreConstants.VCODECOOLDOWNPRE + keyWord1);
        String cooldownSymbol = jedisCluster.get(PreConstants.VCODECOOLDOWNPRE + keyWord2);
        if(!StringUtils.isAllEmpty(cooldownCode,cooldownSymbol)){
            throw new ServiceException("请求过快,请等待一段时间后再试!");
        }
        //CHECK LIMIT AND INCR
        CheckTotalTimeLimit(keyWord1);
        CheckTotalTimeLimit(keyWord2);

        //Save Random Code
        //String randomCode = DigestUtils.md5Hex(UUID.randomUUID().toString() + email + MyUtils.getRandomString(5, 6));
        String randomCode = MyUtils.getRandomString(5, 6).toLowerCase();
        String key = PreConstants.VCODEPREEMAIL + email;
        jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND * 3,randomCode);

        //TODO:Send
        //Send Random Code

        //60s cooldown
        jedisCluster.setex(PreConstants.VCODECOOLDOWNPRE + keyWord1,DefaultConstants.VCODECOOLDOWN,email);
        jedisCluster.setex(PreConstants.VCODECOOLDOWNPRE + keyWord2,DefaultConstants.VCODECOOLDOWN,email);
        return JsonResult.oK(randomCode);
    }

    private void CheckTotalTimeLimit(String keyWord) {
        String key = PreConstants.VCODEDAYTOTALPRE + keyWord;
        Long setnx = jedisCluster.setnx(key, "1");
        if(setnx == 0){
            Integer times = Integer.valueOf(jedisCluster.get(key));
            if(times >= DefaultConstants.VCODEDAYTOTALLIMIT){
                throw new ServiceException("请求次数过多,请等待24小时后再试!");
            }
            jedisCluster.incr(key);
        }else{
            jedisCluster.expire(key,DefaultConstants.ONEDAYSECOND);
        }
    }
}
