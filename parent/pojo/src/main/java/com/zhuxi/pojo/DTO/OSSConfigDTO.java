package src.main.java.com.zhuxi.pojo.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public class OSSConfigDTO {
    @Schema(description = "上传文件分类", requiredMode = Schema.RequiredMode.REQUIRED,
    allowableValues = {"product","spec","article","avatar"})
    private String category;

    @Schema(description = "上传文件类型(product(coverUrl、images),spec(coverUrl),article(coverUrl、content、images),avatar(avatar)",requiredMode = Schema.RequiredMode.REQUIRED,
    allowableValues = {"coverUrl","images","content","avatar"})
    private String type;

    @Schema(description = "文件后缀名(jpg、png、txt、html等)",requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileType;

    @Schema(description = "关联商品ID(分类为product时必填)",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @Schema(description = "关联文章ID(分类为article时必填)",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long articleId;

    @Schema(description = "规格ID(分类为product且类型为spec时使用)",requiredMode = Schema.RequiredMode.REQUIRED)
    private Long specId;

    public OSSConfigDTO(String category, String type, String fileType, Long productId, Long articleId, Long specId) {
        this.category = category;
        this.type = type;
        this.fileType = fileType;
        this.productId = productId;
        this.articleId = articleId;
        this.specId = specId;
    }

    public OSSConfigDTO() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }
}
