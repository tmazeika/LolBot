package com.bionicrm.lolbot;

import java.util.List;
import java.util.logging.Logger;

public class CommandHandler implements CommandHandlerInterface {

    private static final Logger LOGGER = Logger.getGlobal();

    private final FaxesUpdaterController updaterCont;
    private final LolBotController botCont;

    public CommandHandler(FaxesUpdaterController updaterCont, LolBotController botCont)
    {
        this.updaterCont = updaterCont;
        this.botCont = botCont;
    }

    @Override
    public void handleCommand(String cmd, List<String> args)
    {
        switch (cmd)
        {
            case "exit":
            case "quit":
            case "part":
                LOGGER.info("Exiting...");

                botCont.exit();
                break;

            default:
                LOGGER.warning("Unknown command '" + cmd + "'");
        }
    }

}
