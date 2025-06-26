package com.zhuxi.service.TxService;


import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.Result.Result;
import com.zhuxi.mapper.CartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CarVO;

import java.util.List;

@Service
public class CartTxService {

    private final CartMapper cartMapper;

    public CartTxService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }


    @Transactional(readOnly = true,propagation = Propagation.REQUIRES_NEW)
    public List<CarVO> getListCart(Long userId){
        List<CarVO> listCar = cartMapper.getListCar(userId);
        if(listCar == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return  listCar;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateQuantity(CarUpdateDTO carUpdateDTO, Long userId){
        int i = cartMapper.updateQuantity(carUpdateDTO,userId);
        if(i < 1)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(CarUpdateDTO carUpdateDTO, Long userId){
        if(!cartMapper.insert(carUpdateDTO, userId))
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void delete(Long userId, Long productId){
        if(productId != null){
            Boolean delete = cartMapper.delete(userId, productId);
            if ( !delete)
               throw new transactionalException(Message.DELETE_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void deleteAll(Long userId){
        if(!cartMapper.deleteAll(userId))
            throw new transactionalException(Message.DELETE_ERROR);
    }
}
