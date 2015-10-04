package com.bionicrm.lolbot;

import org.kitteh.irc.client.library.IRCFormat;
import org.kitteh.irc.client.library.event.channel.ChannelMessageEvent;
import org.kitteh.irc.client.library.event.client.ClientConnectedEvent;
import org.kitteh.irc.client.library.event.user.PrivateCTCPQueryEvent;
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class IRCListener {

    private final FaxesHolder faxesHolder;

    public IRCListener(FaxesHolder faxesHolder)
    {
        this.faxesHolder = faxesHolder;
    }

    @Handler
    public void onMessage(ChannelMessageEvent event)
    {
        // check if it is the LolFax command
        if ( ! event.getMessage().trim().equalsIgnoreCase(":lolfax")) return;

        // get all of our LolFaxes
        final List<String> faxes = faxesHolder.getFaxes();

        // return if we don't have any LolFaxes
        if (faxes.isEmpty()) return;

        // pick a random int from 0 to lines.size()
        final int randInt = new Random().nextInt(faxes.size());

        final String fax = faxes.get(randInt);

        // "[LOLFAX] <selected LolFax>"
        event.getChannel().sendMessage(IRCFormat.DARK_GREEN + "[" +
                IRCFormat.GREEN + "LOLFAX" + IRCFormat.RESET + IRCFormat.DARK_GREEN +
                "] " + IRCFormat.RESET + IRCFormat.BOLD + fax);
    }

    @Handler
    public void onConnect(ClientConnectedEvent event)
    {
        Logger.getGlobal().info("Connected!");
    }

    @Handler
    public void onVersion(PrivateCTCPQueryEvent event)
    {
        if (event.getMessage().equalsIgnoreCase("VERSION"))
        {
            event.setReply("VERSION LolBot v1.0 - http://goo.gl/NFp4Ww"); // version
        }
    }

}
