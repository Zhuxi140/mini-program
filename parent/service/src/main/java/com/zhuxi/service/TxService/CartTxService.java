package com.zhuxi.service.TxService;


import com.zhuxi.Constant.Message;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.CartMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.main.java.com.zhuxi.pojo.DTO.Car.CarAddDTO;
import src.main.java.com.zhuxi.pojo.DTO.Car.CartUpdateDTO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartNewVO;
import src.main.java.com.zhuxi.pojo.VO.Car.CartVO;

import java.util.List;

@Service
public class CartTxService {

    private final CartMapper cartMapper;

    public CartTxService(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }


    @Transactional(readOnly = true)
    public List<CartVO> getListCart(Long userId){
        List<CartVO> listCar = cartMapper.getListCar(userId);
        if(listCar == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return  listCar;
    }

    @Transactional(readOnly = true)
    public Integer getStock(Long productId, Long specId){
        Integer stock = cartMapper.getStock(productId, specId);
        if(stock == null)
            throw new transactionalException(Message.SELECT_ERROR);

        return stock;
    }

    @Transactional(readOnly = true)
    public CartNewVO getNewCar(Long productId, Long specId){
        CartNewVO newCar = cartMapper.getNewCar(productId, specId);
        if(newCar == null)
            throw new transactionalException(Message.SELECT_ERROR);
        return newCar;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void updateQuantityOrSpec(CartUpdateDTO cartUpdateDTO, Long userId){
        int i = cartMapper.updateQuantityOrSpec(cartUpdateDTO,userId);
        if(i < 1)
            throw new transactionalException(Message.UPDATE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(CarAddDTO carAddDTO, Long userId){

        if(!cartMapper.insert(carAddDTO, userId))
            throw new transactionalException(Message.INSERT_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void delete(Long cartId){
            Boolean delete = cartMapper.delete(cartId);
            if ( !delete)
               throw new transactionalException(Message.DELETE_ERROR);
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void deleteAll(Long userId){
        if(!cartMapper.deleteAll(userId))
            throw new transactionalException(Message.DELETE_ERROR);
    }
}
