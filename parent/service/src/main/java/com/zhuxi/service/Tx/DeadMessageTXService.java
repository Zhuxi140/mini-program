package com.zhuxi.service.Tx;

import com.zhuxi.Constant.MessageReturn;
import com.zhuxi.Exception.transactionalException;
import com.zhuxi.mapper.DeadMessageMapper;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageSync;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageUpdate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeadMessageTXService {

    private final DeadMessageMapper deadMessageMapper;

    public DeadMessageTXService(DeadMessageMapper deadMessageMapper) {
        this.deadMessageMapper = deadMessageMapper;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Long getVersion(String MessageId){
        return deadMessageMapper.getVersion(MessageId);
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public DeadMessageSync getDeadMessage(String MessageId) {
        DeadMessageSync deadMessageSync = deadMessageMapper.getDeadMessage(MessageId);
        if (deadMessageSync == null){
            throw new transactionalException(MessageReturn.NO_RECORDS);
        }
        return deadMessageSync;
    }

    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public boolean isExist(String MessageId) {
        int exist = deadMessageMapper.isExist(MessageId);
        if (exist == 0){
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void update(DeadMessageUpdate deadMessageUpdate, Long version) {
        int update = deadMessageMapper.update(deadMessageUpdate, version);
        if (update == 0){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }
    }


    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<DeadMessageAddDTO> getListDeadMessages(Long lastId, Integer pageSize) {
        List<DeadMessageAddDTO> listDeadMessages = deadMessageMapper.getListDeadMessages(lastId, pageSize);
        if (listDeadMessages ==  null){
            throw new transactionalException(MessageReturn.UNKNOWN_ERROR);
        }else if(listDeadMessages.isEmpty()){
            throw new transactionalException(MessageReturn.NO_RECORDS);
        }
        return listDeadMessages;
    }



    @Transactional(rollbackFor = transactionalException.class)
    public void insert(DeadMessageAddDTO deadMessageAddDTO) {
        int insert = deadMessageMapper.insert(deadMessageAddDTO);
        if (insert == 0){
            throw new transactionalException(MessageReturn.INSERT_ERROR);
        }
    }

    @Transactional(rollbackFor = transactionalException.class)
    public void UpdateHinder(String userName, String messageId,String actionTaken){
        int update = deadMessageMapper.UpdateHinder(userName, messageId, actionTaken);
        if (update == 0){
            throw new transactionalException(MessageReturn.UPDATE_ERROR);
        }
    }


}
