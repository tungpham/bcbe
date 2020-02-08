package com.tung.bcbe.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class ConversationDTO {

    private UUID id;
    List<MessageDTO> messages;
    MessageDTO latestMessage;
}
