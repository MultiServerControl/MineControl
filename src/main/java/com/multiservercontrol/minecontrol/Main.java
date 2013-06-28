package com.multiservercontrol.minecontrol;

public class Main {

    /**
     * @param args
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
            initialiser.serverFileExists(screenName);
        }
    }
}
