package com.eric.util;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * description:单元测试基础类
 * author:Eric
 * Date:16/8/24
 * Time:15:46
 * version 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class BaseTest {
    public BaseTest(){

    }
}
