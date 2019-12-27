package com.tung.bcbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private int rating;
    private String[] qualities;
    private String review;
    private String reviewerFirstName;
    private String reviewerLastName;
    private String reviewerEmail;
}
