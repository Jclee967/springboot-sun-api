package com.example.code.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Table(name="history")
@Entity
public class History {
    @Id
    @GeneratedValue
    private Long id;

    private String lat;

    private String lng;

    @Lob
    private String response;

    private ZonedDateTime timestamp;

    public History(String lat, String lng, String response, ZonedDateTime timestamp){
        this.lat = lat;
        this.lng = lng;
        this.response = response;
        this.timestamp = timestamp;
    }

}
