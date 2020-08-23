package com.kd.novel.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kd.novel.backend.config.jwt.MyJwtUserDetails;
import com.kd.novel.backend.entity.*;
import com.kd.novel.backend.resipority.UserRolePermissionResipority;
import com.kd.novel.backend.resipority.UserSettingsResipority;
import com.kd.novel.backend.service.UserService;
import com.kd.novel.backend.exception.ServiceException;
import com.kd.novel.backend.config.constants.DefaultConstants;
import com.kd.novel.backend.config.constants.PreConstants;
import com.kd.novel.backend.dto.*;
import com.kd.novel.backend.resipority.UserLoginResipority;
import com.kd.novel.backend.resipority.UserLoginRoleResipority;
import com.kd.novel.backend.service.VerificationCodeService;
import com.kd.novel.backend.utils.JwtTokenUtils;
import com.kd.novel.backend.utils.MyUtils;
import com.kd.novel.backend.utils.SensitiveInfoUtils;
import com.kd.novel.backend.vo.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisCluster;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Value("${custom.google.enable}")
    private Boolean googleEnable;

    @Autowired
    private UserLoginResipority userLoginResipority;

    @Autowired
    private UserLoginRoleResipority userLoginRoleResipority;

    @Autowired
    private UserRolePermissionResipority userRolePermissionResipority;

    @Autowired
    private UserSettingsResipority userSettingsResipority;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private MyUtils myUtils;

    @Autowired
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JedisCluster jedisCluster;

    @Autowired
    private HttpServletRequest request;

    private Boolean vCodeExists(VerificationCodeCheckRequest entity){
        String vcode = entity.getVcode();
        String code = entity.getCode();
        String s = jedisCluster.get(PreConstants.VCODEPREEMAIL + code);
        String s1 = jedisCluster.get(PreConstants.VCODEPREPHONE + code);
        vcode = vcode.toLowerCase();
        return vcode.equals(s) || vcode.equals(s1);
    }

    @Transactional
    @Override
    public JsonResult reg(UserRegRequest entity) throws Exception {
        String recaptchaToken = entity.getRecaptchaToken();
        String username = entity.getUsername();
        String nickname = entity.getNickname();
        String email = entity.getEmail();
        if(googleEnable && jedisCluster.exists(PreConstants.GOOGLETOKENPRE + recaptchaToken)){
            Boolean check = myUtils.GoogleRecaptcha(request, recaptchaToken);
            if(!check) return JsonResult.build(DefaultConstants.ERROR_IMGCODE,"人机验证结果校验失败,请重新再试!");
            jedisCluster.setex(PreConstants.GOOGLETOKENPRE + recaptchaToken,90,username);
        }
        Optional<UserLogin> byUsernameEquals = userLoginResipority.findByUsernameEquals(username);
        if(byUsernameEquals.isPresent()){
            throw new ServiceException("此账号名已经被使用");
        }
        Optional<UserLogin> byNicknameEquals = userLoginResipority.findByNicknameEquals(nickname);
        if(byNicknameEquals.isPresent()){
            throw new ServiceException("此昵称已经被使用");
        }
        Optional<UserLogin> byEmailEquals = userLoginResipority.findByEmailEquals(email);
        if(byEmailEquals.isPresent()){
            throw new ServiceException("此邮箱已经被使用");
        }
        String password = entity.getPassword();
        String salt = UUID.randomUUID().toString();
        String saltedPassword = MyUtils.CreateSaltedPassword(password, salt);
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername(username);
        userLogin.setPassword(saltedPassword);
        userLogin.setSalt(salt);
        userLogin.setNickname(nickname);
        userLogin.setEmail(email);
        userLogin.setStatus(1);
        userLogin.setCreatedTime(new Date());
        userLogin.setValidate(0);
        userLogin.setZone(1);
        userLoginResipority.saveAndFlush(userLogin);
//        Long id = userLogin.getId();
//        UserLoginRole userLoginRole = new UserLoginRole();
//        UserRole userRole = new UserRole();
//        userRole.setId(4L);
//        userLoginRole.setUserLogin(userLogin);
//        userLoginRole.setUserRole(userRole);
//        userLoginRoleResipority.saveAndFlush(userLoginRole);

        UserLoginRole userLoginRole = new UserLoginRole();
        UserRole userRole = new UserRole();
        userRole.setId(4L);
        userLoginRole.setUserLogin(userLogin);
        userLoginRole.setUserRole(userRole);

        userLoginRoleResipority.saveAndFlush(userLoginRole);

        UserSettings userSettings = new UserSettings();
        userSettings.setUserLogin(userLogin);
        userSettingsResipority.save(userSettings);

        return JsonResult.oK();
    }

    @Transactional
    @Override
    public JsonResult regSimple(UserRegSimpleRequest entity) {
        String nickname = entity.getNickname();
        String username;
        do {
            username = MyUtils.getRandomString(9, 6);
        } while (userLoginResipority.findByUsernameEquals(username).isPresent());

        Optional<UserLogin> byNicknameEquals = userLoginResipority.findByNicknameEquals(nickname);
        if(byNicknameEquals.isPresent()){
            throw new ServiceException("此昵称已经被使用");
        }
        String password = username;
        String salt = UUID.randomUUID().toString();
        String saltedPassword = MyUtils.CreateSaltedPassword(password, salt);
        UserLogin userLogin = new UserLogin();
        userLogin.setUsername(username);
        userLogin.setPassword(saltedPassword);
        userLogin.setSalt(salt);
        userLogin.setNickname(nickname);
        userLogin.setStatus(1);
        userLogin.setCreatedTime(new Date());
        userLogin.setValidate(0);
        userLogin.setZone(1);
        userLogin.setRebindChance(1);
        userLoginResipority.saveAndFlush(userLogin);

        UserLoginRole userLoginRole = new UserLoginRole();
        UserRole userRole = new UserRole();
        userRole.setId(3L);
        userLoginRole.setUserLogin(userLogin);
        userLoginRole.setUserRole(userRole);

        userLoginRoleResipority.saveAndFlush(userLoginRole);

        UserSettings userSettings = new UserSettings();
        userSettings.setUserLogin(userLogin);
        userSettingsResipority.save(userSettings);
        List<String> rolesCollect = userLogin.getLoginRole()
                .stream()
                .map(each -> each.getUserRole().getUserRolePermissions()
                        .stream()
                        .map(p -> p.getUserPermission().getRole()).collect(Collectors.toSet())).flatMap(Collection::stream).collect(Collectors.toList());
        MyJwtUserDetails userDetails = new MyJwtUserDetails();
        userDetails.setUsername(username);
        userDetails.setPassword(password);
        userDetails.setRoles(rolesCollect);
        String token = JwtTokenUtils.TOKEN_PREFIX + JwtTokenUtils.createToken(userDetails, 365);
        return JsonResult.oK(token);
    }

    @Override
    public JsonResult login( UserLoginRequest entity) throws Exception {
        String recaptchaToken = entity.getRecaptchaToken();
        Boolean googleTokenExists = jedisCluster.exists(PreConstants.GOOGLETOKENPRE + recaptchaToken);
        String username = entity.getUsername();
        if(!googleTokenExists){
            Boolean check = myUtils.GoogleRecaptcha(request, recaptchaToken);
            if(!check) return JsonResult.build(DefaultConstants.ERROR_IMGCODE,"人机验证结果校验失败,请重新再试!");
            jedisCluster.setex(PreConstants.GOOGLETOKENPRE + recaptchaToken,90, username);
        }
        Optional<UserLogin> byUsernameEquals = userLoginResipority.findByUsernameEquals(username);
        if(!byUsernameEquals.isPresent()){
            throw new ServiceException("账号名或密码错误");
        }
        UserLogin userLogin = byUsernameEquals.get();
        String password = entity.getPassword();
        String salt = userLogin.getSalt();
        String saltedPassword = MyUtils.CreateSaltedPassword(password, salt);
        if(!userLogin.getPassword().equals(saltedPassword)){
            throw new ServiceException("账号名或密码错误");
        }
        return JsonResult.oK();
    }

    @Override
    public JsonResult logout() {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        String username = JwtTokenUtils.getUsername(token);
        jedisCluster.del(PreConstants.USERNAMEPRE + username);
        return JsonResult.oK();
    }

    @Override
    public JsonResult getUserInfo() {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLoginDTO userInfo = jwtTokenUtils.getUserInfo(token);
        return JsonResult.oK(userInfo);
    }

    @Override
    public JsonResult getUserDetails() {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLoginDTO userInfo = jwtTokenUtils.getUserInfo(token);
        String uname = userInfo.getUname();
        Optional<UserLogin> byUsernameEquals = userLoginResipority.findByUsernameEquals(uname);
        if(!byUsernameEquals.isPresent()){
            return JsonResult.oK();
        }
        UserLogin userLogin = byUsernameEquals.get();
        UserLoginDetailsDTO userLoginDetailsDTO = new UserLoginDetailsDTO();
        BeanUtils.copyProperties(userLogin,userLoginDetailsDTO);
        if(userLoginDetailsDTO.getEmail() != null){
            userLoginDetailsDTO.setEmail(SensitiveInfoUtils.email(userLoginDetailsDTO.getEmail()));

        }
        if(userLoginDetailsDTO.getPhone() != null){
            userLoginDetailsDTO.setPhone(SensitiveInfoUtils.mobilePhone(userLoginDetailsDTO.getPhone()));

        }
        return JsonResult.oK(userLoginDetailsDTO);
    }

    @Transactional
    @Override
    public JsonResult updateUserInfo(UserUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLoginDTO userInfo = jwtTokenUtils.getUserInfo(token);
        Long id = userInfo.getId();
        Optional<UserLogin> byId = userLoginResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("用户不存在,无法更新");
        }
        UserLogin userLogin = byId.get();
        String nickname = entity.getNickname();
        Optional<UserLogin> byNicknameEquals = userLoginResipority.findByNicknameEquals(nickname);
        if(byNicknameEquals.isPresent()){
            throw new ServiceException("该昵称已经被使用,请尝试别的昵称.");
        }
        if(StringUtils.isNotEmpty(nickname)){
            userLogin.setNickname(nickname);
        }
        if(StringUtils.isNotEmpty(entity.getAvatar())){
            userLogin.setAvatar(entity.getAvatar());
        }
        userLoginResipority.save(userLogin);
        //updateUserInfoToNoSql(userLogin);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(userLogin.getId());
    }

    @Transactional
    @Override
    public JsonResult changeUserPasswordWithVerification(UserChangePwdRequest entity) {
        String vcode = entity.getVcode();
        VerificationCodeCheckRequest check1 = new VerificationCodeCheckRequest();
        VerificationCodeCheckRequest check2 = new VerificationCodeCheckRequest();

        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token, true);
        check1.setCode(userLogin.getPhone());
        check1.setVcode(vcode);
        check2.setCode(userLogin.getEmail());
        check2.setVcode(vcode);

        if(!vCodeExists(check1) && !vCodeExists(check2)){
            throw new ServiceException("验证码不存在,请重新输入");
        }

        Long id = userLogin.getId();
        Optional<UserLogin> byId = userLoginResipority.findById(id);
        if(!byId.isPresent()){
            throw new ServiceException("用户不存在,无法更新");
        }
        UserLogin updateEntity = byId.get();
        String password = entity.getPassword();
        String salt = userLogin.getSalt();
        String saltedPassword = MyUtils.CreateSaltedPassword(password, salt);
        updateEntity.setPassword(saltedPassword);
        userLoginResipority.save(updateEntity);
        return JsonResult.oK(updateEntity.getId());
    }

    @Transactional
    @Override
    public JsonResult addOrUpdateUserSettings(UserSettingsAddOrUpdateRequest entity) {
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token);
        Optional<UserSettings> byUserLogin = userSettingsResipority.findByUserLogin(userLogin);
        UserSettings userSettings;
        userSettings = byUserLogin.orElseGet(UserSettings::new);
        userSettings.setUserLogin(userLogin);
        userSettings.setLanguage(entity.getLanguage());
        userSettings.setCloseSiderBar(entity.getCloseSiderBar());
        userSettings.setFontSize(entity.getFontSize());
        userSettings.setFontColorNumber(entity.getFontColorNumber());
        userSettings.setShowTags(entity.getShowTags());
        userSettings.setShowTsukomi(entity.getShowTsukomi());
        userSettings.setShowOriginContent(entity.getShowOriginContent());
        userSettings.setBackgroundColorNumber(entity.getBackgroundColorNumber());
        userSettingsResipority.save(userSettings);
        //updateUserInfoToNoSql(userLogin);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(userSettings.getId());
    }

    @Transactional
    @Override
    public void updateUserInfoToNoSql(UserLogin userLogin) {
        try{
            Set<UserLoginRole> loginRole = userLogin.getLoginRole();
            String username = userLogin.getUsername();
            UserLoginDTO userVo = new UserLoginDTO();
            JwtTokenUtils.setUserVo(userVo,userLogin);
//            userLogin.getNovelSubscribes().forEach(each->{
//                Long tid = each.getId();
//            });
//            userLogin.getNovelHistories().forEach(each->{
//                Long tid = each.getId();
//            });
//            userLogin.getNovelLikes().forEach(each->{
//                Long tid = each.getId();
//            });
//            userVo.setAvatar(userLogin.getAvatar());
//            userVo.setId(userLogin.getId());
//            userVo.setNickname(userLogin.getNickname());
//            userVo.setStatus(userLogin.getStatus());
//            userVo.setZone(userLogin.getZone());
//            userVo.setNovelSubscribes(userLogin.getNovelSubscribes());
//            userVo.setNovelHistories(userLogin.getNovelHistories());
//            userVo.setNovelLikes(userLogin.getNovelLikes());
//            userVo.setNovelCommentLikes(userLogin.getNovelCommentLikes());
//            userVo.setNovelCommentDislikes(userLogin.getNovelCommentDislikes());

            List<String> rolesCollect = loginRole
                    .stream()
                    .map(each -> each.getUserRole().getUserRolePermissions()
                            .stream()
                            .map(p -> p.getUserPermission().getRole()).collect(Collectors.toSet())).flatMap(Collection::stream).collect(Collectors.toList());
            userVo.setRoles(rolesCollect);
            jedisCluster.setex(PreConstants.USERNAMEPRE + username,DefaultConstants.ONEDAYSECOND * 3,objectMapper.writeValueAsString(userVo));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException("更新用户临时信息失败");
        }

    }

    @Transactional
    @Override
    public JsonResult bindMail(VerificationCodeCheckRequest entity) {
        JsonResult checkResult = verificationCodeService.vCodeCheck(entity);
        if(!checkResult.getCode().equals(DefaultConstants.SUCCESS_SERVICE)){
            throw new ServiceException(checkResult.getMessage());
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token,true);
        Integer rebindChance = userLogin.getRebindChance();
        if(userLogin.getEmail() != null){
            if(rebindChance == null || rebindChance.equals(0)){
                throw new ServiceException("已绑定过邮箱,无法再绑定");
            }
        }
        userLogin.setEmail(entity.getCode());
        userLogin.setRebindChance(rebindChance != null && rebindChance >= 1 ? rebindChance - 1 : 0);
        userLoginResipority.save(userLogin);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(userLogin.getId());
    }

    @Override
    public JsonResult bindPhone(VerificationCodeCheckRequest entity) {
        JsonResult checkResult = verificationCodeService.vCodeCheck(entity);
        if(!checkResult.getCode().equals(DefaultConstants.SUCCESS_SERVICE)){
            throw new ServiceException(checkResult.getMessage());
        }
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token,true);
        Integer rebindChance = userLogin.getRebindChance();
        if(userLogin.getPhone() != null){
            if(rebindChance == null || rebindChance.equals(0)){
                throw new ServiceException("已绑定过邮箱,无法再绑定");
            }
        }
        userLogin.setPhone(entity.getCode());
        userLogin.setRebindChance(rebindChance >= 1 ? rebindChance - 1 : 0);
        userLoginResipority.save(userLogin);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(userLogin.getId());
    }

    @Transactional
    @Override
    public JsonResult rebindMail(VerificationCodeRebindCheckRequest entity) {
        String vcode = entity.getVcode();
        String vconfrimcode = entity.getVconfrimcode();
        String newAddress = entity.getCode();
        //
        String token = JwtTokenUtils.getTokenFromHeader(request);
        UserLogin userLogin = jwtTokenUtils.getUserLogin(token,true);

        VerificationCodeCheckRequest check1 = new VerificationCodeCheckRequest();
        check1.setCode(userLogin.getEmail());
        check1.setVcode(vcode);

        VerificationCodeCheckRequest check2 = new VerificationCodeCheckRequest();
        check2.setCode(newAddress);
        check2.setVcode(vconfrimcode);
        if(!vCodeExists(check1) && !vCodeExists(check2)){
            throw new ServiceException("验证码不存在,请重新输入");
        }
        userLogin.setEmail(newAddress);
        userLoginResipority.save(userLogin);
        symbolUserNeedUpdate(userLogin);
        return JsonResult.oK(userLogin.getId());
    }

    private void symbolUserNeedUpdate(UserLogin userLogin){
        String key = PreConstants.REDISUSERINFONEEDUPDATE + userLogin.getUsername();
        jedisCluster.setex(key,DefaultConstants.ONEDAYSECOND * 3,"subscribe");
    }
}
