package com.nxecoii.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import android.util.Log;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;


public class QueueConsumer extends RabbitMqBase implements Runnable, Consumer {
	
//	private Handler mHandler = null;Handler mHandler,

	public QueueConsumer(String queueName) throws IOException, TimeoutException {
		super(queueName);
//		this.mHandler = mHandler;
	}

	public void run() {
		try {
			// start consuming messages. Auto acknowledge messages.
			channel.basicConsume(queueName, true, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when consumer is registered.
	 */
	public void handleConsumeOk(String consumerTag) {
		System.out.println("Consumer " + consumerTag + " registered");
	}

	/**
	 * Called when new message is available.
	 */
	public void handleDelivery(String consumerTag, Envelope env,
			BasicProperties props, byte[] body) throws IOException {
		String routingKey = env.getRoutingKey();
		String contentType = props.getContentType();
		long deliveryTag = env.getDeliveryTag();
		
		Log.i("MQ", "routingKey: " + routingKey);
		Log.i("MQ", "contentType: " + contentType);
		Log.i("MQ", "deliveryTag: " + deliveryTag);
		
		String message = new String(body,"UTF-8");
		
		Log.i("MQ", "message: " + message);
		
		CommandAnalysis ca  = new CommandAnalysis();
		ca.commandAnalysis(message);
	}

	public void handleCancel(String consumerTag) {
	}

	public void handleCancelOk(String consumerTag) {
	}

	public void handleRecoverOk(String consumerTag) {
	}

	public void handleShutdownSignal(String consumerTag,
			ShutdownSignalException arg1) {
	}

	@Override
	public void handleRecoverOk() {
		// TODO Auto-generated method stub

	}
	
}