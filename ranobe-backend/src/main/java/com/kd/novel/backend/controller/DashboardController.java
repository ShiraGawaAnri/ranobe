package com.kd.novel.backend.controller;

import com.kd.novel.backend.config.annotation.UserAccess;
import com.kd.novel.backend.vo.JsonResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    @UserAccess("ROLE_USER")
    public JsonResult getDashBoard(){
        return JsonResult.oK("Dashboard OK");
    }
}
