package com.simonov.voting.to;

import java.time.LocalDate;

public record VoteTo(LocalDate date, int restaurantId) {
}