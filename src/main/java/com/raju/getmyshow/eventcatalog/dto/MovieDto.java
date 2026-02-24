package com.raju.getmyshow.eventcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Public DTO - exposed to other modules
 * Contains only necessary information, hides internal details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private Long eventId;
    private String title;
    private String description;
    private Integer duration;
    private String genre;
    private String language;
    private String director;
    private BigDecimal imdbRating;
    private LocalDate releaseDate;
    private String[] formats;
    private String posterUrl;
}