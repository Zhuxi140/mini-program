package com.zhuxi.controller.Admin;


import com.zhuxi.Result.PageResult;
import com.zhuxi.Result.Result;
import com.zhuxi.annotation.RequireRole;
import com.zhuxi.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.zhuxi.pojo.VO.Admin.AdminUserVO;
import src.main.java.com.zhuxi.pojo.entity.Role;

@RestController
@RequestMapping("/adminUsers")
@Tag(name = "管理端接口")
public class AdminUserController {

    private final AdminUserService userService;

    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }

    /**
     * 获取用户列表
     */
    @GetMapping
    @RequireRole(Role.ADMIN)
    @Operation(
            summary = "获取用户列表",
            description = "分页获取所有用户信息列表"
    )
    public Result<PageResult<AdminUserVO>> getListUser(
           @Parameter(description = "上一页最后一条数据的id(第一次查询时不需填写，后续需要根据响应数据来填写)")
           Long lastId,
           @Parameter(description = "每页数量(后端默认10)")
           @RequestParam(defaultValue = "10") Integer pageSize,
           @Parameter(description = "排序方式, 为1时为降序",required = true)
           Integer DESC
    ){

           return userService.getListUser(lastId, pageSize,DESC);
       }


   /**
    * 禁用用户
    */
   @Operation(
           summary = "禁用用户",
           description = "禁用用户"
   )
   @GetMapping("/{status}")
   @RequireRole(Role.SUPER_ADMIN)
       public Result<Void> disableUser(
               @Parameter(description = "禁用状态, 0为禁用, 1为启用",required = true)
               @PathVariable Integer status,
               @Parameter(description = "用户id",required = true)
               @RequestParam("userId") Long id
   ){
       return userService.disableUser(status, id);
   }

}
