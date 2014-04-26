package com.relivethefuture.generic;

/**
 * Created by martin on 08/01/12 at 16:50
 */
public class BasicZoneFactory implements ZoneFactory {
    public Zone createZone() {
        return new BasicZone();
    }
}
