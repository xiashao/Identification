package com.tfboss.login.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import com.tfboss.login.util.*;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */


public class ClientInputThread extends Thread
{
	private Socket socket;
	private TranObject msg;
	private boolean isStart = true;
	private ObjectInputStream ois;
	private MessageListener messageListener;

	public ClientInputThread(Socket socket)
	{
		this.socket = socket;
	}

	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;
	}

	public void setStart(boolean isStart)
	{
		this.isStart = isStart;
	}

	@Override
	public void run()
	{
		try
		{
			if (ois == null&&socket!=null)
			{
				ois = new ObjectInputStream(socket.getInputStream());
			}
			while (isStart)
			{
				msg = (TranObject) ois.readObject();

				messageListener.Message(msg);
			}
			ois.close();
			if (socket != null&&!socket.isClosed())
				socket.close();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
