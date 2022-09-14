package com.simonov.voting.controller.vote;

import com.simonov.voting.controller.AuthUser;
import com.simonov.voting.error.IllegalRequestException;
import com.simonov.voting.model.Restaurant;
import com.simonov.voting.model.Vote;
import com.simonov.voting.repository.RestaurantRepository;
import com.simonov.voting.repository.UserRepository;
import com.simonov.voting.repository.VoteRepository;
import com.simonov.voting.service.LimiterService;
import com.simonov.voting.service.ToMapper;
import com.simonov.voting.to.VoteTo;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO;
import static org.springframework.http.HttpStatus.NO_CONTENT;


@Slf4j
@RestController
@Tag(name = "Vote controller")
@RequestMapping(value = VoteController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {

    static final String REST_URL = "/api/votes";

    private final ToMapper toMapper;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final LimiterService limiterService;
    private final RestaurantRepository restaurantRepository;

    public VoteController(ToMapper toMapper,
                          UserRepository userRepository,
                          VoteRepository voteRepository,
                          LimiterService limiterService,
                          RestaurantRepository restaurantRepository) {
        this.toMapper = toMapper;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.limiterService = limiterService;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/by-date")
    public VoteTo get(@AuthenticationPrincipal AuthUser authUser,
                      @RequestParam @Nullable @DateTimeFormat(iso = ISO.DATE) LocalDate date) {
        LocalDate voteDate = (date == null) ? LocalDate.now() : date;
        int userId = authUser.getId();
        log.info("Try get() votes user: {} date: {}", userId, voteDate);
        return voteRepository.get(userId, voteDate).map(toMapper::createTo).orElseThrow(() ->
                new IllegalRequestException("Vote with date:" + voteDate + "User: " + userId + " not found"));
    }

    @GetMapping
    public List<VoteTo> getAll(@AuthenticationPrincipal AuthUser authUser) {
        final int authUserId = authUser.getId();
        log.info("getAll() votes. user: {}", authUserId);
        return toMapper.createVoteTos(voteRepository.getAll(authUserId));
    }


    @PostMapping
    @Transactional
    public ResponseEntity<VoteTo> createOrUpdate(@AuthenticationPrincipal AuthUser authUser,
                                                 @RequestParam int restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() ->
                new IllegalRequestException("Restaurant with id: " + restaurantId + " not found")
        );

        int userId = authUser.getId();
        final LocalDate now = LocalDate.now();
        Vote vote = voteRepository.get(userId, now)
                .orElse(new Vote(restaurant, now, userRepository.getReferenceById(userId)));

        if (vote.isNew()) {
            log.info("user: {} vote for restaurant: {}", userId, restaurantId);
            Vote savedVote = voteRepository.save(vote);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(REST_URL).buildAndExpand().toUri();
            return ResponseEntity.created(uriOfNewResource).body(toMapper.createTo(savedVote));
        }
        if (vote.getRestaurant().id() == restaurantId) {
            log.info("user: {} vote for restaurant: {} again", userId, restaurantId);
            return ResponseEntity.status(NO_CONTENT).build();
        }

        if (!LocalTime.now().isBefore(limiterService.getTimeLimit())) {
            throw new IllegalRequestException(limiterService.getVoteTimeLimitErrorString());
        }

        log.info("user {} changed vote for restaurant: {}", userId, restaurantId);
        vote.setRestaurant(restaurant);
        voteRepository.save(vote);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}