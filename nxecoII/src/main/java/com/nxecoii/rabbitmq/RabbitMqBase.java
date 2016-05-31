package com.nxecoii.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class RabbitMqBase {

	protected Channel channel;
	protected Connection connection;
	protected String queueName;
	
//	static final String host = "www.rainmq.com";
//	static final String host = "45.79.82.86";
	static final String host = "173.255.243.219";
	static final String username = "admin";
	static final String password = "admin";
	
//	static final String host = "192.168.1.182";
//	static final String username = "rollen";
//	static final String password = "root";
	
	static final String exchangeName = "amq.topic";
	static public String routingKey = "";
	
	public RabbitMqBase(String queueName) throws IOException, TimeoutException {
		this.queueName = queueName;

		ConnectionFactory factory = new ConnectionFactory();
		factory.setRequestedHeartbeat(60);
		
		factory.setHost(host);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setAutomaticRecoveryEnabled(true);
		
		connection = factory.newConnection();

		channel = connection.createChannel();

		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, "ACCF235AAE4E_APP");
	}

	/**
	 *close channel and connection
	 *Is not necessary, because the implicit is automatically invoked.
	 * @throws IOException
	 * @throws TimeoutException 
	 */
	public void close() throws IOException, TimeoutException {
		this.channel.close();
		this.connection.close();
	}

	public void handleRecoverOk() {
		// TODO Auto-generated method stub
	}
}