package com.bionicrm.lolbot;

import org.kitteh.irc.client.library.Client;

public interface LolBotController {

    /**
     * Exits the application gracefully.
     */
    void exit();

    /**
     * Gets the KICL bot.
     *
     * @return the bot
     */
    Client getBot();

}
