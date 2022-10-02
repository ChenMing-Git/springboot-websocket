package cn.com.pfu.ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.com.pfu.ws.service.Websocket;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/test")
@Slf4j
public class WebsocketTest {
    @Autowired
    private Websocket websocket;

    @RequestMapping(value = "/a", method = RequestMethod.GET)
    public String test(String a) {
        if (a != null) {
            websocket.sendMessage("heheh");
        }
        return a;
    }
}
