package com.multiservercontrol.minecontrol;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class MainController {

    private ProcessBuilder builder = new ProcessBuilder("");
    private Configuration config = null;

    public MainController() {
	try {
	    this.config = new PropertiesConfiguration("minecontrol.properties");
	} catch (ConfigurationException e) {
	    // TODO log
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	MainController controller = new MainController();
	String server = args[0];
	String command = args[1];

	if (command.equals("start")) {
	    controller.start(server);
	} else if (command.equals("stop")) {
	    controller.stop();
	} else if (command.equals("restart")) {
	    controller.restart();
	} else if (command.equals("status")) {
	    controller.isRunning();
	}
    }

    public void start(String serverId) {
	System.out.println("Starting server...");

	String startCommand = config.getString("command.start");
	this.builder.command("screen", "-dmS", serverId, "java", "-jar",
		"minecraft_server.jar", "nogui");
	builder.redirectErrorStream(true);

	try {
	    Process p = builder.start();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void stop() {
	System.out.println("stop");
    }

    public void restart() {
	System.out.println("restart");
    }

    public void isRunning() {
	System.out.println("status");
    }
}
