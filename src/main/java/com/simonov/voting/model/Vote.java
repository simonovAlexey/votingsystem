package com.simonov.voting.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.StringJoiner;

@Entity
@Table(name = "vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@AllArgsConstructor
@Accessors(chain = true)
public class Vote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    @ToString.Exclude
    private Restaurant restaurant;

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    @NotNull
    private User user;


    public Vote(Integer id, LocalDate date) {
        super(id);
        this.date = date;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "Vote[", "]")
                .add("id=" + id)
                .add("user=" + user)
                .add("restaurant=" + restaurant)
                .add("date=" + date)
                .toString();
    }
}