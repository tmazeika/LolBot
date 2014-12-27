package com.bionicrm.lolbot;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;

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
    private PircBotX bot;
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

        lolBot.startBot();
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
        // noinspection unchecked
        final Configuration.Builder confBuilder = new Configuration.Builder()
                .addListener(new IRCListener(faxDater))
                .setAutoNickChange(true)
                .setLogin(prop("login", prop("nick"))) // login, default to nick
                .setVersion("LolBot v1.0 - http://goo.gl/NFp4Ww") // version
                .setServer(
                        prop("server"), // server
                        Integer.parseInt(prop("port", "6666"))) // port, default to 6666
                .setName(prop("nick")); // nick

        if (isPropSet("password")) confBuilder.setServerPassword(prop("password")); // server password

        if (isPropSet("channels"))
        {
            for (String c : prop("channels").split(","))
            {
                confBuilder.addAutoJoinChannel("#" + c); // channels
            }
        }

        final Configuration conf = confBuilder.buildConfiguration();

        // noinspection unchecked
        bot = new PircBotX(conf);
    }

    public void startBot()
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                LOGGER.info("Connecting...");

                try
                {
                    bot.startBot();
                }
                catch (IOException | IrcException ex)
                {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);

                    exit();
                }
            }

        }).start();
    }

    @Override
    public void exit()
    {
        inputListener.stopListening();

        faxDater.stopUpdating();

        if (bot.isConnected())
        {
            bot.sendIRC().quitServer("Shutting down...");
        }
        else
        {
            System.exit(0);
        }
    }

    @Override
    public PircBotX getBot()
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
