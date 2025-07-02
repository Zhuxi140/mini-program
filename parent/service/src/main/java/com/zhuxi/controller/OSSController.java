package com.zhuxi.controller;


import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.zhuxi.Constant.Message;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Slf4j
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

    @GetMapping("/oss/token")
    @Operation(summary = "获取OSS上传token")
    public Map<String, String> getOssToken(
            @Parameter(description = "JWT", hidden = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "上传文件分类(product、article、avatar)", required = true)
            String category,
                @Parameter(description = "上传文件类型(product(封面图(cover)或详细图(images))、article(文章内容(content),文章封面(cover),文章图片(contentImages))、avatar(头像传avatar)", required = true)
            String type,
            @Parameter(description = "上传文件后缀名", required = true)
            String fileType
    ){

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null)
            return Map.of("url", Message.JWT_IS_NULL);

        Map<String, String> stringStringMap = categoryDeal(category, type, fileType, claims);

        return Map.of(
                "objectName", stringStringMap.get("objectName"),
                "url", stringStringMap.get("url")

        );

    }

    // 分类处理
    private Map<String,String> categoryDeal(String category,String type,String fileType,Claims  claims) {
        String s = claims.get("role", String.class);
        checkType(fileType);
        switch (category.toLowerCase()){
            case "avatar":{
                if(!(s).equalsIgnoreCase("user"))
                    throw new RuntimeException(Message.ROLE_ERROR);
                String s1 = generateObjectName(claims, type, category, fileType);
                return generateUrl(s1, fileType);
            }
            case "product", "article":{
                checkRole(s);
                String s1 = generateObjectName(claims, type, category, fileType);
                return generateUrl(s1, fileType);
            }
            default:
                throw new RuntimeException(Message.NO_CATEGORY);
        }

    }

    // 生成objectName
    private String generateObjectName(Claims  claims,String type,String category,String fileType){
        Long id = claims.get("id", Long.class);
        String Path = switch ( category + "_" + type){
            case "product_cover" -> "product/" + id + "/cover/";
            case "product_images" -> "product/" + id + "/images/";
            case "article_cover" -> "article/" + id + "/cover/";
            case "article_content" -> "article/" + id + "/content/";
            case "article_contentImages" -> "article/" + id + "/contentImages/";
            case "avatar_avatar" -> "avatar/" + id + "/";
            default -> throw new RuntimeException(Message.CATEGORY_FIT_ERROR);
        };

        String s = String.valueOf(System.currentTimeMillis());
        String randomID = UUID.randomUUID().toString().substring(0, 8);

        return Path + s + "-" + randomID + "." + fileType.toLowerCase();
    }

    // 检查文件的合法性
    private void checkType(String fileType){
        switch ( fileType.toLowerCase()){
            case "jpg", "jpeg", "webp", "png", "html", "txt":
                break;
            default:
                throw new RuntimeException(Message.FILE_TYPE_ERROR);
        }
    }

    // 检查角色
    private void checkRole(String role){
        if(!(role).equalsIgnoreCase("admin") && !(role).equalsIgnoreCase("super_admin"))
            throw new RuntimeException(Message.ROLE_ERROR);
    }

    // 生成带签名的url
    private Map<String,String> generateUrl(String objectName, String fileType) {
        OSS build = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    try{

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
        request.setMethod(HttpMethod.PUT);
        request.setExpiration(new java.util.Date(System.currentTimeMillis() + expireTime));
        String mimeType = getMimeType(fileType);
        request.setContentType(mimeType);
        log.info(" mimeType : {}", mimeType );
        URL url = build.generatePresignedUrl(request);

        return Map.of(
                "url", url.toString(),
                "objectName", objectName
                );

    }finally {
        build.shutdown();
    }
    }


    // 获取文件类型
    private String getMimeType(String suffix) {
        return switch (suffix.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "txt" -> "text/plain";
            case "html" -> "text/html";
            case "webp" -> "image/webp";

            default -> throw new RuntimeException(Message.FILE_TYPE_ERROR);
        };

    }

}



