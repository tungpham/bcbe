package com.tung.bcbe.dto;

import com.tung.bcbe.model.Project;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class ConversationDTO {

    private UUID id;
    private List<MessageDTO> messages;
    private MessageDTO latestMessage;
    private Project project;
}
