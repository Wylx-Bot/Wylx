package com.wylxbot.wylx.Commands.TimeConversion;

public enum Timezone {
    // TODO: Put in way more timezones / Use Java's Timezone Library
    PST("PST", "Pacific Standard Time", 0),
    MST("MST", "Mountain Standard Time", 1),
    CST("CST", "Central Standard Time", 2),
    EST("EST", "Eastern Standard Time", 3);

    public final String abrv;
    public final String name;
    public final int offset;
    Timezone(String abrv, String name, int offset){
        this.abrv = abrv;
        this.name = name;
        this.offset = offset;
    }

    static Timezone getTimezone(String abrv){
        for(Timezone zone : Timezone.values()){
            if(abrv.equalsIgnoreCase(zone.abrv)){
                return zone;
            }
        }
        return null;
    }
}
