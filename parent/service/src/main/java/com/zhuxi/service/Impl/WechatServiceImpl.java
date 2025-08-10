package com.zhuxi.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.WechatException;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.Result.wechat.PhoneInfoResult;
import com.zhuxi.Result.wechat.PhoneResult;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import com.zhuxi.service.business.WechatService;
import com.zhuxi.utils.IdSnowFLake;
import com.zhuxi.utils.JwtUtils;
import com.zhuxi.utils.WechatRequest;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.zhuxi.pojo.DTO.User.LoginMQDTO;
import com.zhuxi.pojo.DTO.User.UserBasicDTO;
import com.zhuxi.pojo.VO.User.UserBasicVO;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class WechatServiceImpl implements WechatService {

    private final IdSnowFLake idSnowFLake;
    private final WechatAuthTxService wechatAuthTxService;
    private final LoginRedisCache loginRedisCache;
    private final JwtUtils jwtUtils;
    private final WechatRequest wechatRequest;
    private final RabbitTemplate rabbitTemplate;


    public WechatServiceImpl(IdSnowFLake idSnowFLake, WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache, JwtUtils jwtUtils, WechatRequest wechatRequest, RabbitTemplate rabbitTemplate) {
        this.idSnowFLake = idSnowFLake;
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
        this.jwtUtils = jwtUtils;
        this.wechatRequest = wechatRequest;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * 登录
     */
    @Override
    @Transactional
    public Result<UserBasicVO> login(String code) {

        if (code == null || code.isEmpty()){
            return Result.error(MessageReturn.CODE_IS_NULL);
        }

        JsonNode userInfo = wechatRequest.getUserInfo(code);
        String openId = userInfo.get("openid").asText();
        if (openId == null){
            return Result.error(MessageReturn.OPEN_ID_IS_NULL);
        }
        String sessionKey = userInfo.get("session_key").asText();
        if (sessionKey == null){
            return Result.error(MessageReturn.SESSION_KEY_IS_NULL);
        }
        UserBasicVO userBasicVO;
        boolean isSave = false;
        try {
            if (wechatAuthTxService.isExist(openId)) {
                if (wechatAuthTxService.isBan(openId)){
                    return Result.error(MessageReturn.ACCOUNT_IS_BANING);
                }
                userBasicVO = loginRedisCache.getUserInfo(openId);
                if (userBasicVO == null) {
                    isSave=true;
                    userBasicVO = wechatAuthTxService.getUserBasicInfo(openId, true);
                    userBasicVO.setOpenid(openId);
                }
            } else {
                wechatAuthTxService.insert(idSnowFLake.getIdInt(), openId);
                userBasicVO = new UserBasicVO(null, null, null, openId);
            }
            boolean finalIsSave = isSave;
            UserBasicVO finalUserBasicVO = userBasicVO;
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
                @Override
                public void afterCommit() {
                    LoginMQDTO loginMQDTO = new LoginMQDTO();
                    loginMQDTO.setOpenId(openId);
                    loginMQDTO.setSessionKey(sessionKey);
                    if (finalIsSave){
                        loginMQDTO.setName(finalUserBasicVO.getName());
                        loginMQDTO.setAvatar(finalUserBasicVO.getAvatar());
                    }
                    rabbitTemplate.convertAndSend("wechat.Exchange","login",loginMQDTO,message -> {
                        MessageProperties props = message.getMessageProperties();
                        props.setMessageId(UUID.randomUUID().toString());
                        props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return message;
                    });
                }
            });
            return Result.success(MessageReturn.LOGIN_SUCCESS,userBasicVO);
        }catch (Exception e){
            loginRedisCache.DeleteOneFiled(openId,sessionKey);
            throw new transactionalException(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @Override
    @Transactional
    public Result<Void> getUserBasicInfo(String token,UserBasicDTO userBasicDTO) {
        if (userBasicDTO == null){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }
        String name = userBasicDTO.getName();
        String avatar = userBasicDTO.getAvatar();
        if (name == null || avatar == null ){
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);
        }

        Claims claims = jwtUtils.parseToken(token);
        String openid = claims.get("openid", String.class);

        try {
            Long userId = loginRedisCache.getUserId(openid);
            if (userId == null){
                userId = wechatAuthTxService.getUserId(openid);
            }
            userBasicDTO.setId(userId);
            wechatAuthTxService.insertUser(userBasicDTO);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
                @Override
                public void afterCommit() {
                    loginRedisCache.initUserBasic(userBasicDTO);
                }
            });
            return Result.success(MessageReturn.OPERATION_SUCCESS);
        }catch (Exception e){
            loginRedisCache.deleteUser(openid);
            throw new transactionalException(e.getMessage());
        }
    }

    /**
     * 登出
     */
    @Override
    public Result<Void> logout(String token, HttpServletRequest request, HttpServletResponse response) {
        Claims claims = jwtUtils.parseToken(token);
        Date expiration = claims.getExpiration();
        String jit = claims.getId();
        long ttl = TimeUnit.MILLISECONDS.toSeconds(expiration.getTime() - System.currentTimeMillis());
        if (ttl > 0){
            loginRedisCache.saveToken(token,jit,ttl,TimeUnit.SECONDS);
        }

        HttpSession session = request.getSession(false);
        if (session != null){
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            Arrays.stream( cookies)
                    .filter(cookie->"AUTH_TOKEN".equals(cookie.getName()) || "JSESSIONID".equals(cookie.getName()))
                    .forEach(cookie-> {
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    });
        }
        return Result.success(MessageReturn.LOGOUT_SUCCESS);
    }

    /**
     * 获取用户手机号
     */
    @Override
    public Result<Void> getUserPhone(String code,Long userId) {
        if (code==null || code.isEmpty()){
            return Result.error(MessageReturn.CODE_IS_NULL);
        }
        PhoneResult userPhone = wechatRequest.getUserPhone(code);
        PhoneInfoResult phoneInfo = userPhone.getPhoneInfo();
        String phoneNumber = phoneInfo.getPhoneNumber();
        if (phoneNumber == null){
            throw new WechatException("phone number is null");
        }
        wechatAuthTxService.InsertPhone(phoneNumber, userId);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }



}
