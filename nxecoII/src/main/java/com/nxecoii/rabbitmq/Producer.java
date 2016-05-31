package com.nxecoii.rabbitmq;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.TimeoutException;
 
public class Producer extends RabbitMqBase{
     
    public Producer(String endPointName) throws IOException, TimeoutException{
        super(endPointName);
    }
 
    public void sendMessage(String message) throws IOException {
        channel.basicPublish("",queueName, null, message.getBytes());
    }  
}