package com.raju.getmyshow.eventcatalog.domain.entity;

import com.raju.getmyshow.eventcatalog.domain.enums.EventStatus;
import com.raju.getmyshow.eventcatalog.domain.enums.EventType;
import com.raju.getmyshow.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;

    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 80)
    private EventStatus status;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured;

    @Column(columnDefinition = "TEXT[]")
    private String[] tags;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    public void publish() {
        if(this.status == EventStatus.PUBLISHED) {
            throw new IllegalStateException("Event already published");
        }
        this.status = EventStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void archieve() {
        this.status = EventStatus.ARCHIVED;
    }

    public boolean isPublished() {
        return this.status == EventStatus.PUBLISHED;
    }

}
