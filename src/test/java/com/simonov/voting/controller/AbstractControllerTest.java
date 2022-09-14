package com.simonov.voting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simonov.voting.service.JsonTestService;
import com.simonov.voting.service.ToMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;
import util.JsonUtil;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {

    public static final LocalDate NOW = LocalDate.now();

    @Autowired
    protected ToMapper toMapper;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JsonTestService jsonTestService;

    @Autowired
    private MockMvc mockMvc;

    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        JsonUtil.setMapper(objectMapper);
        log.info(String.format("------------------ test started: %s ------------------ ",
                testInfo.getTestClass().orElse(NoClassDefFoundError.class).getSimpleName() + "." + testInfo.getDisplayName()));
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        log.info(String.format("------------------ test finished: %s ------------------  %n%n",
                testInfo.getTestClass().orElse(NoClassDefFoundError.class).getSimpleName() + "." + testInfo.getDisplayName()));
    }
}