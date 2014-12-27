package com.bionicrm.lolbot;

public interface FaxesUpdaterController {

    /**
     * Starts updating every 6 hours for new LolFaxes. Immediately updates and then every 6 hours thereafter.
     */
    void startUpdating();

    /**
     * Immediately updates even if currently sleeping.
     */
    void updateNow();

    /**
     * Stops updating every 6 hours for new LolFaxes.
     */
    void stopUpdating();

    /**
     * Restarts the updating process.
     *
     * @see #startUpdating()
     */
    void restartUpdating();

    /**
     * Gets if the updater is currently running.
     *
     * @return if the updater is running
     */
    boolean isUpdating();

}
