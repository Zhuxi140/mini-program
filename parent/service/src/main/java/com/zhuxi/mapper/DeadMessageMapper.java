package com.zhuxi.mapper;

import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageAddDTO;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageSync;
import com.zhuxi.pojo.DTO.DeadMessage.DeadMessageUpdate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DeadMessageMapper {

    int insert(DeadMessageAddDTO deadMessageAddDTO);

    List<DeadMessageAddDTO> getListDeadMessages(Long lastId, Integer pageSize);

    @Select("""
            SELECT
                message_body,
                failure_reason
            FROM dead_letter_message
            WHERE message_id = #{messageId}
    """)
    DeadMessageSync getDeadMessage(String messageId);

    @Select("SELECT COUNT(*) FROM dead_letter_message WHERE message_id = #{messageId}")
    int isExist(String messageId);

    @Select("SELECT version FROM dead_letter_message WHERE message_id = #{messageId}")
    Long getVersion(String messageId);

    @Update("""
    UPDATE dead_letter_message
    SET
        failure_reason = #{dead.failureReason},
        message_body = #{dead.messageBody},
        version = version + 1
    WHERE message_id = #{dead.messageId} AND version = #{version}
    """)
    int update(@Param("dead") DeadMessageUpdate deadMessageUpdate, Long  version);

    @Update("""
    UPDATE dead_letter_message
    SET handler = #{handler}, action_taken = #{actionTaken}
    WHERE message_id = #{messageId}
    """)
    int UpdateHinder(String handler, String messageId, String actionTaken);
}
