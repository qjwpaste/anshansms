package com.landray.asyh.asyhmsg.dao;

import com.landray.asyh.asyhmsg.beans.KKSMSSEND;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class MsgDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<KKSMSSEND> selectUnusedMsg(){
        //查询状态为未发起的kk信息
        List<KKSMSSEND> kkSmsSendList = jdbcTemplate.query("SELECT * FROM KK_SMS_SEND where state=?",
                new BeanPropertyRowMapper<>(KKSMSSEND.class),0);
        return kkSmsSendList;
    }

    public int updateSmsState(KKSMSSEND msg){
        int update = jdbcTemplate.update("UPDATE KK_SMS_SEND SET state=? WHERE id=?",
                2, msg.getId());
        return update;
    }


}
