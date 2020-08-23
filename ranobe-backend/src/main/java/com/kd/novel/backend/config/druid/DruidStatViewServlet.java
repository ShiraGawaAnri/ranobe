package com.kd.novel.backend.config.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * druid监控视图配置
 */
@WebServlet(urlPatterns = "/druid/*", initParams = {
        @WebInitParam(name = "loginUsername", value = "root"),// 账号名
        @WebInitParam(name = "loginPassword", value = "root"),// 密码
        @WebInitParam(name = "resetEnable", value = "false")// 禁用HTML页面上的“Reset All”功能
})
public class DruidStatViewServlet extends StatViewServlet {
    private static final long serialVersionUID = 6588499385893299545L;

}

