package com.kd.novel.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.dto.GoogleRecaptchaToken;
import com.kd.novel.backend.utils.HttpUtils;
import com.kd.novel.backend.utils.IpUtils;
import com.kd.novel.backend.vo.JsonResult;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recaptcha")
public class RecaptchaController {

    @Value("${custom.google.host}")
    private String host;

    @Value("${custom.google.path}")
    private String path;

    @Value("${custom.google.secret}")
    private String secret;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/google")
    public JsonResult google(HttpServletRequest request, @RequestBody GoogleRecaptchaToken google) throws Exception {
        String method = "POST";
        Map<String, String> headers = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        Map<String, String> bodys = new HashMap<>();
        querys.put("secret", secret);
        querys.put("response", google.getRecaptchaToken());
        querys.put("remoteip", IpUtils.getIpAddr(request));
        HttpResponse httpResponse = HttpUtils.doPost(host, path, method, headers, querys, bodys);
        String retString = EntityUtils.toString(httpResponse.getEntity());
        HashMap map = objectMapper.readValue(retString, HashMap.class);
        System.out.println(map.toString());
        return JsonResult.oK();
    }
}
