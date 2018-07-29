package com.tfboss.login.util;

import net.sourceforge.pinyin4j.PinyinHelper;

public class CnToEnglish
{
	public static String getSpell(String one)
	{
		StringBuffer buffer=new StringBuffer();
		char[] ch=one.toCharArray();
		String [] check;
		for(char ch2:ch)
		{
			if(ch2<128)
			{
				buffer.append(ch2);
			}
			else {
				check = PinyinHelper.toHanyuPinyinStringArray(ch2);
				buffer.append(check[0]);
			}
		}
		return buffer.toString().trim();
	}
}
