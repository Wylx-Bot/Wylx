package com.wylxbot.wylx.Commands.TimeConversion;

public class Time {
    private final int hour;
    private final int minutes;
    private final boolean isAM;
    private final Timezone timezone;

    public Time(int hour, int minutes, boolean isAM, Timezone timezone){
        this.hour = hour;
        this.minutes = minutes;
        this.isAM = isAM;
        this.timezone = timezone;
    }

    public Time(String timeString, Timezone timezone){
        this.timezone = timezone;

        isAM = timeString.contains("a");
        timeString = timeString.replace(isAM ? "am" : "pm", "");

        if(!timeString.contains(":")){
            hour = Integer.parseInt(timeString.strip());
            minutes = 0;
        } else {
            String[] pieces = timeString.split(":");
            hour = Integer.parseInt(pieces[0].strip());
            minutes = Integer.parseInt(pieces[1].strip());
        }
    }

    public Time convertTo(Timezone newTimezone){
        if(newTimezone == timezone) return this;

        // Conversion for normal times
        int pstHour = hour - timezone.offset;
        int newHour = pstHour + newTimezone.offset;
        boolean newIsAM = isAM;
        while(newHour < 1 || newHour > 12){
            newIsAM = !newIsAM;
            newHour = newHour < 1 ? newHour + 12 : newHour - 12;
        }

        // 12am / pm is dumb
        if(hour == 12|| newHour == 12){
            newIsAM = !newIsAM;
        }

        return new Time(newHour, minutes, newIsAM, newTimezone);
    }

    @Override
    public String toString() {
        return String.format("%d:%02d%s", hour, minutes, isAM ? "am" : "pm");
    }
}
