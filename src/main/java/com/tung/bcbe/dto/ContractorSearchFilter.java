package com.tung.bcbe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractorSearchFilter {
    
    private String name;
    private String city;
    private String[] specialty;
}
