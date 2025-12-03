package com.gundam.gundam_web.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumPost {
    private Integer id;
    private String callsign;
    private String message;
    private LocalDateTime postTime;
}