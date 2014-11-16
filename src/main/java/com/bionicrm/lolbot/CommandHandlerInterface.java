package com.bionicrm.lolbot;

import java.util.List;

public interface CommandHandlerInterface {

    /**
     * Handles an inputted command from the command line.
     *
     * @param cmd the entered command
     * @param args any entered args, or empty list if none
     */
    void handleCommand(String cmd, List<String> args);

}
