package com.simonov.voting.controller.vote;

import com.simonov.voting.controller.AbstractControllerTest;
import com.simonov.voting.repository.VoteRepository;
import com.simonov.voting.service.LimiterService;
import com.simonov.voting.to.VoteTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.simonov.voting.controller.restaurant.TestDataRestaurant.REST_2_ID;
import static com.simonov.voting.controller.restaurant.TestDataRestaurant.REST_3_ID;
import static com.simonov.voting.controller.user.TestDataUser.*;
import static com.simonov.voting.controller.vote.TestDataVote.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTest extends AbstractControllerTest {

    private static final String REST_URL = VoteController.REST_URL + '/';

    @Autowired
    private VoteRepository repository;

    @MockBean
    private LimiterService limiterService;

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getOne() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL + "/by-date")
                .param("date", NOW.minusDays(1).toString()))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(toMapper.createTo(vote2)));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void getAll() throws Exception {
        final List<VoteTo> expectedVotes = toMapper.createVoteTos(userVotes);
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(VOTE_TO_MATCHER.contentJson(expectedVotes));
    }

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void create() throws Exception {

        when(limiterService.getTimeLimit()).thenReturn(LocalTime.of(11, 0));

        VoteTo expectedVoteTo = new VoteTo(NOW, REST_3_ID);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", REST_3_ID + ""))
                .andDo(print())
                .andExpect(status().isCreated());

        Optional<VoteTo> actualVoteOp = repository.get(ADMIN_ID, NOW).map(toMapper::createTo);
        assertTrue(actualVoteOp.isPresent(), "Vote must present in DB");
        TestDataVote.VOTE_TO_MATCHER.assertMatch(actualVoteOp.get(), expectedVoteTo);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateBefore() throws Exception {

        when(limiterService.getTimeLimit()).thenReturn(LocalTime.now().plusSeconds(1));

        VoteTo expectedVote = new VoteTo(NOW, REST_2_ID);
        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", String.valueOf(REST_2_ID)))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<VoteTo> actualVoteOp = repository.get(USER_ID, NOW).map(toMapper::createTo);
        assertTrue(actualVoteOp.isPresent(), "Vote must present in DB");
        TestDataVote.VOTE_TO_MATCHER.assertMatch(actualVoteOp.get(), expectedVote);
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void updateAfter() throws Exception {

        when(limiterService.getTimeLimit()).thenReturn(LocalTime.now());
        when(limiterService.getVoteTimeLimitErrorString()).thenReturn("message here");

        perform(MockMvcRequestBuilders.post(REST_URL)
                .param("restaurantId", REST_2_ID + ""))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content()
                        .string(containsString(limiterService.getVoteTimeLimitErrorString())));
    }
}