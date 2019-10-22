package com.tung.bcbe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewSummary {
    private double rating = 4.8;
    private int totalReviews = 10;
    private String aReview = "";
}
