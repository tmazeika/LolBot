package com.bionicrm.lolbot;

import org.pircbotx.PircBotX;

public interface LolBotController {

    /**
     * Exits the application gracefully.
     */
    void exit();

    /**
     * Gets the PircBotX bot.
     *
     * @return the bot
     */
    PircBotX getBot();

}
