package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.tung.bcbe.dto.ConversationDTO;
import com.tung.bcbe.dto.MessageDTO;
import com.tung.bcbe.model.Address;
import com.tung.bcbe.model.Contractor;
import com.tung.bcbe.model.Message;
import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.model.ProposalMessageFile;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.MessageRepository;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private ProposalMessageFileRepository proposalMessageFileRepository;

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

    @GetMapping("/project/{project_id}/conversationsummary")
    public PageImpl<ConversationDTO> getOwnerProjectMessagesList(@PathVariable(value = "project_id") UUID projectId,
                                                                 Pageable pageable) {
        List<ConversationDTO> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Contractor contractor = Contractor.builder()
                    .address(Address.builder().name("Contractor Name " + i).build())
                    .build();
            contractor.setId(UUID.fromString(i%2 == 0 ? "a2d67837-2d36-41a5-8066-6118a2cb2128" : "b579a3de-8e01-4668-b80c-7c1a40068f69"));
            MessageDTO msg = MessageDTO.builder().sender(contractor)
                    .id(UUID.randomUUID())
                    .timestamp(Instant.now().minus(5*i, ChronoUnit.MINUTES))
                    .status(i%2 == 0 ? MessageDTO.Status.UNREAD : MessageDTO.Status.READ)
                    .message("message message message message message message message message " + i).build();
            ConversationDTO conversationDTO = ConversationDTO.builder().id(UUID.randomUUID())
                    .latestMessage(msg)
                    .build();
            list.add(conversationDTO);
        }
        return new PageImpl<>(list, pageable, 20);
    }

    @GetMapping("/conversation/{conversation_id}")
    public PageImpl<MessageDTO> getConversation(@PathVariable(value = "conversation_id") UUID conversation_id,
                                                Pageable pageable) {
        List<MessageDTO> list = new ArrayList<>();
        Contractor p1 = Contractor.builder()
                .address(Address.builder().name("Contractor p1 Name").build())
                .build();
        p1.setId(UUID.fromString("b579a3de-8e01-4668-b80c-7c1a40068f69"));
        Contractor p2 = Contractor.builder()
                .address(Address.builder().name("Contractor p2 Name").build())
                .build();
        p2.setId(UUID.fromString("a2d67837-2d36-41a5-8066-6118a2cb2128"));
        Instant time = Instant.parse("2018-06-25T05:12:35Z");
        for (int i = 0; i < 20; i++) {
            MessageDTO msg = MessageDTO.builder().sender(i%2 == 0 ? p1 : p2)
                    .id(UUID.randomUUID())
                    .timestamp(time.plus(5*i, ChronoUnit.MINUTES))
                    .message("message message message message message message message message message " + i).build();
            list.add(msg);
        }

        return new PageImpl<>(list, pageable, 20);
    }
}
