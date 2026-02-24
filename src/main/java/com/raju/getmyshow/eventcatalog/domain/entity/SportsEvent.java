package com.raju.getmyshow.eventcatalog.domain.entity;

import com.raju.getmyshow.eventcatalog.domain.enums.MatchType;
import com.raju.getmyshow.eventcatalog.domain.enums.SportType;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sports_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportsEvent extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false, length = 50)
    private SportType sportType;

    @Column(name = "team_home",  length = 50)
    private String teamHome;

    @Column(name = "team_away",  length = 50)
    private String teamAway;

    @Column(name = "team_home_logo",  length = 50)
    private String teamHomeLogo;

    @Column(name = "team_away_logo",  length = 50)
    private String teamAwayLogo;

    @Column(name = "tournament_name",  length = 50)
    private String tournamentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_type",  length = 50)
    private MatchType matchType;

    @Column(name = "venue_type",  length = 50)
    private String venueType;

    @Column(name = "expected_duration")
    private Integer expectedDuration;

    @Column(name = "is_international")
    private Boolean isInternational;

    @Column(name = "league_name", length = 255)
    private String leagueName;

    @Column(name = "season", length = 50)
    private String season;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private LocalDateTime updatedAt;

}
