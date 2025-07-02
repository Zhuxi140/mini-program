package com.zhuxi.controller;


import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import src.main.java.com.zhuxi.pojo.DTO.OssTokenDTO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@Tag(name = "OSS接口")
public class OSSController {

    private final JwtUtils jwtUtils;

    @Value("${oss.access-key-id}")
    private String accessKeyId;

    @Value("${oss.access-key-secret}")
    private String accessKeySecret;

    @Value("${oss.bucket-name}")
    private String bucketName;

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.expire-time}")
    private long expireTime;

    public OSSController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/oss/token")
    @Operation(summary = "获取OSS上传token")
    @RequireRole(Role.ADMIN)
    public Result<OssTokenDTO> getOssToken(@RequestHeader("Authorization") String token){

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null)
            return Result.error(Message.JWT_IS_NULL);

        claims.get("id", Long.class);
        claims.get("role", Role.class);
        claims.get("username", String.class);



    }

}
