package com.bionicrm.lolbot;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FaxesUpdater implements FaxesHolder, FaxesUpdaterController {

    private final URL faxesUrl;
    private final int updatePeriod;
    private final Object faxesLock = new Object();

    private List<String> faxes = new ArrayList<>();
    private boolean updating;
    private Thread updater;

    public FaxesUpdater(URL faxesUrl, int updatePeriod)
    {
        this.faxesUrl = faxesUrl;
        this.updatePeriod = updatePeriod;
    }

    @Override
    public void startUpdating()
    {
        // return if we've already started
        if (updating) return;

        updating = true;

        // create the updater in a new thread
        updater = new Thread(new Updater());

        updater.start();
    }

    @Override
    public void updateNow()
    {
        updater.interrupt();
    }

    @Override
    public void stopUpdating()
    {
        // return if we've already stopped
        if ( ! updating) return;

        updating = false;

        updater.interrupt();
    }

    @Override
    public void restartUpdating()
    {
        stopUpdating();

        startUpdating();
    }

    @Override
    public boolean isUpdating()
    {
        return updating;
    }

    @Override
    public List<String> getFaxes()
    {
        // the updater may be setting a new value while we're returning it
        synchronized (faxesLock)
        {
            // copy to prevent modification
            return new ArrayList<>(faxes);
        }
    }

    /**
     * Updates the LolFax list every 6 hours. The list is retrieved from the configured "lolfaxes-url" in
     * 'config.properties'. Each line should be a separate LolFax.
     */
    private class Updater implements Runnable
    {

        @Override
        public void run()
        {
            while (updating)
            {
                try
                {
                    // we may be currently returning the faxes list, so let's not interrupt it
                    synchronized (faxesLock)
                    {
                        // read all lines (faxes) from the specified LolFaxes URL
                        faxes = IOUtils.readLines(faxesUrl.openStream());

                        final Iterator<String> itr = faxes.iterator();

                        // get rid of blank faxes
                        while (itr.hasNext())
                        {
                            if (itr.next().trim().isEmpty())
                            {
                                itr.remove();
                            }
                        }
                    }

                    // stop if we stopped updating in the middle of reading the faxes
                    if ( ! updating) break;

                    // millis -> seconds -> minutes (updatePeriod)
                    Thread.sleep(1000 * 60 * updatePeriod);
                }
                catch (IOException ex)
                {
                    Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
                }
                catch (InterruptedException ignored) { }
            }
        }

    }

}
