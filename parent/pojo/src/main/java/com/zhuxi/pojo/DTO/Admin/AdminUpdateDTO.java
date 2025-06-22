package src.main.java.com.zhuxi.pojo.DTO.Admin;

import io.swagger.v3.oas.annotations.media.Schema;
import src.main.java.com.zhuxi.pojo.entity.Role;


public class AdminUpdateDTO {
    @Schema(description = "管理员id",requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer id;
    @Schema(description = "真实名字",requiredMode = Schema.RequiredMode.REQUIRED)
        private String realName;
    @Schema(description = "权限",requiredMode = Schema.RequiredMode.REQUIRED)
        private Role role;
    @Schema(description = "是否启用",requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer status;

    public AdminUpdateDTO(Integer id, String realName, Role role, Integer status) {
        this.id = id;
        this.realName = realName;
        this.role = role;
        this.status = status;
    }

    public AdminUpdateDTO() {
    }

    public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }



        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }


}
