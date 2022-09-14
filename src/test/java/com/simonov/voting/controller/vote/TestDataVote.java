package com.simonov.voting.controller.vote;

import com.simonov.voting.controller.MatcherFactory;
import com.simonov.voting.model.Vote;
import com.simonov.voting.to.VoteTo;

import java.util.List;

import static com.simonov.voting.controller.AbstractControllerTest.NOW;
import static com.simonov.voting.controller.MatcherFactory.usingEqualsComparator;
import static com.simonov.voting.controller.restaurant.TestDataRestaurant.*;
import static com.simonov.voting.controller.user.TestDataUser.admin;
import static com.simonov.voting.controller.user.TestDataUser.user;

public class TestDataVote {

    public static final Vote vote1 = new Vote(1, NOW.minusDays(1));
    public static final Vote vote2 = new Vote(2, NOW.minusDays(1));
    public static final Vote vote3 = new Vote(3, NOW);
    public static final List<Vote> userVotes = List.of(vote3, vote2);
    public static MatcherFactory.Matcher<VoteTo> VOTE_TO_MATCHER = usingEqualsComparator(VoteTo.class);

    static {
        vote1.setUser(admin).setRestaurant(RUM_RESTAUR_2);
        vote2.setUser(user).setRestaurant(CH_RESTAUR_1);
        vote3.setUser(user).setRestaurant(BLIN_RESTAUR_3);
    }


}