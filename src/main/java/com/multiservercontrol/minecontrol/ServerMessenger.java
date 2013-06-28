package com.multiservercontrol.minecontrol;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * This class is used for all art of communication with the server, like chat or command tunneling.
 */
public class ServerMessenger {

    private static Logger LOGGER = Logger.getLogger(ServerMessenger.class);
    private final static String CONFIG_FILE_NAME = "minecontrol.properties";
    private final static String CONFIG_LOGGER_LEVEL = "logger.level";
    private final static String CONFIG_SHELL_BIN = "shell.bin";
    private final static String CONFIG_MESSENGER_COMMAND = "command.messenger";

    private ProcessBuilder processBuilder;
    private Configuration config;
    private String pathToShellBinary;

    public ServerMessenger()
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
     * Passes a server command through the minecraft server.
     *
     * @param screenName name of the minecraft server
     * @param command command that should be passed through the server
     */
    public void sendServerCommand(String screenName, String command)
    {
        this.config.setProperty("screen.name", screenName);
        LOGGER.debug("sendServerCommand(): Set property 'screen.name' to "
                + screenName);
        this.config.setProperty("messenger.argument", command);
        LOGGER.debug("sendServerCommand(): Set property 'messenger.argument' to "
                + command);
        String messengerCommand = this.config
                .getString(CONFIG_MESSENGER_COMMAND);
        LOGGER.debug("messenger command: " + messengerCommand);

        System.out.println("Sending command '" + command + "' to server "
                + screenName);
        this.processBuilder.command(this.pathToShellBinary, "-c",
                messengerCommand);
        try {
            Process messengerProcess = this.processBuilder.start();
        } catch (IOException e) {
            LOGGER.error("Sending server command failed: " + e.getMessage());
        }
    }
}
