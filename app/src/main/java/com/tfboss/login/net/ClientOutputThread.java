package com.tfboss.login.net;

import java.io.ObjectOutputStream;
import java.net.Socket;
import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */


public class ClientOutputThread extends Thread
{
	private Socket socket;
	private ObjectOutputStream oos;
	private boolean isStart = true;
	private TranObject msg;
	private Client client;

	public ClientOutputThread(Socket socket,Client client)
	{
		this.socket = socket;
		this.client=client;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public void setMsg(TranObject msg)
	{
		this.msg = msg;
		synchronized (this) {
			notify();
		}
	}

	@Override
	public void run()
	{
		try
		{
			if(oos==null&&socket!=null)
			{
				oos = new ObjectOutputStream(socket.getOutputStream());
			}
			while (isStart)
			{
				if (msg != null)
				{
					oos.writeObject(msg);
					oos.flush();
					synchronized (this)
					{
						wait();
					}
				}
			}
			oos.close();
			if (socket != null && !socket.isClosed())
			{
				socket.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			client.setIsConnect(false);
		}
	}
}
