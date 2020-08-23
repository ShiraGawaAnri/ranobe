package com.kd.novel.backend.config.constants;


import org.springframework.stereotype.Component;

@Component
public class PreConstants {

    public final static String LOGINPRE = "Ranobe_LOGIN_";

    public final static String TOKENPRE = "Ranobe_TOKEN_";

    public final static String USERNAMEPRE = "Ranobe_USERNAME_";

    public final static String REDISUSERINFONEEDUPDATE = "Ranobe_REDISUSERINFONEEDUPDATE_";

    public final static String GOOGLETOKENPRE = "Ranobe_GOOGLE_TOKEN_";

    public final static String AUTHPRE = "Ranobe_Authticate_";

    public final static String PHSPRE = "Ranobe_PlusHotScore_PREFIX_";

    public final static String VCODEPRE = "Ranobe_VerificationCode_PREFIX_";

    public final static String VCODEPREEMAIL = VCODEPRE + "EMAIL_";

    public final static String VCODEPREPHONE = VCODEPRE + "PHONE_";

    public final static String VCODECOOLDOWNPRE = VCODEPRE + "COOLDOWN_";

    public final static String VCODEDAYTOTALPRE = VCODEPRE + "DAYTOTAL_";
}
