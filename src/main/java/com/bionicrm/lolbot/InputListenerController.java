package com.bionicrm.lolbot;

public interface InputListenerController {

    /**
     * Starts listening for command line input.
     */
    void startListening();

    /**
     * Stops listening for command line input.
     */
    void stopListening();

    /**
     * Gets if the listener is currently running.
     *
     * @return if the listener is running
     */
    boolean isListening();

}
