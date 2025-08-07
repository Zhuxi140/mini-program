package com.zhuxi.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.LoginException;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Cache.LoginRedisCache;
import com.zhuxi.service.Tx.WechatAuthTxService;
import com.zhuxi.service.business.WechatAuthService;
import com.zhuxi.utils.IdSnowFLake;
import com.zhuxi.utils.JacksonUtils;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import src.main.java.com.zhuxi.pojo.DTO.User.UserBasicDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserBasicVO;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class WechatAuthServiceImpl implements WechatAuthService {

    private final IdSnowFLake idSnowFLake;
    private final WechatAuthTxService wechatAuthTxService;
    private final LoginRedisCache loginRedisCache;
    private final JwtUtils jwtUtils;
    @Value("${wechat-miniProgram.appId}")
    private String appId;
    @Value("${wechat-miniProgram.appSecret}")
    private String appSecret;

    public WechatAuthServiceImpl(IdSnowFLake idSnowFLake, WechatAuthTxService wechatAuthTxService, LoginRedisCache loginRedisCache, JwtUtils jwtUtils) {
        this.idSnowFLake = idSnowFLake;
        this.wechatAuthTxService = wechatAuthTxService;
        this.loginRedisCache = loginRedisCache;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public Result<UserBasicVO> login(String code) {

        if (code == null || code.isEmpty()){
            return Result.error(MessageReturn.CODE_IS_NULL);
        }

        JsonNode userInfo = getUserInfo(code);
        String openId = userInfo.get("openid").asText();
        if (openId == null){
            return Result.error(MessageReturn.OPEN_ID_IS_NULL);
        }
        String sessionKey = userInfo.get("session_key").asText();
        if (sessionKey == null){
            return Result.error(MessageReturn.SESSION_KEY_IS_NULL);
        }
        UserBasicVO userBasicVO;
        try {
            if (wechatAuthTxService.isExist(openId)) {
                userBasicVO = loginRedisCache.getUserInfo(openId);
                if (userBasicVO == null) {
                    userBasicVO = wechatAuthTxService.getUserBasicInfo(openId, true);
                    userBasicVO.setOpenid(openId);
                }
            } else {
                wechatAuthTxService.insert(idSnowFLake.getIdInt(), openId);
                userBasicVO = new UserBasicVO(null, null, null, openId);
            }
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
                @Override
                public void afterCommit() {
                    loginRedisCache.saveSessionKey(openId, sessionKey);
                }
            });
            return Result.success(MessageReturn.LOGIN_SUCCESS,userBasicVO);
        }catch (Exception e){
            loginRedisCache.DeleteOneFiled(openId,sessionKey);
            throw new transactionalException(e.getMessage());
        }
    }

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

    @Override
    public Result<Void> logout(String token, HttpServletRequest request, HttpServletResponse response) {
        log.info("impl token : {}", token);
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
     * 向微信服务器发送请求获取用户信息
     */
    public JsonNode getUserInfo(String code){
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?" +
                        "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId,
                appSecret,
                code
        );

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet httpGet = new HttpGet(url);
            try(CloseableHttpResponse response = httpClient.execute(httpGet)){
                HttpEntity entity = response.getEntity();
                String responseStr = EntityUtils.toString(entity,"UTF-8");
                JsonNode result = JacksonUtils.jsonToJsonNode(responseStr);
                if (result == null){
                    throw new LoginException(" wx returns result which is null");
                }

                if (result.has("errcode")&& result.get("errcode").asInt() != 0){
                    String errMsg = result.get("errmsg").asText();
                    throw new LoginException("登录失败" + errMsg + "(errcode:" + result.get("errcode").asInt() + ")");
                }
                return result;
            }
        } catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
    }
}
