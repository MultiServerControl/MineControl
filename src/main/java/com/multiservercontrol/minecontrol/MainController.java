package com.multiservercontrol.minecontrol;

public class MainController {

    /**
     * @param args
     */
    public static void main(String[] args) {
	String command = args[0];
	if (command.equals("start")) {
	    start();
	} else if (command.equals("stop")) {
	    stop();
	} else if (command.equals("restart")) {
	    restart();
	} else if (command.equals("status")) {
	    isRunning();
	}
    }

    public static void start() {
	System.out.println("start");
    }

    public static void stop() {
	System.out.println("stop");
    }

    public static void restart() {
	System.out.println("restart");
    }

    public static void isRunning() {
	System.out.println("status");
    }
}
