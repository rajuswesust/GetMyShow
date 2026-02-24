package com.raju.getmyshow.eventcatalog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crew {
    private String name;
    private String role;
    private String department;
}
