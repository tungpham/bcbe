package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.ConversationDTO;
import com.tung.bcbe.dto.MessageDTO;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.Conversation;
import com.tung.bcbe.model.ConversationMessage;
import com.tung.bcbe.model.Message;
import com.tung.bcbe.model.Message2;
import com.tung.bcbe.model.MessageStatus;
import com.tung.bcbe.model.Project;
import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.model.ProposalMessageFile;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.ConversationMessageRepository;
import com.tung.bcbe.repository.ConversationRepository;
import com.tung.bcbe.repository.Message2Repository;
import com.tung.bcbe.repository.MessageRepository;
import com.tung.bcbe.repository.ProjectRepository;
import com.tung.bcbe.repository.ProposalMessageFileRepository;
import com.tung.bcbe.repository.ProposalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/messages")
@Slf4j
public class MessageController {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ContractorRepository contractorRepository;
    
    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ProjectRepository projectRepository;
    
    @Autowired
    private ProposalMessageFileRepository proposalMessageFileRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private Message2Repository message2Repository;

    @Autowired
    private ConversationMessageRepository conversationMessageRepository;

    @Autowired
    AmazonS3 s3;

    @Value("${S3_BUCKET_PROPOSAL_MSG}")
    private String bucket;

    /**
     * General contractor send message to subcontractor proposal. Get subcontractor info from proposal.
     * @param propId
     * @param message
     * @return
     */
    @PostMapping("/proposals/{prop_id}/tosubcon")
    public Message sendMsgToProposal(@PathVariable(name = "prop_id") UUID propId, @RequestBody @Valid Message message) {
        return proposalRepository.findById(propId).map(proposal -> {
            message.setFrom(proposal.getProject().getGenContractor());
            message.setTo(proposal.getSubContractor());
            message.setProposal(proposal);
            message.setStatus(Message.Status.UNREAD);
            return messageRepository.save(message);
        }).orElseThrow(Util.notFound(propId, Proposal.class));
    }
    
    /*
    Get all msg for a proposal
     */
    @GetMapping("/proposals/{prop_id}")
    public Page<Message> getMsgForProposal(@PathVariable(name = "prop_id") UUID propId, Pageable pagable) {
        return messageRepository.findByProposalIdOrderByCreatedAtDesc(propId, pagable);   
    }

    /**
     * Send msg to gen contractor from proposal subcontractor
     * @param propId
     * @param message
     * @return
     */
    @PostMapping("/proposals/{prop_id}/togencon")
    public Message sendMsgToGenContractor(@PathVariable(name = "prop_id") UUID propId, @RequestBody @Valid Message message) {
        return proposalRepository.findById(propId).map(proposal -> {
            message.setTo(proposal.getProject().getGenContractor());
            message.setFrom(proposal.getSubContractor());
            message.setProposal(proposal);
            message.setStatus(Message.Status.UNREAD);
            return messageRepository.save(message);
        }).orElseThrow(Util.notFound(propId, Proposal.class));
    }
    
    @PutMapping("/{msg_id}/read")
    public void updateStatus(@PathVariable(name = "msg_id") UUID msgId) {
        messageRepository.findById(msgId).ifPresent(message -> {
            message.setStatus(Message.Status.READ);
            messageRepository.save(message);
        });
    }

    @GetMapping("/{msg_id}/files/{file_name}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "msg_id") UUID msgId,
                                           @PathVariable(value = "file_name") String fileName) throws IOException {
        String key = msgId + "/" + fileName;
        return Util.download(s3, bucket, key);
    }
    
    @PostMapping("/{msg_id}/files/upload")
    public void uploadFile(@PathVariable(value = "msg_id") UUID msgId,
                           @RequestParam("file") MultipartFile file) {
        upload(msgId, file);
    }
    
    @PostMapping("/{msg_id}/files/upload/multiple")
    public void uploadFile(@PathVariable(value = "msg_id") UUID msgId,
                           @RequestParam("file") MultipartFile[] files) {
        for (MultipartFile file : files) {
            upload(msgId, file);
        }
    }

    public void upload(UUID msgId, MultipartFile file) {
        messageRepository.findById(msgId).ifPresent(message -> {
            try {
                ProposalMessageFile proposalMsgFile = new ProposalMessageFile();
                proposalMsgFile.setName(file.getOriginalFilename());
                proposalMsgFile.setMessage(message);

                String key = message.getId() + "/" + file.getOriginalFilename();
                Util.putFile(s3, bucket, key, file);
                proposalMessageFileRepository.save(proposalMsgFile);

            } catch (IOException e) {
                log.error("error uploading file", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Send a message to a project. If there's no existing conversation, then create one. Else, use existing conversation
     * @param projectId
     */
    @PostMapping("/project/{project_id}/contractor/{con_id}")
    public void createMessage(@PathVariable(value = "project_id") UUID projectId,
                              @PathVariable(value = "con_id") UUID conId,
                              @RequestBody Message2 message2) {
        Conversation existing = conversationRepository.findByProjectIdAndContractorId(projectId, conId);
        Contractor contractor = contractorRepository.findById(conId).orElseThrow(Util.notFound(conId, Contractor.class));
        if (existing == null) {
            Project project = projectRepository.findById(projectId).orElseThrow(Util.notFound(projectId, Project.class));
            Conversation conversation = Conversation.builder().contractor(contractor).project(project).build();
            existing = conversationRepository.save(conversation);
        }

        message2.setSender(contractor);
        message2.setStatus(MessageStatus.UNREAD);
        Message2 msg = message2Repository.save(message2);

        ConversationMessage conversationMessage = ConversationMessage.builder().conversation(existing).message2(msg).build();
        conversationMessageRepository.save(conversationMessage);
    }

    @PostMapping("/conversations/{convo_id}/contractor/{con_id}")
    public void createMessageForConversation(@PathVariable(value = "convo_id") UUID convoId,
                              @PathVariable(value = "con_id") UUID conId,
                              @RequestBody Message2 message2) {
        Conversation conversation = conversationRepository.findById(convoId).orElseThrow(Util.notFound(convoId, Conversation.class));
        Contractor contractor = contractorRepository.findById(conId).orElseThrow(Util.notFound(conId, Contractor.class));
        message2.setStatus(MessageStatus.UNREAD);
        message2.setSender(contractor);
        Message2 msg = message2Repository.save(message2);

        ConversationMessage conversationMessage = ConversationMessage.builder().message2(msg).conversation(conversation).build();
        conversationMessageRepository.save(conversationMessage);
    }

    /**
     * Get list of conversations for a given project
     * @param projectId
     * @param pageable
     * @return
     */
    @GetMapping("/project/{project_id}/conversations")
    public Page<ConversationDTO> getProjectConversations(@PathVariable(value = "project_id") UUID projectId, Pageable pageable) {

        Page<Conversation> page = conversationRepository.findByProjectId(projectId, pageable);

        List<ConversationDTO> list = page.getContent().stream().map(conversation -> {
            ConversationMessage latestConversationMessage = conversationMessageRepository.findTopByConversationIdOrderByMessage2UpdatedAt(conversation.getId());
            return toConversationDTO(conversation, latestConversationMessage == null ? null : latestConversationMessage.getMessage2());
        }).collect(Collectors.toList());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    @GetMapping("/conversations/contractor/{con_id}")
    public Page<ConversationDTO> getConversationsByContractor(@PathVariable(value = "con_id") UUID conId, Pageable pageable) {

        Page<Conversation> page = conversationRepository.findByContractorId(conId, pageable);

        List<ConversationDTO> list = page.getContent().stream().map(conversation -> {
            ConversationMessage latestConversationMessage = conversationMessageRepository.findTopByConversationIdOrderByMessage2UpdatedAt(conversation.getId());
            return toConversationDTO(conversation, latestConversationMessage == null ? null : latestConversationMessage.getMessage2());
        }).collect(Collectors.toList());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    /**
     * Get list of messages from a given conversation
     * @param convoId
     * @param pageable
     * @return
     */
    @GetMapping("/conversations/{convo_id}")
    public Page<MessageDTO> getConversationMessages(@PathVariable(value = "convo_id") UUID convoId, Pageable pageable) {

        Page<ConversationMessage> page = conversationMessageRepository.findAllByConversationId(convoId, pageable);

        List<MessageDTO> dtos = page.getContent().stream()
                .map(convoMsg -> toMessageDTO(convoMsg.getMessage2()))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    public MessageDTO toMessageDTO(Message2 message2) {
        if (message2 == null) {
            return null;
        }

        return MessageDTO.builder()
                .conId(message2.getSender().getId())
                .timestamp(message2.getUpdatedAt())
                .status(message2.getStatus())
                .message(message2.getContent()).build();
    }

    public ConversationDTO toConversationDTO(Conversation conversation, Message2 latestMsg) {
        if (conversation == null) {
            return null;
        }

        return ConversationDTO.builder()
                .projectTitle(conversation.getProject().getTitle())
                .latestMessage(toMessageDTO(latestMsg))
                .id(conversation.getId())
                .build();
    }
}