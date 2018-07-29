package com.tfboss.login.client;

import java.io.Serializable;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class User implements Serializable 
{
	private static final long serialVersionUID = 1L;

	private String Command;
	private int color;
	
	private String name;
	private String fingerPassword;
	private String passwordType;

	public String getCommand() {
		return Command;
	}

	public void setCommand(String command) {
		Command = command;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getPasswordType()
	{
		return passwordType;
	}

	public void setPasswordType(String passwordType)
	{
		this.passwordType=passwordType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFingerPassword() {
		return fingerPassword;
	}

	public void setFingerPassword(String fingerPassword) {
		this.fingerPassword = fingerPassword;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof User) 
		{
			User user = (User) o;
			if (user.getName().equals(name))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() 
	{
		return "User [name=" + name + "]";
	}

}
