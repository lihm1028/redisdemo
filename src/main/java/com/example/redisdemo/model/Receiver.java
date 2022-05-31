package com.example.redisdemo.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class Receiver {

    private static final Logger logger = LoggerFactory.getLogger(Receiver.class);
    private AtomicInteger counter = new AtomicInteger();


    public void receiveMessage(String message) {

        logger.info("接收消息:<" + message + ">");

        /**
         * 数量加1
         */
        counter.incrementAndGet();
    }

    public int getCount() {
        return counter.get();
    }


}
