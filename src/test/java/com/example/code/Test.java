package com.example.code;

import com.example.code.service.SunService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Test {

    @Autowired
    SunService sunService;

    @org.junit.jupiter.api.Test
    public void testing(){
        String twilightBegin = "2023-12-29T00:47:43+00:00";
        String sunrise = "2023-12-29T02:23:33+00:00";
        String sunset = "2023-12-29T11:48:01+00:00";
        String twilightEnd = "2023-12-29T13:23:51+00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        LocalTime time1 = LocalTime.parse(twilightBegin, formatter);
        LocalTime time2 = LocalTime.parse(sunrise, formatter);

        System.out.println(java.time.Duration.between(time1, time2).getSeconds());

        LocalTime sunsetTime = LocalTime.parse(sunset, formatter);
        LocalTime twilightEndTime = LocalTime.parse(twilightEnd, formatter);
        System.out.println(java.time.Duration.between(sunsetTime, twilightEndTime).getSeconds());
    }

    @org.junit.jupiter.api.Test
    public void testingCurrentTime(){
        String twilightBegin = "2023-12-31T06:00:00+00:00";
        String sunrise = "2023-12-31T07:00:00+00:00";
        String sunset = "2023-12-31T17:00:00+00:00";
        String twilightEnd = "2023-12-31T18:00:00+00:00";
//        System.out.println(getCurrentSunStatus(twilightBegin,sunrise,sunset,twilightEnd));

        System.out.println(getCurrentTemp(twilightBegin,sunrise,sunset,twilightEnd));
    }

    public int getCurrentTemp(String twilightBegin, String sunrise, String sunset, String twilightEnd){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T01:00:00+00:00", formatter); // 2700
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T06:30:00+00:00", formatter); // 3525
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T07:00:00+00:00", formatter); // 4350
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T08:00:00+00:00", formatter); // 6000
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T12:00:00+00:00", formatter); // 6000
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T16:00:00+00:00", formatter); // 6000
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T17:00:00+00:00", formatter); // 4350
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T18:00:00+00:00", formatter); // 2700
//        ZonedDateTime currentTime = ZonedDateTime.parse("2023-12-31T22:00:00+00:00", formatter); // 2700

        // Parse the time strings to ZonedDateTime objects
        ZonedDateTime twilightBeginTime = ZonedDateTime.parse(twilightBegin, formatter);
        ZonedDateTime sunriseTime = ZonedDateTime.parse(sunrise, formatter);
        ZonedDateTime sunsetTime = ZonedDateTime.parse(sunset, formatter);
        ZonedDateTime twilightEndTime = ZonedDateTime.parse(twilightEnd, formatter);


        long twilightSec = Duration.between(twilightBeginTime, sunriseTime).getSeconds();

        System.out.println("sunrise range:" + sunriseTime.plusSeconds(twilightSec));
        System.out.println("sunset range:" + sunsetTime.minusSeconds(twilightSec));

        if (currentTime.isBefore(twilightBeginTime) || currentTime.isEqual(twilightBeginTime) || currentTime.isEqual(twilightEndTime) ||currentTime.isAfter(twilightEndTime)) {
            return 2700;
        }

        if(currentTime.isAfter(twilightBeginTime) && currentTime.isBefore(sunriseTime.plusSeconds(twilightSec))){   // twilightBegin < currentTime < sunrise + twilightSec
            return (int) ((6000 - 2700) * Duration.between(twilightBeginTime, currentTime).getSeconds() / (twilightSec * 2.0) + 2700);
        }

        if(currentTime.isBefore(twilightEndTime) && currentTime.isAfter(sunsetTime.minusSeconds(twilightSec))){     // sunset - twilightSec < currentTime < twilightEnd
            return (int) ((6000 - 2700) * Duration.between(currentTime, twilightEndTime).getSeconds() / (twilightSec * 2.0) + 2700);
        }

        return 6000;
    }


}
