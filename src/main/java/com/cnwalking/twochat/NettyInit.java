package com.cnwalking.twochat;

import com.cnwalking.twochat.websocket.WebSocketServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NettyInit implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 没启动就启动起来
        if (event.getApplicationContext().getParent() == null) {
            try {
                WebSocketServer.getInstance().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
