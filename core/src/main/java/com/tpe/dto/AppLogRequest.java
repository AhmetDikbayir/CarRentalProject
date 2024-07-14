package com.tpe.dto;

import java.time.LocalDateTime;


public class AppLogRequest {

    private String level;//DB ye kaydederken enum olarak değiştireceğiz

    private String description;

    private LocalDateTime time;

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
