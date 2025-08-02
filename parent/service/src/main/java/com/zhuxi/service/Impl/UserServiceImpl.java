package com.zhuxi.service.Impl;


import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Result.Result;
import com.zhuxi.service.Tx.UserTxService;
import com.zhuxi.service.business.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.User.UserLoginDTO;
import src.main.java.com.zhuxi.pojo.DTO.User.UserUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.User.UserLoginVO;


@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserTxService userTxService;

    public UserServiceImpl(UserTxService userTxService) {
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
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        UserLoginDTO userExist = userTxService.isUserExist(userLoginDTO.getOpenId());

        return Result.success(MessageReturn.LOGIN_SUCCESS,userExist);
    }



    /**
     * 修改用户信息
     */
    @Override
    @Transactional
    public Result<Void> updateUserInfo(Long userId,UserUpdateDTO userUpdateDTO) {

        userUpdateDTO.setId(userId);

        if(userUpdateDTO.getId() == null)
            return Result.error(MessageReturn.BODY_NO_MAIN_OR_IS_NULL);

        String nickName = userUpdateDTO.getNickName();
        String customAvatarUrl = userUpdateDTO.getCustomAvatarOss();

        if((nickName == null || nickName.isBlank())
           && (customAvatarUrl == null || customAvatarUrl.isBlank())
        )
            return Result.error(MessageReturn.AT_LEAST_ONE_FIELD);

        userTxService.updateUser(userUpdateDTO);

        return Result.success(MessageReturn.OPERATION_SUCCESS);
    }


}
