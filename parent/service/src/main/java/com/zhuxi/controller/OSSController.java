package com.zhuxi.controller;


import ch.qos.logback.core.util.StringUtil;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.service.TxService.ArticleTxService;
import com.zhuxi.service.TxService.ProductTxService;
import com.zhuxi.service.TxService.UserTxService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.DTO.OSSConfigDTO;
import src.main.java.com.zhuxi.pojo.DTO.OssRecord;
import src.main.java.com.zhuxi.pojo.DTO.OssTokenDTO;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.util.*;

@Slf4j
@RestController
@Tag(name = "OSS接口")
public class OSSController {

    private final JwtUtils jwtUtils;
    private final ProductTxService productTxService;
    private final UserTxService userTxService;
    private final ArticleTxService articleTxService;
    private final Map<String, PublicKey> publicKeyCache = new HashMap<>();

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

    @Value("${oss.callback-url}")
    private String callbackUrl;

    public OSSController(JwtUtils jwtUtils,UserTxService userTxService, ProductTxService productTxService, ArticleTxService articleTxService) {
        this.jwtUtils = jwtUtils;
        this.userTxService = userTxService;
        this.productTxService = productTxService;
        this.articleTxService = articleTxService;
    }


    @PostMapping("/oss/token")
    @Operation(summary = "获取OSS上传token", description = "严格遵守规定值传输")
    public List<OssTokenDTO> getOssToken(
            @Parameter(description = "JWT", hidden = true)
            @RequestHeader("Authorization") String token,
            @Parameter(description = "上传文件", required = true)
            @RequestBody List<OSSConfigDTO> configs
    ){

        Claims claims = jwtUtils.parseToken(token);
        if (claims == null)
            throw new RuntimeException(Message.JWT_IS_NULL);


        //返回
        return categoryDeal( claims,configs);

    }


    @PostMapping("/oss/upload")
    @Operation(summary = "上传回调（供阿里云OSS后台使用用 可忽略）")
    public Result<Void> uploadCallback(
            @Parameter(description = "请求头组",hidden = true)
            @RequestHeader
            Map<String,String> mapHeaders,
            @Parameter(description = "请求体组",hidden = true)
            @RequestBody
            Map<String,String> mapBody
    ){
        for(String headers : mapHeaders.keySet()){  // 遍历所有请求头
            log.info("Header: {}  Data : {}", headers, mapHeaders.get(headers));
        }
        log.warn("_____________________________________________________________________");
        for(String body : mapBody.keySet()){  // 遍历所有请求体
            log.info("Body: {}  Data : {}", body, mapBody.get(body));
        }

        // 验证签名
        if(!checkCallback(mapHeaders, mapBody))
            return Result.error(Message.PARAM_ERROR);

        // 解析回调数据
        CallbackData callbackData = parseCallback(mapHeaders, mapBody);

        // 业务处理
        callbackDataDeal(callbackData);

        return Result.success(Message.OPERATION_SUCCESS);
    }

    //业务 处理
    private void callbackDataDeal(CallbackData callbackData){
        if(callbackData == null){
            log.error(Message.CALLBACK_IS_NULL);
            return;
        }

        OssRecord record = new OssRecord();
        record.setId(callbackData.Id);

        record.setObjectName(callbackData.objectName);
        record.setBucketName(callbackData.bucket);
        record.setSize(callbackData.size);
        record.setMimeType(callbackData.mimeType);
        record.setCategoryType(callbackData.categoryType);


        switch (callbackData.categoryType){
            case "avatar_avatar":{
                log.info("avatar_avatar________________执行中");

            }
                break;
            case "product_cover": {
                log.info("product_cover________________执行中");
                /*productTxService.addBasePics(callbackData.objectName, callbackData.images, callbackData.productId);*/
            }
                break;
            case "product_images": {
                log.info("product_images________________执行中");
            }
                break;
            case "article_cover": {
                log.info("article_cover________________执行中");
            }
                break;
            case "article_contentImages":{
                log.info("article_contentImages________________执行中");
            }
                break;
            case "article_content":{
                log.info("article_content________________执行中");
            }
                break;
            default: log.info("未定义的callbackData");
        }
    }

    // 解析回调数据
    private CallbackData parseCallback(Map<String,String> mapHeaders, Map<String,String> mapBody){
        CallbackData callbackData = new CallbackData();
        callbackData.bucket = mapBody.get("bucket");
        callbackData.size = Long.parseLong(mapBody.get("size"));
        callbackData.mimeType = mapBody.get("mimeType");
        callbackData.objectName = mapBody.get("object");
        callbackData.Id = Long.valueOf(mapHeaders.get("x-oss-var-x-id"));
        callbackData.productId = Long.parseLong(mapHeaders.get("x-oss-var-x-productid"));
        callbackData.articleId = Long.parseLong(mapHeaders.get("x-oss-var-x-articleid"));
        callbackData.specId = Long.parseLong(mapHeaders.get("x-oss-var-x-specid"));
        callbackData.categoryType = mapHeaders.get("x-oss-var-x-categorytype");

        return callbackData;
    }

    // 检查回调
    private boolean checkCallback(Map<String,String> headers, Map<String,String> mapBody){
        String Auth = headers.get("Authorization");
        String publicKey = headers.get("x-oss-pub-key");

        String id = headers.get("x-oss-var-x-id");
        String categoryType = headers.get("x-oss-var-x-categorytype");

        if(StringUtil.isNullOrEmpty( Auth) || StringUtil.isNullOrEmpty(publicKey))
            return false;

    try {
        PublicKey publicKey1 = getPublicKey(publicKey);
        String path = callbackUrl;

        Map<String,String> allParams = new HashMap<>(mapBody);
        allParams.put("id", id);
        allParams.put("categoryType", categoryType);

        String query = buildQueryString(allParams);
        String signString = path + "\n" + query;

        byte[] decode = Base64.getDecoder().decode(Auth);

        Signature SHA1 = Signature.getInstance("SHA1withRSA");
        SHA1.initVerify(publicKey1); // 初始化签名验证器
        SHA1.update(signString.getBytes(StandardCharsets.UTF_8));

        return SHA1.verify(decode);
    }catch (Exception e){
        log.warn("Public key error: {}", e.getMessage());
        return false;
    }

    }

    // 构建查询字符串
    private String buildQueryString(Map<String,String>  mapBody){
        return mapBody.entrySet().stream() // 获取所有参数项 转换为 Stream
                .filter(entry -> !"callback".equals(entry.getKey()))  // 排除callback参数
                .sorted(Map.Entry.comparingByKey()) // 按key排序
                .map(entry -> entry.getKey() + "=" + entry.getValue()) // 构建参数字符串
                .reduce((s1, s2) -> s1 + "&" + s2) // 将参数字符串连接起来
                .orElse(""); // 如果没有参数，返回空字符串
    }

    // 获取公钥并缓存
    private PublicKey getPublicKey(String publicKey) throws Exception{
        if(publicKeyCache.containsKey(publicKey)){
            return publicKeyCache.get(publicKey);
        }

        if(!publicKey.startsWith("https://gosspublic.alicdn.com/")){
            throw new RuntimeException(Message.INVALID_PUBLIC_KEY_URL);
        }

        URL url = new URL(publicKey);
        URLConnection conn = url.openConnection();

        try(InputStream inputStream = conn.getInputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            // 解析公钥
            PEMParser pemParser = new PEMParser(inputStreamReader);
            Object o = pemParser.readObject();

            if(o instanceof SubjectPublicKeyInfo subjectPublicKeyInfo){
                JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
                PublicKey publicKey1 = jcaPEMKeyConverter.getPublicKey(subjectPublicKeyInfo);

                publicKeyCache.put(publicKey, publicKey1);

                return publicKey1;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }

        return null;
    }

    // 分类处理
    private List<OssTokenDTO> categoryDeal(Claims  claims, List<OSSConfigDTO> configs) {
        Long id = claims.get("id", Long.class);
        String s = claims.get("role", String.class);
        Map<String,String> map = new HashMap<>();
        OssTokenDTO ossTokenDTO = new OssTokenDTO();
        List<OssTokenDTO> ossTokenDTOS = new ArrayList<>();
        for(OSSConfigDTO config : configs) {
            checkType(config.getFileType());
            switch (config.getCategory().toLowerCase()) {
                case "avatar": {
                    if (!(s).equalsIgnoreCase("user"))
                        throw new RuntimeException(Message.ROLE_ERROR);
                    String s1 = generateObjectName(config.getType(), config.getCategory(), config.getFileType(), id, config.getProductId(), config.getArticleId(), config.getSpecId());
                    map = generateUrl(s1, config.getFileType(), config.getCategory(), config.getType(), id, config.getProductId(), config.getArticleId(), config.getSpecId());
                    ossTokenDTO.setUrl(map.get("url"));
                    ossTokenDTO.setObjectName(map.get("objectName"));
                    ossTokenDTOS.add(ossTokenDTO);
                } break;
                case "product", "article": {
                    checkRole(s);
                    String s1 = generateObjectName(config.getType(), config.getCategory(), config.getFileType(), id, config.getProductId(), config.getArticleId(), config.getSpecId());
                    map = generateUrl(s1, config.getFileType(), config.getCategory(), config.getType(), id, config.getProductId(), config.getArticleId(), config.getSpecId());
                    ossTokenDTO.setUrl(map.get("url"));
                    ossTokenDTO.setObjectName(map.get("objectName"));
                    ossTokenDTOS.add(ossTokenDTO);
                } break;
                default:
                    throw new RuntimeException(Message.NO_CATEGORY);
            }
        }

        return ossTokenDTOS;
    }

    // 生成objectName
    private String generateObjectName(String type,String category,String fileType,Long id,Long productId,Long articleId,Long specId){
        String Path = switch ( category + "_" + type){
            case "product_cover" -> "product/Id" +  productId + "/admin" + id + "/cover/";
            case "product_images" -> "productId/Id" +  productId + "/admin" + id + "/images/";
            case "product_spec" ->   "product/Id" +  productId + "/admin" + id + "/spec" + specId + "/";
            case "article_cover" -> "article/Id" + articleId + "/admin" + id + "/cover/";
            case "article_content" -> "article/Id" + articleId + "/admin" + id + "/content/";
            case "article_contentImages" -> "article/Id" +articleId + "/admin" + id + "/contentImages/";
            case "avatar_avatar" -> "avatar/user" + id + "/";
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
    private Map<String,String> generateUrl(String objectName, String fileType,String category,String type,Long id,Long productId,Long articleId,Long specId) {
        OSS build = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    try{

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
        request.setMethod(HttpMethod.PUT);
        request.setExpiration(new Date(System.currentTimeMillis() + expireTime));
        String mimeType = getMimeType(fileType);
        request.setContentType(mimeType);
        log.info(" mimeType : {}", mimeType );

       /* Callback callback = new Callback();
        callback.setCallbackUrl(callbackUrl);
        callback.setCalbackBodyType(Callback.CalbackBodyType.URL); // 设置回调类型为URL
        callback.addCallbackVar("x:id", id.toString());
        callback.addCallbackVar("x:categoryType", category + "_" + type);

        String callbackBody = """
                bucket=${bucket}&object=${object}&size=${size}&mimeType=${mimeType}&id=${x:id}&categoryType=${x:categoryType}
                """;
        callback.setCallbackBody(callbackBody);

        Map<String, String> callbackParams = new HashMap<>();
        callbackParams.put("callback", callback.buildCallbackJSONString());
        request.setCallback(callbackParams);*/

        Map<String,String> callbackMap = new HashMap<>();
        callbackMap.put("callback", callbackUrl);
        callbackMap.put("callbackBody", """
                bucket=${bucket}&object=${object}&size=${size}&mimeType=${mimeType}&id=${x:id}&categoryType=${x:categoryType}
                """);
        callbackMap.put("callbackBodyType", " application/x-www-form-urlencoded");
        callbackMap.put("x:id", id.toString());
        callbackMap.put("x:productId", productId.toString());
        callbackMap.put("x:articleId", articleId.toString());
        callbackMap.put("x:specId", specId.toString());
        callbackMap.put("x:categoryType", category + "_" + type);



        // 构建JSON对象
        String callbackJson = new JSONObject(callbackMap).toString();
        String base64Callback = Base64.getEncoder().encodeToString(callbackJson.getBytes());
        request.addQueryParameter("callback",base64Callback);

        URL url = build.generatePresignedUrl(request);

        return Map.of(
                "url", url.toString(),
                "objectName", objectName
                );

    } catch (JSONException e) {
        throw new RuntimeException(e);
    } finally {
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

    // 回调数据类
    public static class CallbackData {
        String bucket;
        String objectName;
        long size;
        String mimeType;
        Long Id;
        Long productId;
        Long articleId;
        Long specId;
        String categoryType;
    }


}



