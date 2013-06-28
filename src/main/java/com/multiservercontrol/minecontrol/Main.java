package com.multiservercontrol.minecontrol;

/**
 * This is the entry point of the MineControl library. It receives user commands and handle them.
 */
public class Main {

    /**
     * Main method will be called on every library call.
     *
     * @param args arguments that are passed through with the library call like 'serverName', 'command', ('serverCommand')
     */
    public static void main(String[] args)
    {
        ServerInitialiser initialiser = new ServerInitialiser();
        ServerController controller = new ServerController();
        ServerMessenger messenger = new ServerMessenger();

        String screenName = args[0];
        String command = args[1];

        if (command.equals("start")) {
            controller.start(screenName);
        } else if (command.equals("stop")) {
            // TODO pass messenger to work with
            controller.stop(screenName);
        } else if (command.equals("restart")) {
            controller.restart(screenName);
        } else if (command.equals("status")) {
            controller.isRunning(screenName);
        } else if (command.equals("pid")) {
            controller.getPid(screenName);
        } else if (command.equals("command")) {
            // TODO dev
            String serverCommand = args[2];
            messenger.sendServerCommand(screenName, serverCommand);
        } else if (command.equals("test")) {
            initialiser.serverExists(screenName);
        }
    }
}
