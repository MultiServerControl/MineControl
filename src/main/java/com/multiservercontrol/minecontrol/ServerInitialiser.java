package com.multiservercontrol.minecontrol;

import org.apache.log4j.Logger;

import java.io.File;

public class ServerInitialiser {

    private static Logger LOGGER = Logger.getLogger(ServerController.class);
    private final static String CONFIG_FILE_NAME = "minecontrol.properties";
    private final static String CONFIG_LOGGER_LEVEL = "logger.level";
    private final static String CONFIG_SHELL_BIN = "shell.bin";

    /**
     * Checks if the server (file/folder) exists.
     *
     * @param fileName name or path of the server
     * @return true if the server exists, false if not
     */
    public boolean serverFileExists(String fileName)
    {
        File serverFile = new File(fileName);

        if (serverFile.exists()) {
            return true;
        }
        System.out.println("Der Server existiert nicht!");
        return false;
    }
}
