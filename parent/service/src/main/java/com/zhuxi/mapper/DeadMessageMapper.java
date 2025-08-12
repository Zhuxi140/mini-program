package com.zhuxi.mapper;

import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeadMessageMapper {

    int insert(DeadMessageAddDTO deadMessageAddDTO);
}
