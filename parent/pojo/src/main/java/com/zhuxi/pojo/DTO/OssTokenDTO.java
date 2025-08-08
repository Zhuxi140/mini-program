package src.main.java.com.zhuxi.pojo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class OssTokenDTO {
    @Schema(description = "上传文件预处理url")
    private String url;
    @Schema(description = "上传文件名")
    private String objectName;

    public OssTokenDTO(String url, String objectName) {
        this.url = url;
        this.objectName = objectName;
    }

    public OssTokenDTO() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }


    public void clear(){
        url = null;
        objectName = null;
    }
}
