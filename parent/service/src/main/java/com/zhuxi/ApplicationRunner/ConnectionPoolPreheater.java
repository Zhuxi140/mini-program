
package com.zhuxi.ApplicationRunner;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.zhuxi.Exception.ConnectionPoolInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.IntStream;

@Service
public class ConnectionPoolPreheater {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolPreheater.class);


    public void preheatDruidPool(DruidDataSource dataSource,int minConnectCount, String validationQuery){
        if (dataSource == null){
            throw new IllegalArgumentException("数据库预热失败:  dataSource cannot be null");
        }
        try{
            if(!dataSource.isInited()){
                log.debug("数据源未初始化，开始初始化");
                dataSource.init();
            }

            log.debug("创建{}个物理连接并验证",minConnectCount);
            IntStream.range(0,minConnectCount/2).parallel().forEach(i->{
                try(DruidPooledConnection connection = dataSource.getConnection();)
                {
                    validateConnection(connection,validationQuery);
                }catch (SQLException e){
                    log.error("验证连接失败",e);
                }
            });
        }catch (SQLException e){
            throw new ConnectionPoolInitializationException("数据源初始化失败 :"  +  e.getMessage());
        }
    }

    private void validateConnection(Connection conn,String validationQuery) throws SQLException{
        log.debug("验证连接");
        try(
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(validationQuery);
        ){
            if(!resultSet.next()){
                throw new SQLException("验证查询未返回结果");
            }
            log.debug("验证成功");
        }catch (SQLException e){
            throw new SQLException("验证失败: " + e.getMessage());
        }
    }
}


