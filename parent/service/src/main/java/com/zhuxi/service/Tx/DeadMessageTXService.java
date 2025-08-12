package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.DeadMessageMapper;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeadMessageTXService {

    private final DeadMessageMapper deadMessageMapper;

    public DeadMessageTXService(DeadMessageMapper deadMessageMapper) {
        this.deadMessageMapper = deadMessageMapper;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void insert(DeadMessageAddDTO deadMessageAddDTO) {
        int insert = deadMessageMapper.insert(deadMessageAddDTO);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }
}
