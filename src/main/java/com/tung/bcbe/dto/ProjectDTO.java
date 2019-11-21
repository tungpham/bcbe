package com.tung.bcbe.dto;

import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.Project;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ProjectDTO {
    private Project project;
    private int numberOfBids;
    private Contractor contractor;
}
