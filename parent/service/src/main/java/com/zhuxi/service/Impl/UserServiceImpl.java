package com.zhuxi.service.Impl;


import com.zhuxi.Constant.Message;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.UserMapper;
import com.zhuxi.service.TxService.UserTxService;
import com.zhuxi.service.UserService;
import com.zhuxi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserLoginVO;


@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserTxService userTxService;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserTxService userTxService, JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.userTxService = userTxService;
    }



    @Override
    public UserLoginVO loginPhone(String phone) {
        return null;
    }


    /**
     * 登录
     */
    @Override
    public Result<UserLoginDTO> loginTest(UserLoginDTO userLoginDTO) {

        if(userLoginDTO.getOpenId() == null || userLoginDTO.getOpenId().isBlank())
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        UserLoginDTO userExist = userTxService.isUserExist(userLoginDTO.getOpenId());

        return Result.success(Message.LOGIN_SUCCESS,userExist);
    }



    /**
     * 修改用户信息
     */
    @Override
    @Transactional
    public Result<Void> updateUserInfo(String token,UserUpdateDTO userUpdateDTO) {


        Claims claims = jwtUtils.parseToken(token);
        if(claims == null)
            return Result.error(Message.JWT_ERROR);
        userUpdateDTO.setId(claims.get("id",Long.class));

        if(userUpdateDTO.getId() == null)
            return Result.error(Message.BODY_NO_MAIN_OR_IS_NULL);

        String nickName = userUpdateDTO.getNickName();
        String customAvatarUrl = userUpdateDTO.getCustomAvatarOss();

        if((nickName == null || nickName.isBlank())
           && (customAvatarUrl == null || customAvatarUrl.isBlank())
        )
            return Result.error(Message.AT_LEAST_ONE_FIELD);

        userTxService.updateUser(userUpdateDTO);

        return Result.success(Message.OPERATION_SUCCESS);
    }


}
