package com.bionicrm.lolbot;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class IRCListener extends ListenerAdapter {

    private final FaxesHolder faxesHolder;

    public IRCListener(FaxesHolder faxesHolder)
    {
        this.faxesHolder = faxesHolder;
    }

    @Override
    public void onMessage(MessageEvent event)
    {
        // check if it is the LolFax command
        if ( ! event.getMessage().startsWith(":lolfax")) return;

        // get all of our LolFaxes
        List<String> faxes = faxesHolder.getFaxes();

        // return if we don't have any LolFaxes
        if (faxes.isEmpty()) return;

        // pick a random int from 0 to lines.size()
        int randInt = new Random().nextInt(faxes.size());

        String fax = faxes.get(randInt);

        // "<username>: [lolfax] <selected LolFax>"
        event.respond("[lolfax] " + fax);
    }

    @Override
    public void onConnect(ConnectEvent event)
    {
        Logger.getGlobal().info("Connected!");
    }

}
