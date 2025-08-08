package com.zhuxi.service.Cache;

import com.zhuxi.utils.RedisUntil;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import src.main.java.com.zhuxi.pojo.DTO.User.UserBasicDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserBasicVO;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class LoginRedisCache {

    private RedisCacheProperties rCP;
    private RedisUntil redisUntil;

    public LoginRedisCache(RedisCacheProperties rCP, RedisUntil redisUntil) {
        this.rCP = rCP;
        this.redisUntil = redisUntil;
    }

    public String getLogOutKey(String token){
        return rCP.getUserCache().getLogoutPrefix() + ":" + token;
    }


    public String getUserInfoKey(String openId){
        return rCP.getUserCache().getUserInfoPrefix() + ":" + openId;
    }

    public void initUserInfo(List<UserBasicDTO>  list){
        redisUntil.executePipeline(p->{
            HashMap<Object, Object> HashMap = new HashMap<>();
            HashOperations<String, Object, Object> hash = p.opsForHash();
            list.forEach(userBasicDTO -> {
                String key = getUserInfoKey(userBasicDTO.getOpenId());
                HashMap.put("id", userBasicDTO.getId());
                HashMap.put("name", userBasicDTO.getName());
                HashMap.put("avatar", userBasicDTO.getAvatar());
                hash.putAll(key,HashMap);
                p.expire(key,7, TimeUnit.DAYS);
            });
        });
    }

    public void initUserBasic(UserBasicDTO user){
        String openId = user.getOpenId();
        HashMap<Object, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("avatar", user.getAvatar());
        redisUntil.hPutMap(getUserInfoKey(openId),data);
        redisUntil.expire(getUserInfoKey(openId),7,TimeUnit.DAYS);
    }

    public Long deleteUser(String openId){
        return redisUntil.hDelete(getUserInfoKey(openId),"name","avatar");
    }

    public UserBasicVO getUserInfo(String openId){
        List<Object> data = redisUntil.hMultiGet(getUserInfoKey(openId), List.of("name", "avatar"));
        if (data == null || data.isEmpty()){
            return null;
        }
        return new UserBasicVO(
                (String) data.get(0),
                (String) data.get(1),
                null,
                openId
        );
    }

    public Long getUserId(String openId){
        Object id = redisUntil.hGet(getUserInfoKey(openId), "id");
        if (id instanceof Long){
            return (Long) id;
        }else{
            return Long.valueOf((Integer)id);
        }
    }

    public void saveUserId(String openId,Long userId){
        redisUntil.hPut(getUserInfoKey(openId),"id",userId);
    }
    public void saveSessionKey(String openId,String sessionKey){
        redisUntil.hPut(getUserInfoKey(openId),"sessionKey",sessionKey);
    }

    public void DeleteOneFiled(String openId,String field){
        redisUntil.hDelete(getUserInfoKey(openId), field);
    }

    public void saveToken(String token,String value,long time,TimeUnit unit){
        String logOutKey = getLogOutKey(token);
        redisUntil.setStringValue(logOutKey,value,time,unit);
    }

    public String getTokenValue(String token){
        return redisUntil.getStringValue(getLogOutKey(token));
    }
}
