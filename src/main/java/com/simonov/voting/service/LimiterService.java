package com.simonov.voting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class LimiterService {
    public static final String UPDATE_VOTE_PREFIX = "Vote can only be changed before ";

    private static final String DATE_TIME_PATTERN = "HH:mm";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    @Value("${app.limits.time-vote-before:11:00}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    LocalTime timeLimit;

    @PostConstruct
    public void init() {
        log.info("LimiterService initialized with  timeLimit: {}", getTimeLimit());
    }

    public LocalTime getTimeLimit() {
        return timeLimit;
    }

    public String getVoteTimeLimitErrorString() {
        return getTimeLimit() == null ?
                "Vote can't be changed" : UPDATE_VOTE_PREFIX + getTimeLimit().format(DATE_TIME_FORMATTER);
    }
}