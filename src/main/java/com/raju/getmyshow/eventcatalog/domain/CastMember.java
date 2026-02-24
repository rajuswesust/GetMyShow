package com.raju.getmyshow.eventcatalog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CastMember {
    private String name;
    private String role;
    private String characterName;
}
