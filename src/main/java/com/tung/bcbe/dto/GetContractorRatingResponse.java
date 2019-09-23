package com.tung.bcbe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetContractorRatingResponse {

    private double oneStarRating;
    private double twoStarRating;
    private double threeStarRating;
    private double fourStarRating;
    private double fiveStarRating;

    private int reviews;
}
