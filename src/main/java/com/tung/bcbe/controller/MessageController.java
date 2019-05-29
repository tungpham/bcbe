package com.tung.bcbe.controller;

import com.tung.bcbe.model.Message;
import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.repository.ContractorRepository;
import com.tung.bcbe.repository.MessageRepository;
import com.tung.bcbe.repository.ProposalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
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

    /**
     * General contractor send message to subcontractor proposal. Get subcontractor info from proposal.
     * @param propId
     * @param fromId
     * @param message
     * @return
     */
    @PostMapping("/proposals/{prop_id}/tosubcon")
    public Message sendMsgToProposal(@PathVariable(name = "prop_id") UUID propId, @RequestBody @Valid Message message) {
        return proposalRepository.findById(propId).map(proposal -> {
            message.setFrom(proposal.getProject().getGenContractor());
            message.setTo(proposal.getSubContractor());
            message.setProposal(proposal);
            return messageRepository.save(message);
        }).orElseThrow(Util.notFound(propId, Proposal.class));
    }
    
    /*
    Get all msg for a proposal
     */
    @GetMapping("/proposals/{prop_id}")
    public Page<Message> getMsgForProposal(@PathVariable(name = "prop_id") UUID propId, Pageable pagable) {
        return messageRepository.findByProposalId(propId, pagable);   
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
            return messageRepository.save(message);
        }).orElseThrow(Util.notFound(propId, Proposal.class));
    }
}
