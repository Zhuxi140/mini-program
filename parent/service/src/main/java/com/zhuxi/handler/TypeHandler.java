package com.zhuxi.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class TypeHandler extends BaseTypeHandler<List<String>> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i,objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            throw new SQLException("JSON转换失败",e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    private List<String> parseJson(String json) throws SQLException{
        if(json == null || json.isEmpty())
            return Collections.emptyList();
        try {
            JavaType javaType =  objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class);
            return objectMapper.readValue(json,javaType);
        } catch (JsonProcessingException e) {
            throw new SQLException("JSON转换失败",e);
        }
    }


    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return List.of();
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return List.of();
    }
}
