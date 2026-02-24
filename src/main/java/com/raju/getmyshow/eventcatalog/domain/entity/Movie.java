package com.raju.getmyshow.eventcatalog.domain.entity;

import com.raju.getmyshow.eventcatalog.domain.CastMember;
import com.raju.getmyshow.eventcatalog.domain.CensorRating;
import com.raju.getmyshow.eventcatalog.domain.Crew;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @Column(name = "genre", nullable = false, length = 100)
    private String genre;

    @Column(name = "language", nullable = false, length = 50)
    private String language;

    @Column(nullable = false, length = 50)
    private String director;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cast_members", columnDefinition = "jsonb")
    private List<CastMember> castMembers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "crew", columnDefinition = "jsonb")
    private List<Crew> crews;

    @Enumerated(EnumType.STRING)
    @Column(name = "censor_rating", length = 10)
    private CensorRating censorRating;

    @Column(name = "imdb_rating", precision = 3, scale = 1)
    private BigDecimal imdbRating;

    @Column(name = "user_rating", nullable = false, scale = 1)
    private BigDecimal user_rating;

    @Column(name = "release_date", nullable = false)
    private LocalDate release_date;

    @Column(name = "original_language", nullable = false, length = 50)
    private String originalLanguage;

    @Column(name = "country_of_origin", nullable = false, length = 50)
    private String countryOfOrigin;

    @Column(columnDefinition = "TEXT[]")
    private String[] formats;
}
