package com.tung.bcbe.dto;

import com.tung.bcbe.model.Project;
import lombok.Data;

@Data
public class ProjectWithSpecialtyDTO {

    private Project project;
    private String[] specialtyIds;
}
