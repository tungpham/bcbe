package com.tung.bcbe.controller;

import com.amazonaws.services.s3.AmazonS3;
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
import org.springframework.data.domain.Pageable;
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
}
