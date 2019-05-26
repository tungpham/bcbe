package com.tung.bcbe.dto;

import com.tung.bcbe.model.Proposal;
import com.tung.bcbe.model.ProposalOption;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ProposalTemCatOptionDetail implements Serializable {
    
    Proposal proposal;
    List<Map<UUID, List<Map<UUID, List<ProposalOption>>>>> temCatOptionDetail = new ArrayList<>();
}
