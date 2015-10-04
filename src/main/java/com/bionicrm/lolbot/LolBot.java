package com.bionicrm.lolbot;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.kitteh.irc.client.library.Client;
import org.kitteh.irc.client.library.ClientBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LolBot implements LolBotController {

    private static final Logger LOGGER = Logger.getGlobal();
    private static final File JAR_DIR;

    private Properties props;
    private Client bot;
    private FaxesUpdater faxDater;
    private InputListener inputListener;

    static
    {
        File jar = new File(".");

        try
        {
            jar = new File(LolBot.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        }
        catch (URISyntaxException ex)
        {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }

        JAR_DIR = jar;
    }

    public static void main(String[] args) throws Exception
    {
        final LolBot lolBot = new LolBot();

        lolBot.loadPropsFile();

        lolBot.createFaxesUpdater();

        final CommandHandlerInterface cmdHandler = new CommandHandler(lolBot.getFaxDaterController(), lolBot);

        lolBot.createInputListener(cmdHandler);

        lolBot.createBot();
    }

    public void loadPropsFile() throws IOException
    {
        final String propsFileName = "config.properties";

        final File propsFile = new File(JAR_DIR, propsFileName);

        // copy the config.properties file from resources if one does not exist
        if ( ! propsFile.exists())
        {
            // create the file
            propsFile.createNewFile();

            // get the input stream of the file in resources
            final InputStream resPropsIn = Resources.getResource(propsFileName).openStream();

            // get the output stream of the newly created file
            final OutputStream propsOut = new FileOutputStream(propsFile);

            // copy the contents of the file in resources to the newly created file
            IOUtils.copy(resPropsIn, propsOut);
        }

        final InputStream in = new FileInputStream(propsFile);

        // load the properties
        props = new Properties();
        props.load(in);
    }

    public void createFaxesUpdater() throws MalformedURLException
    {
        final URL faxesUrl = new URL(prop("lolfaxes-url"));
        final int updatePeriod = Integer.parseInt(prop("lolfaxes-update-period"));

        faxDater = new FaxesUpdater(faxesUrl, updatePeriod);

        faxDater.startUpdating();
    }

    public void createInputListener(CommandHandlerInterface cmdHandler)
    {
        inputListener = new InputListener(cmdHandler);

        inputListener.startListening();
    }

    public void createBot()
    {
        final ClientBuilder builder = Client.builder()
                .user(prop("login", prop("nick"))) // login, default to nick
                .server(prop("server")) // server
                .server(Integer.parseInt(prop("port", "6666"))) // port, default to 6666
                .nick(prop("nick")); // nick

        if (isPropSet("password")) builder.serverPassword(prop("password")); // server password



        bot = builder.build();
        bot.getEventManager().registerEventListener(new IRCListener(faxDater));
        if (isPropSet("channels"))
        {
            for (String c : prop("channels").split(","))
            {
                bot.addChannel("#" + c); // channels
            }
        }
    }

    @Override
    public void exit()
    {
        inputListener.stopListening();

        faxDater.stopUpdating();

        bot.shutdown("Shutting down...");
        System.exit(0);
    }

    @Override
    public Client getBot()
    {
        return bot;
    }

    public FaxesUpdaterController getFaxDaterController()
    {
        return faxDater;
    }

    private String prop(String key, String defaultValue)
    {
        final String value = props.getProperty(key, defaultValue);

        if (value.isEmpty())
        {
            return defaultValue;
        }

        return value;
    }

    private String prop(String key)
    {
        return prop(key, "");
    }

    private boolean isPropSet(String key)
    {
        return ! prop(key).isEmpty();
    }

}
