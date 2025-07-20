package com.zhuxi.ApplicationRunner;

import com.zhuxi.Exception.RedisException;
import com.zhuxi.utils.properties.RedisCacheProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class RedistPreheater {
    private static final Logger log = LoggerFactory.getLogger(RedistPreheater.class);

    private  final RedisConnectionFactory redisConnectionFactory;
    private final RedisCacheProperties rCP;
    private volatile boolean redisReady = false;

    public RedistPreheater(RedisConnectionFactory redisConnectionFactory, RedisCacheProperties rCP) {
        this.redisConnectionFactory = redisConnectionFactory;
        this.rCP = rCP;
    }

    public void ensureRedisReady(){

        int attempt = 0;
        boolean success = false;

        while (attempt < rCP.getMaxAttempts() && !success) {
            attempt++;
            try {
                log.debug("尝试连接Redis (尝试 #{})", attempt + 1);
                success = testRedisConnection();

                if (!success) {
                    long waitTime = rCP.getBaseWaitMs() * (long) Math.pow(2, attempt - 1);
                    log.warn("Redis连接失败，{}ms后重试...", waitTime);
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Redis预启动被中断");
                break;
            } catch (Exception e) {
                log.error("Redis连接异常", e);
            }
        }

        if (success) {
            redisReady = true;
        } else {
            log.error("Redis预启动失败！已达到最大尝试次数: {}", rCP.getMaxAttempts());
            throw new RedisException("Redis服务未就绪");
        }
    }

    private boolean testRedisConnection(){
        try(RedisConnection connection = redisConnectionFactory.getConnection()){
            String ping = connection.ping();
            boolean success = "PONG".equals(ping);
            if ( success){
                log.debug("Redis连接成功(预热):{}",ping);
                return true;
            }else{
                log.warn("Redis连接测试失败(预热){}",ping);
            }
        }catch(Exception e){
            log.warn("Redis连接异常(预热):{}",e.getMessage());
        }

        return false;
    }

    public boolean isRedisReady(){
        return redisReady;
    }



}
