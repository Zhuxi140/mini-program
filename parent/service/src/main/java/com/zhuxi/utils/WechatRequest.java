package com.zhuxi.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.zhuxi.Exception.LoginException;
import com.zhuxi.Exception.WechatException;
import com.zhuxi.Result.wechat.AccessTokenReturnResult;
import com.zhuxi.Result.wechat.PhoneResult;
import com.zhuxi.service.Cache.WechatCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WechatRequest {


    @Value("${wechat-miniProgram.appId}")
    private String appId;
    @Value("${wechat-miniProgram.appSecret}")
    private String appSecret;
    private final WechatCache wechatCache;

    public WechatRequest(WechatCache wechatCache) {
        this.wechatCache = wechatCache;
    }

    public  JsonNode getUserInfo(String code){
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId,
                appSecret,
                code
        );

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            HttpGet httpGet = new HttpGet(url);
            try(CloseableHttpResponse response = httpClient.execute(httpGet)){
                HttpEntity entity = response.getEntity();
                String responseStr = EntityUtils.toString(entity,"UTF-8");
                EntityUtils.consume(entity);
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


    public PhoneResult getUserPhone(String code){
        String accessToken = wechatCache.getAccessionToken();
        if (accessToken == null){
            accessToken = getAccessToken();
            wechatCache.saveAccessionToken(accessToken, 7200, TimeUnit.SECONDS);
        }
        String url = String.format("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s",
                accessToken);

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){
            JSONObject requestBody = new JSONObject();
            requestBody.put("code", code);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            StringEntity body = new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON);
            httpPost.setEntity(body);
            try(CloseableHttpResponse response = httpClient.execute(httpPost)){
                HttpEntity entity = response.getEntity();
                String responseStr = EntityUtils.toString(entity, "UTF-8");
                PhoneResult phoneResult = JacksonUtils.jsonToObject(responseStr, PhoneResult.class);
                if (phoneResult == null){
                    throw new WechatException(" wx returns result which is null");
                }
                if (phoneResult.getErrCode() != 0){
                    throw new WechatException("获取手机号失败" + phoneResult.getErrMsg());
                }

                if (phoneResult.getPhoneInfo() == null){
                    throw new WechatException("获取手机号失败，手机信息为null");
                }

                return phoneResult;
            }
        } catch (IOException | JSONException e) {
            throw new WechatException(e.getMessage());
        }
    }

    private String getAccessToken(){
        String url = String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s",
                appId,
                appSecret);
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try(CloseableHttpResponse response = httpClient.execute(httpGet)){
                HttpEntity entity = response.getEntity();
                String responseStr = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                AccessTokenReturnResult accessTokenReturnResult = JacksonUtils.jsonToObject(responseStr, AccessTokenReturnResult.class);

                if (accessTokenReturnResult == null){
                    throw new LoginException(" wx returns result which is null");
                }

                if (accessTokenReturnResult.getErrCode() != 0){
                    throw new WechatException("获取access_token失败" + accessTokenReturnResult.getErrMsg());
                }
                String accessToken = accessTokenReturnResult.getAccessToken();
                if (accessToken == null || accessToken.isEmpty()){
                    throw new WechatException("wx return token that is null");
                }

                return accessToken;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
        throw new RuntimeException("获取access_token失败，未知错误：" + e.getMessage(), e);
    }
    }

}
