package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 对任何路径都进行匹配的一个Controller，工作在Filter之后
 */
@Controller
public class AllController {
    final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    // 从配置文件里读取自定义内容
    @Value("${id}")
    String id;
    @Value("${name}")
    String name;
    @Value("${set}")
    String set;
    @Value("${pass_tip}")
    String passTip;
    @Value("${type}")
    String type;

    // 对任何路径都进行匹配
    @RequestMapping("/**")
    public String test(Map<String, Object> map) {
        log.info("[ID] "+id);
        log.info("[NAME] "+name);
        log.info("[Set] "+set);
        // 设定传给页面的值
        map.put("studentId", id);
        map.put("studentName", name);
        map.put("studentSet", set);
        map.put("passTip", passTip);
        map.put("type", type);
        // 返回fake.html页面
        return "fake";
    }


}
