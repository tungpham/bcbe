package com.tung.bcbe.dto;

import com.tung.bcbe.model.Specialty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SpecialtyCarouselDto {
    private Specialty specialty;
    private String imageUrl;
}
