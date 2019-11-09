package com.tung.bcbe.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractorOverview {

    private int numberOfHires;
    private boolean backgroundChecked;
    private boolean licenseVerified;

}
