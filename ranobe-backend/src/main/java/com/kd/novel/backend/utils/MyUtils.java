package com.kd.novel.backend.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.constants.SaltConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class MyUtils {

    private final ObjectMapper objectMapper;

    @Autowired
    public MyUtils(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static String CreateSaltedPassword(String password, String salt) {
        return DigestUtils.md5Hex(password + salt + SaltConstants.SALTCONSTANTS);
    }

    public Boolean ArrayNotEmpty(List list){
        return list != null && list.size() > 0;
    }

    public Boolean MapNotEmpty(Map map){
        return map != null && map.size() > 0;
    }

    public Boolean SetNotEmpty(Set set){
        return set != null && set.size() > 0;
    }

    public Boolean GoogleRecaptcha(HttpServletRequest request, String rereptchaToken) throws Exception {
        String host = "https://www.recaptcha.net";
        String path = "/recaptcha/api/siteverify";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        querys.put("secret", "6Ldm0-4UAAAAABb1d59UVEv3p3aIu1nTGxEeVc_t");
        querys.put("response", rereptchaToken);
        querys.put("remoteip", IpUtils.getIpAddr(request));
        HttpResponse httpResponse = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        String retString = EntityUtils.toString(httpResponse.getEntity());
        HashMap map = objectMapper.readValue(retString, HashMap.class);
        System.out.println(map.toString());
        return (Boolean)map.get("success");
    }

    public static String getRandomString(int passLength, int type) {
        StringBuffer buffer = null;
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        r.setSeed(new Date().getTime());
        switch (type) {
            case 0:
                buffer = new StringBuffer("0123456789");
                break;
            case 1:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyz");
                break;
            case 2:
                buffer = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 3:
                buffer = new StringBuffer("0123456789abcdefghijklmnopqrstuvwxyz");
                break;
            case 4:
                buffer = new StringBuffer("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 5:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
                break;
            case 6:
                buffer = new StringBuffer("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
                sb.append(buffer.charAt(r.nextInt(buffer.length() - 10)));
                passLength -= 1;
                break;
            case 7:
                String s = UUID.randomUUID().toString();
                sb.append(s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24));
        }

        if (type != 7) {
            int range = buffer.length();
            for (int i = 0; i < passLength; ++i) {
                sb.append(buffer.charAt(r.nextInt(range)));
            }
        }
        return sb.toString();
    }
}
