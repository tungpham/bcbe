package com.tung.bcbe.dto;

import com.tung.bcbe.model.Contractor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class MessageDTO {

    public enum Status {
        READ,
        UNREAD
    }

    private UUID id;
    private Contractor sender;
    private String message;
    private Instant timestamp;
    private Status status;
}
