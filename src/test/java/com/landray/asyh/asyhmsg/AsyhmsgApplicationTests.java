package com.landray.asyh.asyhmsg;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AsyhmsgApplicationTests {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void updateTest(){
        int update = jdbcTemplate.update("UPDATE KK_SMS_SEND SET state=? WHERE id=?",
                2, 4);
    }

}
