package com.kd.novel.backend.config.constants;

import org.springframework.stereotype.Component;

@Component
public class DefaultConstants {

    public final static int ACCOUNT_DEFAULT_GROUPLEVEL = 1;

    public final static int ACCOUNT_DEFAULT_STATUS = 0;

    public final static int SUCCESS_SERVICE = 20000;

    public final static int ERROR_IMGCODE = 20004;

    public final static int ERROR_SERVICE = 40000;

    public final static int ERROR_SERVIE_ERRORTEXT = 40001;

    public final static int ERROR_SYSTEM = 50000;

    public final static int ERROR_TOKEN_ILLEGEAL = 50008;

    public final static int ERROR_TOKEN_ACCESSDENIED = 50012;

    public final static int ERROR_TOKEN_TIMEOUT = 50014;

    public final static Integer ONEDAYSECOND = 86400;

    public final static Long ONEDAYMILLISECOND = 86400000L;

    public final static Integer VCODECOOLDOWN = 60;

    public final static Integer VCODEDAYTOTALLIMIT = 20;

}
