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
    private final static String CONFIG_SERVER_MODS = "server.mods";
    private final static String CONFIG_SERVER_URL = "server.url";
    private final static String CONFIG_SERVER_JAR = "server.jar";
    private final static String CONFIG_SERVER_PREFIX = "server";

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
     * @param serverName name or path of the server
     * @return true if the server exists, false if not
     */
    protected boolean serverExists(String serverName)
    {
        File serverFile = new File(serverName);

        if (serverFile.exists()) {
            return true;
        }
        System.out.println("The given server doesn't exist!");
        System.out.println("Should MineControl create it for you? (type yes or no)");

        if (this.input.next().toLowerCase().equals("yes")) {
            this.createServer(serverName);
        } else {
            System.out.println("Sorry, but without that existing server you can't play!");
        }
        return false;
    }

    /**
     * Downloads the server file from the given URL and saves it under the given path
     * e.g. serverName/minecraft_server.jar
     *
     * @param serverName name of the server (is used as directory name)
     */
    protected void getServerFile(String serverName, String urlToServerMod, String nameOfServerFile)
    {
        try {
            URL urlToServerFile = new URL(urlToServerMod);
            File destination = new File(serverName + "/" + nameOfServerFile);

            System.out.println("Server file will be downloaded now...");
            FileUtils.copyURLToFile(urlToServerFile, destination);
            System.out.println("Server file download completed!");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void createServer(String serverName)
    {
        if (this.serverExists(serverName)) {
            System.out.println("Server " + serverName + "already exists!");
            return;
        }

        String[] serverMods = this.config.getStringArray(CONFIG_SERVER_MODS);
        String inputServerMod = null;
        String modUrl = null;
        String modJar = null;

        System.out.println("Following server mods are available:");

        for (String mod : serverMods) {
            System.out.println(mod);
        }

        System.out.println("Which one do you want to use?");
        inputServerMod = this.input.next();

        for (int i = 0; i < serverMods.length; i++) {
            String acutalMod = serverMods[i];
            if (inputServerMod.toLowerCase().equals(acutalMod)) {
                this.config.setProperty(CONFIG_SERVER_PREFIX + "." + serverName, acutalMod);
                modUrl = this.config.getString(CONFIG_SERVER_URL + "." + acutalMod);
                modJar = this.config.getString(CONFIG_SERVER_JAR + "." + acutalMod);
            }
        }
        this.getServerFile(serverName, modUrl, modJar);
    }
}
