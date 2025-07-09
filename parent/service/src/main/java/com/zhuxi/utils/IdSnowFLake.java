package com.zhuxi.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

@Component
public class IdSnowFLake {

    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);


    public  String getIdString(){
        return SNOWFLAKE.nextIdStr();
    }

    public  Long getIdInt(){
        return SNOWFLAKE.nextId();
    }


}
