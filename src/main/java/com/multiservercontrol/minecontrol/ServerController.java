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
     * @param serverName name of the minecraft server
     */
    public void start(String serverName)
    {
        if (!this.isRunning(serverName)) {
            System.out.println("Starting server " + serverName + "...");
            LOGGER.info("Starting server " + serverName + "...");

            this.config.setProperty("server.name", serverName);
            String startCommand = this.config.getString(CONFIG_START_COMMAND);
            LOGGER.debug("Start command: " + startCommand);

            this.processBuilder.command(this.pathToShellBinary, "-c",
                    startCommand);
            this.processBuilder.redirectErrorStream(true);

            try {
                Process startProcess = processBuilder.start();
                System.out.println("Server " + serverName
                        + " started successfully!");
                LOGGER.info("Server " + serverName + " started successfully!");
            } catch (IOException e) {
                System.out.println("Server " + serverName
                        + " startup failed! See logs for more information.");
                LOGGER.error("Server " + serverName + " startup failed: "
                        + e.getMessage());
            }
        }
    }

    /**
     * Stops the minecraft server with the given server name.
     *
     * @param serverName name of the minecraft server
     */
    public void stop(String serverName)
    {
        if (this.isRunning(serverName)) {
            ServerMessenger messenger = new ServerMessenger();
            long shutdownDelay = this.config.getLong(CONFIG_STOP_DELAY);
            LOGGER.debug("stop(): Shutdown delay for server " + serverName
                    + ": " + shutdownDelay);

            System.out.println("Stopping server " + serverName + "...");
            LOGGER.info("Stopping server " + serverName + "...");
            messenger.sendServerCommand(serverName, "stop");

            try {
                Thread.sleep(shutdownDelay);
            } catch (InterruptedException e) {
                LOGGER.error("Thread can't sleep!" + e.getMessage());
            }
            if (!this.isRunning(serverName)) {
                System.out.println("Server " + serverName
                        + " stopped successfully!");
                LOGGER.info("Server " + serverName + " stopped successfully!");
            }
        }
    }

    /**
     * Restarts the minecraft server with the given server name.
     *
     * @param serverName name of the minecraft server
     */
    public void restart(String serverName)
    {
        this.stop(serverName);
        this.start(serverName);
    }

    /**
     * Checks if the minecraft server with the given server name is running.
     *
     * @param serverName name of the minecraft server
     * @return true if the server is running, false if not
     */
    public boolean isRunning(String serverName)
    {
        if (this.getPid(serverName) != 0) {
            System.out.println("Server " + serverName + " is running!");
            LOGGER.info("Server " + serverName + " is running!");
            return true;
        } else {
            System.out.println("Server " + serverName + " isn't running!");
            LOGGER.info("Server " + serverName + " isn't running!");
            return false;
        }
    }

    /**
     * Retrieves the process number (pid) of the running server with the given server name.
     *
     * @param serverName name of the minecraft server
     * @return pid - the process number of the minecraft server
     */
    protected int getPid(String serverName)
    {
        int pid = 0;
        this.config.setProperty("server.name", serverName);
        LOGGER.debug("getPid(): Set property 'server.name' to " + serverName);
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
            LOGGER.info("Server " + serverName + " is running under pid " + pid);
        } catch (Exception e) {
            LOGGER.error("Pid lookup failed: " + e.getMessage());
        }
        return pid;
    }
}
