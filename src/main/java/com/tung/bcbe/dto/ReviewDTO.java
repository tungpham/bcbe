package com.tung.bcbe.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReviewDTO {

    private int rating;
    private String[] qualities;
    private String review;
    private String reviewerFirstName;
    private String reviewerLastName;
    private String reviewerEmail;
}
