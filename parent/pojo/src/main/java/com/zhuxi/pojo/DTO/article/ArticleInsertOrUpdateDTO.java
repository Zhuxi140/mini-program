package src.main.java.com.zhuxi.pojo.DTO.article;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ArticleInsertOrUpdateDTO {

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "状态(1草稿 2 发布  默认不设置为1草稿状态)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer status;

    @Schema(description = "类型(1公告 2 新闻/文章", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer type;


    public ArticleInsertOrUpdateDTO(String title,Integer status, Integer type) {
        this.title = title;
        this.status = status;
        this.type = type;
    }

    public ArticleInsertOrUpdateDTO() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }


}
