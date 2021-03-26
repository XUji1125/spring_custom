package com.yunqia.demo.controller;

import com.yunqia.demo.service.DemoService;
import com.yunqia.mvcframework.annotation.Autowired;
import com.yunqia.mvcframework.annotation.Controller;
import com.yunqia.mvcframework.annotation.RequestMapping;
import com.yunqia.mvcframework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @author 徐霁
 *
 * 微信/QQ：1198435103
 */
@Controller
public class DemoController {

    @Autowired
    private DemoService demoService;

    @RequestMapping("req1")
    public void request1(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("接收到请求了。");
        try {
            response.getWriter().write(demoService.request1("success"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("req2")
    public void request2(HttpServletRequest request, HttpServletResponse response, @RequestParam("name")  String name) {
        System.out.println("接收到请求了。");
        try {
            response.getWriter().write(demoService.request1(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
