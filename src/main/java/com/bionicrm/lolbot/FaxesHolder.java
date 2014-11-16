package com.bionicrm.lolbot;

import java.util.List;

public interface FaxesHolder {

    /**
     * Gets a copy of the latest retrieved LolFaxes, excluding CRLF's.
     *
     * @return the LolFaxes
     */
    List<String> getFaxes();

}
