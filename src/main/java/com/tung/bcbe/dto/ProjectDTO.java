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

    /*
    in case the project is in ONGOING or ARCHIVED status, contractor is the contractor working on the project
     */
    private Contractor contractor;
}
