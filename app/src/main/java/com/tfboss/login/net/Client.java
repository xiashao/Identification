package com.tfboss.login.net;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class Client
{
	private Socket socket;
	private ClientThread clientThread;
	private ClientConnect clientConnect;
	private String ip;
	private int port;
	private boolean isConnect;

	public Client(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String toString()
	{
		return ip+" "+port;
	}

	public boolean start()
	{
		try
		{
			socket = new Socket();
			connectService(socket,ip,port);
			if (socket.isConnected())
			{
				isConnect=true;
				clientThread = new ClientThread(socket);
				clientThread.start();
			}
			else
			{
				isConnect=false;
			}
		}
		catch(Exception e)
		{
			isConnect=false;
		}
		return isConnect;
	}

	public void connectService(Socket socket,String ip,int port)throws Exception
	{
		clientConnect = new ClientConnect(socket,ip,port);
		clientConnect.start();
		try {
			clientConnect.join();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public class ClientConnect extends Thread
	{
		private Socket socket;
		private String ip;
		private int port;
		public ClientConnect(Socket socket,String ip, int port)
		{
			this.socket=socket;
			this.ip=ip;
			this.port=port;
		}

		public void run()
		{
			try {
				socket.connect(new InetSocketAddress(ip, port), 5000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public Socket getSocket()
	{
		return socket;
	}

	public class ClientThread extends Thread
	{
		private ClientInputThread in;
		private ClientOutputThread out;

		public ClientThread(Socket socket)
		{
			in = new ClientInputThread(socket);
			out = new ClientOutputThread(socket,Client.this);
		}

		public void run()
		{
			in.setStart(true);
			out.setStart(true);
			in.start();
			out.start();
		}

		public ClientInputThread getIn() {
			return in;
		}

		public ClientOutputThread getOut() {
			return out;
		}
	}

	public ClientInputThread getClientInputThread() {
		return clientThread.getIn();
	}

	public ClientOutputThread getClientOutputThread() {
		return clientThread.getOut();
	}

	public void setIsStart(boolean isStart) {
		clientThread.getIn().setStart(isStart);
		clientThread.getOut().setStart(isStart);
	}

	public Boolean getIsConnect()
	{
		return isConnect;
	}

	public void setIsConnect(boolean isConnect)
	{
		this.isConnect=isConnect;
	}
}
