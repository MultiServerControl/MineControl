package com.multiservercontrol.minecontrol;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ServerInitialiser {

    private static Logger LOGGER = Logger.getLogger(ServerController.class);
    private final static String CONFIG_FILE_NAME = "minecontrol.properties";
    private final static String CONFIG_LOGGER_LEVEL = "logger.level";
    private final static String CONFIG_SERVER_URL = "server.url";
    private final static String CONFIG_SERVER_JAR = "server.jar";

    private Configuration config;
    private Scanner input;

    public ServerInitialiser()
    {
        try {
            this.config = new PropertiesConfiguration(CONFIG_FILE_NAME);
            this.input = new Scanner(System.in);
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
    }

    /**
     * Checks if the server (file/folder) exists.
     *
     * @param fileName name or path of the server
     * @return true if the server exists, false if not
     */
    public boolean serverExists(String fileName)
    {
        File serverFile = new File(fileName);

        if (serverFile.exists()) {
            return true;
        }
        System.out.println("Can't find the server file!");
        System.out.println("Should MineControl download it for you? (type yes or no)");

        if (this.input.next().toLowerCase().equals("yes")) {
            this.getServerFile(fileName);
            System.out.println("Server file download completed!");
        } else {
            System.out.println("Without the server file you can't start your server!");
        }

        return false;
    }

    /**
     * Downloads the server file from the given URL and saves it under the given path
     * e.g. serverName/minecraft_server.jar
     *
     * @param serverName name of the server (is used as directory name)
     */
    public void getServerFile(String serverName)
    {
        try {
            URL urlToServerFile = new URL(this.config.getString(CONFIG_SERVER_URL));
            File destination = new File(serverName + "/" + this.config.getString(CONFIG_SERVER_JAR));

            System.out.println("Server file will be downloaded now...");
            FileUtils.copyURLToFile(urlToServerFile, destination);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
