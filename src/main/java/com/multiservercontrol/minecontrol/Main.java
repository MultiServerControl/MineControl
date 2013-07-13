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
        String serverName = args[0];
        String command = args[1];

        ServerInitialiser initialiser = new ServerInitialiser();
        ServerController controller = new ServerController(serverName);
        ServerMessenger messenger = new ServerMessenger();

        if (command.equals("start")) {
            if (initialiser.serverExists(serverName)) {
                controller.start();
            }
        } else if (command.equals("stop")) {
            // TODO pass messenger to work with
            controller.stop();
        } else if (command.equals("restart")) {
            controller.restart();
        } else if (command.equals("status")) {
            controller.isRunning();
        } else if (command.equals("pid")) {
            controller.getPid();
        } else if (command.equals("command")) {
            // TODO dev
            String serverCommand = args[2];
            messenger.sendServerCommand(serverName, serverCommand);
        } else if (command.equals("create")) {
            initialiser.createServer(serverName);
        }
    }
}
