package com.multiservercontrol.minecontrol;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is the controller of the minecraft servers. With it you can start|stop|restart the server.
 */
public class ServerController {

    private static Logger LOGGER = Logger.getLogger(ServerController.class);
    private final static String CONFIG_FILE_NAME = "minecontrol.properties";
    private final static String CONFIG_LOGGER_LEVEL = "logger.level";
    private final static String CONFIG_SHELL_BIN = "shell.bin";
    private final static String CONFIG_STOP_DELAY = "server.stop.delay";
    private final static String CONFIG_PID_COMMAND = "command.pid";
    private final static String CONFIG_START_COMMAND = "command.start";

    private ProcessBuilder processBuilder;
    private Configuration config;
    private String pathToShellBinary;

    public ServerController()
    {
        try {
            this.config = new PropertiesConfiguration(CONFIG_FILE_NAME);
            String logLevel = this.config.getString(CONFIG_LOGGER_LEVEL);
            LOGGER.setLevel(Level.toLevel(logLevel));
            LOGGER.debug("Log level: " + logLevel);
        } catch (ConfigurationException e) {
            System.out
                    .println("Fatal error: Can't run MineControl library, see log files for more information.");
            LOGGER.error("Class "
                    + this.getClass().getSimpleName()
                    + " can't be instantiated (error with PropertiesConfiguration): "
                    + e.getMessage());
        }
        this.processBuilder = new ProcessBuilder("");
        this.pathToShellBinary = this.config.getString(CONFIG_SHELL_BIN);
        LOGGER.debug("Path to shell binary: " + pathToShellBinary);
    }

    /**
     * Starts the minecraft server with the given server name.
     *
     * @param screenName name of the minecraft server
     */
    public void start(String screenName)
    {
        if (!this.isRunning(screenName)) {
            System.out.println("Starting server " + screenName + "...");
            LOGGER.info("Starting server " + screenName + "...");

            this.config.setProperty("screen.name", screenName);
            String startCommand = this.config.getString(CONFIG_START_COMMAND);
            LOGGER.debug("Start command: " + startCommand);

            this.processBuilder.command(this.pathToShellBinary, "-c",
                    startCommand);
            this.processBuilder.redirectErrorStream(true);

            try {
                Process startProcess = processBuilder.start();
                System.out.println("Server " + screenName
                        + " started successfully!");
                LOGGER.info("Server " + screenName + " started successfully!");
            } catch (IOException e) {
                System.out.println("Server " + screenName
                        + " startup failed! See logs for more information.");
                LOGGER.error("Server " + screenName + " startup failed: "
                        + e.getMessage());
            }
        }
    }

    /**
     * Stops the minecraft server with the given server name.
     *
     * @param screenName name of the minecraft server
     */
    public void stop(String screenName)
    {
        if (this.isRunning(screenName)) {
            ServerMessenger messenger = new ServerMessenger();
            long shutdownDelay = this.config.getLong(CONFIG_STOP_DELAY);
            LOGGER.debug("stop(): Shutdown delay for server " + screenName
                    + ": " + shutdownDelay);

            System.out.println("Stopping server " + screenName + "...");
            LOGGER.info("Stopping server " + screenName + "...");
            messenger.sendServerCommand(screenName, "stop");

            try {
                Thread.sleep(shutdownDelay);
            } catch (InterruptedException e) {
                LOGGER.error("Thread can't sleep!" + e.getMessage());
            }
            if (!this.isRunning(screenName)) {
                System.out.println("Server " + screenName
                        + " stopped successfully!");
                LOGGER.info("Server " + screenName + " stopped successfully!");
            }
        }
    }

    /**
     * Restarts the minecraft server with the given server name.
     *
     * @param screenName name of the minecraft server
     */
    public void restart(String screenName)
    {
        this.stop(screenName);
        this.start(screenName);
    }

    /**
     * Checks if the minecraft server with the given server name is running.
     *
     * @param screenName name of the minecraft server
     * @return true if the server is running, false if not
     */
    public boolean isRunning(String screenName)
    {
        if (this.getPid(screenName) != 0) {
            System.out.println("Server " + screenName + " is running!");
            LOGGER.info("Server " + screenName + " is running!");
            return true;
        } else {
            System.out.println("Server " + screenName + " isn't running!");
            LOGGER.info("Server " + screenName + " isn't running!");
            return false;
        }
    }

    /**
     * Retrieves the process number (pid) of the running server with the given server name.
     *
     * @param screenName name of the minecraft server
     * @return pid - the process number of the minecraft server
     */
    protected int getPid(String screenName)
    {
        int pid = 0;
        this.config.setProperty("screen.name", screenName);
        LOGGER.debug("getPid(): Set property 'screen.name' to " + screenName);
        String pidCommand = this.config.getString(CONFIG_PID_COMMAND);
        LOGGER.debug("pid command: " + pidCommand);
        this.processBuilder.command(this.pathToShellBinary, "-c", pidCommand);

        try {
            Process pidProcess = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    pidProcess.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                pid = Integer.parseInt(line);
                line = "";
            }
            LOGGER.info("Server " + screenName + " is running under pid " + pid);
        } catch (Exception e) {
            LOGGER.error("Pid lookup failed: " + e.getMessage());
        }
        return pid;
    }
}
