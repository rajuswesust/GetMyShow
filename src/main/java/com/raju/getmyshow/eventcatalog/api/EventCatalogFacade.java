package com.raju.getmyshow.eventcatalog.api;

import com.raju.getmyshow.eventcatalog.api.dto.SearchShowsRequest;
import com.raju.getmyshow.eventcatalog.api.dto.ShowDto;
import com.raju.getmyshow.eventcatalog.domain.entity.Movie;
import com.raju.getmyshow.eventcatalog.dto.MovieDto;

import java.util.List;
import java.util.Optional;

public interface EventCatalogFacade {

    Optional<MovieDto> getMovieById(Long id);

    Optional<ShowDto> getShowById(Long id);

    List<ShowDto> searchShows(SearchShowsRequest request);

    boolean isShowBookable(Long showId);
}
