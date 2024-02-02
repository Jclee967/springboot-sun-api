package com.example.code.service;

import com.example.code.entity.History;
import com.example.code.record.ChatApiRequest;
import com.example.code.record.ChatApiResponse;
import com.example.code.record.SunriseApiResponse;
import com.example.code.repository.HistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class SunService {

    @Value("${sunrise-sunset.api}")
    private String sunriseSunsetApiUrl;

    @Value("${openai.api}")
    private String chatApiUrl;

    @Value("${openai.api.model}")
    private String chatApiModel;

    private RestTemplate sunriseApiRestTemplate;

    private RestTemplate chatApiRestTemplate;

    private HistoryRepository historyRepository;

    private final DateTimeFormatter formatter;

    public SunService(
            @Qualifier("sunriseApiRestTemplate") RestTemplate sunApiRestTemplate,
            @Qualifier("chatApiRestTemplate") RestTemplate chatApiRestTemplate,
            HistoryRepository historyRepository){
        this.sunriseApiRestTemplate = sunApiRestTemplate;
        this.chatApiRestTemplate = chatApiRestTemplate;
        this.historyRepository = historyRepository;
        this.formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    public int getTempByCoord(String lat, String lng){

        SunriseApiResponse response = sunriseApiRestTemplate.getForObject(
                sunriseSunsetApiUrl, SunriseApiResponse.class, lat, lng);
        log.info("Response from Sunrise API: " + response.results().toString());

        return calcCurrentTemp(
                response.results().astTwilightBegin(),
                response.results().sunrise(),
                response.results().sunset(),
                response.results().astTwilightEnd());
    }

    public int calcCurrentTemp(String twilightBegin, String sunrise, String sunset, String twilightEnd){

        ZonedDateTime currentTime = getCurrentUTCTime();
        log.info("Current UTC time: " + currentTime);

        // Parse the time strings to ZonedDateTime objects
        ZonedDateTime twilightBeginTime = ZonedDateTime.parse(twilightBegin, formatter);
        ZonedDateTime sunriseTime = ZonedDateTime.parse(sunrise, formatter);
        ZonedDateTime sunsetTime = ZonedDateTime.parse(sunset, formatter);
        ZonedDateTime twilightEndTime = ZonedDateTime.parse(twilightEnd, formatter);

        long twilightSec = Duration.between(twilightBeginTime, sunriseTime).getSeconds();

        if (currentTime.isBefore(twilightBeginTime)
                || currentTime.isEqual(twilightBeginTime)
                || currentTime.isEqual(twilightEndTime)
                || currentTime.isAfter(twilightEndTime)) {
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

    public String getPoemByCoord(String lat, String lng){

        // get sun's current position through sunriseApi
        SunriseApiResponse sunriseApiResponse = sunriseApiRestTemplate.getForObject(
                sunriseSunsetApiUrl, SunriseApiResponse.class, lat, lng);
        log.info("Response from Sunrise API: " + sunriseApiResponse.results().toString());

        String status = getCurrentSunStatus(
                            sunriseApiResponse.results().astTwilightBegin(),
                            sunriseApiResponse.results().sunrise(),
                            sunriseApiResponse.results().sunset(),
                            sunriseApiResponse.results().astTwilightEnd());

        // call chatgpt to get poem
        String prompt = "write a short poem regarding to " + status;
        log.info("Request prompt to Open API: " + prompt);

        ChatApiRequest chatApiRequest = new ChatApiRequest(chatApiModel, prompt);
        ChatApiResponse chatApiResponse = chatApiRestTemplate.postForObject(chatApiUrl, chatApiRequest, ChatApiResponse.class);
        log.info("Response from Open API: " + chatApiResponse);

        if (chatApiResponse == null || chatApiResponse.choices() == null || chatApiResponse.choices().isEmpty()) {
            return "No response from Open API";
        }

        return chatApiResponse.choices().get(0).message().content();
    }

    public String getCurrentSunStatus(String twilightBegin, String sunrise, String sunset, String twilightEnd){

        ZonedDateTime currentTime = getCurrentUTCTime();
        log.info("Current UTC time: " + currentTime);

        if (currentTime.isAfter(ZonedDateTime.parse(twilightBegin, formatter))) {
            if(currentTime.isBefore(ZonedDateTime.parse(sunrise, formatter))){ // between twilightBeginTime and sunriseTime
                return "sunrise";
            } else if(currentTime.isBefore(ZonedDateTime.parse(sunset, formatter))){ // between sunriseTime and sunsetTime
                return "daylight";
            } else if (currentTime.isBefore(ZonedDateTime.parse(twilightEnd, formatter))) { // between sunsetTime and twilightEndTime
                return "sunset";
            }
        }
        return "moonlight";
    }

    public void saveHistory(String lat, String lng, String response){
        historyRepository.save(new History(lat, lng, response, getCurrentUTCTime()));
        log.info("Request history saved");
    }

    public List<History> getHistory(){
        return historyRepository.findAll();
    }

    public static ZonedDateTime getCurrentUTCTime() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

}
