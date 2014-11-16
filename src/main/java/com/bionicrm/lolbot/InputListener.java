package com.bionicrm.lolbot;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputListener implements InputListenerController {

    private static final Pattern CMD_PATTERN = Pattern.compile("(?:\"([^\"]*)\")|(\\S+)");

    private final CommandHandlerInterface cmdHandler;

    private boolean listening;
    private Scanner listenerScanner;

    public InputListener(CommandHandlerInterface cmdHandler)
    {
        this.cmdHandler = cmdHandler;
    }

    @Override
    public void startListening()
    {
        // return if we've already started
        if (listening) return;

        listening = true;

        // create and start the listener in a new thread
        new Thread(new Listener()).start();
    }

    @Override
    public void stopListening()
    {
        // return if we've already stopped or there's no scanner to stop
        if ( ! listening || listenerScanner == null) return;

        listening = false;

        listenerScanner.close();
    }

    @Override
    public boolean isListening()
    {
        return listening;
    }

    /**
     * Listens for input from the command line and sends it off to be handled by a CommandHandler.
     *
     * @see CommandHandlerInterface
     */
    private class Listener implements Runnable
    {

        @Override
        public void run()
        {
            listenerScanner = new Scanner(System.in);

            while (listening)
            {
                // read the next line
                final String line = listenerScanner.nextLine();

                // stop if we stopped listening
                if ( ! listening) break;

                final List<String> splitLine = new ArrayList<>();

                final Matcher cmdMatcher = CMD_PATTERN.matcher(line);

                while (cmdMatcher.find())
                {
                    splitLine.add(cmdMatcher.group());
                }

                final int splitLength = splitLine.size();

                // ignore if we're dealing with a blank line
                if (splitLength == 0) continue;

                // get the base command
                final String cmd = splitLine.get(0);

                List<String> args = new ArrayList<>();

                // if we have arguments available, set them!
                if (splitLength > 1)
                {
                    args = splitLine.subList(1, splitLength);
                }

                cmdHandler.handleCommand(cmd, args);
            }

            listenerScanner.close();
        }

    }

}
