package com.example.code.controller;

import com.example.code.entity.History;
import com.example.code.service.SunService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sun")
public class SunController {

    @Autowired
    private SunService sunService;

    @GetMapping("/temp")
    public Integer getTemperatureByCoord(@RequestParam String lat, @RequestParam String lng){
        Integer response = sunService.getTempByCoord(lat,lng);
        sunService.saveHistory(lat, lng, response.toString());
        return response;
    }

    @GetMapping("/poem")
    public String getPoemByCoord(@RequestParam String lat, @RequestParam String lng){
        String response = sunService.getPoemByCoord(lat, lng);
        sunService.saveHistory(lat, lng, response);
        return response;
    }

    @GetMapping("/history")
    public List<History> getRequestHistory(){
        return sunService.getHistory();
    }

}
