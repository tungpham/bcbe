package com.tung.bcbe.dto;

import com.tung.bcbe.model.Contractor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractorSearchResultDto {

    private Contractor contractor;
    private ReviewSummary reviewSummary;
}
