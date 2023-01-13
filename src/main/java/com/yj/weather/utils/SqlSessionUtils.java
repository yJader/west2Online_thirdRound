package com.yj.weather.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author yJade
 * @Date 2023-01-12 12:10
 * @Package com.yj.mybatis.service
 */
public class SqlSessionUtils {
    public static SqlSession getSqlSession() {
        SqlSession sqlSession = null;
        try{
            InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
            SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
            sqlSession = factory.openSession(false); //自动提交事务
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sqlSession;
    }
}
