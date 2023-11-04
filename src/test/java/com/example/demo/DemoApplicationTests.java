package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


//@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() throws SQLException  {
        long before = System.currentTimeMillis();

        Connection conn = jdbcTemplate.getDataSource().getConnection();
        PreparedStatement pstmt = null;
        conn.setAutoCommit(false);

//        String sql = "insert into log (id, name,create_time) values (?,?,?)";
//        int perNumer = 100000;
//        for(int i = 0 ; i < 100; i++) {
//            List<Object[]> voList = new ArrayList<>();
//            for (int j = i * perNumer; j < (i + 1) * perNumer; j++) {
//                Object[] vo = new Object[]{j, j+"", makeRandomDate()};
//                voList.add(vo);
//            }
//            jdbcTemplate.batchUpdate(sql, voList);
//        }
        // select count(*) from log where create_time < '2000-01-01';

        // 53s--有索引     11s---无索引
        jdbcTemplate.update("delete from log where create_time < ?", "2000-01-01");

        conn.commit();
        if(null != pstmt) {
            pstmt.close();
        }
        conn.close();
        long after = System.currentTimeMillis();
        System.out.println(String.format("花费了{%d}s", (after-before)/1000));



    }

//    @Test
    public Date makeRandomDate() {
        // 47174400000 1648739217138
        System.out.println("========start=========");
        long max=1648739217138L, min=47174400000L;
        long ran2 = (long)(Math.random()*(max-min)+min);
        Date date = new Date(ran2);
        System.out.println(date);
        System.out.println("========end===========");
        return date;
    }

}
