package com.yunqia.demo.service.impl;

import com.yunqia.demo.service.DemoService;
import com.yunqia.mvcframework.annotation.Service;

/**
 * @author 徐霁
 *
 * 微信/QQ：1198435103
 */
@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String request1(String message) {
        return "自写框架测试返回信息：" + message;
    }
}
