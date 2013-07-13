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

    private String serverName;
    private ProcessBuilder processBuilder;
    private Configuration config;
    private String pathToShellBinary;

    public ServerController(String serverName)
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
        this.serverName = serverName;
        this.processBuilder = new ProcessBuilder("");
        this.pathToShellBinary = this.config.getString(CONFIG_SHELL_BIN);
        this.config.setProperty("server.name", this.serverName);
        this.config.setProperty("server.jar", this.config.getString("server." + serverName));
        LOGGER.debug("Path to shell binary: " + pathToShellBinary);
        LOGGER.debug("getPid(): Set property 'server.name' to " + this.serverName);
    }

    /**
     * Starts the minecraft server with the given server name.
     */
    public void start()
    {
        if (!this.isRunning()) {
            System.out.println("Starting server " + this.serverName + "...");
            LOGGER.info("Starting server " + this.serverName + "...");

            this.config.setProperty("server.name", this.serverName);
            String startCommand = this.config.getString(CONFIG_START_COMMAND);
            LOGGER.debug("Start command: " + startCommand);

            this.processBuilder.command(this.pathToShellBinary, "-c",
                    startCommand);
            this.processBuilder.redirectErrorStream(true);

            try {
                Process startProcess = processBuilder.start();
                System.out.println("Server " + this.serverName
                        + " started successfully!");
                LOGGER.info("Server " + this.serverName + " started successfully!");
            } catch (IOException e) {
                System.out.println("Server " + this.serverName
                        + " startup failed! See logs for more information.");
                LOGGER.error("Server " + this.serverName + " startup failed: "
                        + e.getMessage());
            }
        }
    }

    /**
     * Stops the minecraft server with the given server name.
     */
    public void stop()
    {
        if (this.isRunning()) {
            ServerMessenger messenger = new ServerMessenger();
            long shutdownDelay = this.config.getLong(CONFIG_STOP_DELAY);
            LOGGER.debug("stop(): Shutdown delay for server " + this.serverName
                    + ": " + shutdownDelay);

            System.out.println("Stopping server " + this.serverName + "...");
            LOGGER.info("Stopping server " + this.serverName + "...");
            messenger.sendServerCommand(this.serverName, "stop");

            try {
                Thread.sleep(shutdownDelay);
            } catch (InterruptedException e) {
                LOGGER.error("Thread can't sleep!" + e.getMessage());
            }
            if (!this.isRunning()) {
                System.out.println("Server " + this.serverName
                        + " stopped successfully!");
                LOGGER.info("Server " + this.serverName + " stopped successfully!");
            }
        }
    }

    /**
     * Restarts the minecraft server with the given server name.
     */
    public void restart()
    {
        this.stop();
        this.start();
    }

    /**
     * Checks if the minecraft server with the given server name is running.
     *
     * @return true if the server is running, false if not
     */
    public boolean isRunning()
    {
        if (this.getPid() != 0) {
            System.out.println("Server " + this.serverName + " is running!");
            LOGGER.info("Server " + this.serverName + " is running!");
            return true;
        } else {
            System.out.println("Server " + this.serverName + " isn't running!");
            LOGGER.info("Server " + this.serverName + " isn't running!");
            return false;
        }
    }

    /**
     * Retrieves the process number (pid) of the running server with the given server name.
     *
     * @return pid - the process number of the minecraft server
     */
    protected int getPid()
    {
        int pid = 0;
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
            LOGGER.info("Server " + this.serverName + " is running under pid " + pid);
        } catch (Exception e) {
            LOGGER.error("Pid lookup failed: " + e.getMessage());
        }
        return pid;
    }
}
