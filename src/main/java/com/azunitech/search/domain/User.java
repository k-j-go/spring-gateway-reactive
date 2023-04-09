package com.azunitech.search.domain;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class User {
    private int id;
    private String title;
    private String author;
}
