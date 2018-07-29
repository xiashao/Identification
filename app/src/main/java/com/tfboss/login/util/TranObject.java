package com.tfboss.login.util;

import java.io.Serializable;


/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */


public class TranObject<T> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private TranObjectType type;//发送的消息用于什么操作,TranObjectType是enum类型的数据
	private T object;// 传输的对象

	//构造方法
	public TranObject(TranObjectType type) {
		this.type = type;
	}

	//一般的set和get方法

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public TranObjectType getType() {
		return type;
	}

	//toString方法
	@Override
	public String toString() {
		return "TranObject [type=" + type + ", object=" + object + "]";
	}
}
