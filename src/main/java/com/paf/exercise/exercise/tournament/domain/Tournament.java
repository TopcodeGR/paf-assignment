package com.paf.exercise.exercise.tournament.domain;


import com.paf.exercise.exercise.player.domain.Player;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tournament")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @Positive
    @Column(nullable = false, name = "reward_amount")
    private Integer rewardAmount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "reward_currency")
    private TournamentRewardCurrency rewardCurrency;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tournament_has_player", joinColumns = @JoinColumn(name = "tournament_id"), inverseJoinColumns = @JoinColumn(name = "player_id"))
    private Set<Player> players;
}

