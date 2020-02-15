package com.tung.bcbe.dto;

import com.tung.bcbe.model.MessageStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Builder
@Data
public class MessageDTO {

    private UUID id;
    private UUID senderId;
    private String senderName;
    private String message;
    private Date timestamp;
    private MessageStatus status;
}
