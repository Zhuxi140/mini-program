package src.main.java.com.zhuxi.pojo.DTO.product;

public class PIdSnowFlake {

    private Long id;
    private Long snowflakeId;

    public PIdSnowFlake() {
    }

    public PIdSnowFlake(Long id, Long snowflakeId) {
        this.id = id;
        this.snowflakeId = snowflakeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSnowflakeId() {
        return snowflakeId;
    }

    public void setSnowflakeId(Long snowflakeId) {
        this.snowflakeId = snowflakeId;
    }
}
