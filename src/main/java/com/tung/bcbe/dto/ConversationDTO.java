package com.tung.bcbe.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class ConversationDTO {
    private UUID id;
    private MessageDTO latestMessage;
    private String projectTitle;
    private UUID projectOwnerId;
    private String projectOwnerName;
}
