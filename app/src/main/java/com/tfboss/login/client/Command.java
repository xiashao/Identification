package com.tfboss.login.client;

import java.io.Serializable;

/**
 *  Created By 陈佳杰 --献给最喜欢的她.
 */

public class Command implements Serializable
{
    private String Command;
    private int color1,color2,color3,color4,color5;

    public Command()
    {
        Command="";
        color1=color2=color3=color4=color5=0;
    }

    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }

    public int getColor1() {
        return color1;
    }

    public void setColor1(int color1) {
        this.color1 = color1;
    }

    public int getColor2() {
        return color2;
    }

    public void setColor2(int color2) {
        this.color2 = color2;
    }

    public int getColor3() {
        return color3;
    }

    public void setColor3(int color3) {
        this.color3 = color3;
    }

    public int getColor4() {
        return color4;
    }

    public void setColor4(int color4) {
        this.color4 = color4;
    }

    public int getColor5() {
        return color5;
    }

    public void setColor5(int color5) {
        this.color5 = color5;
    }
}
