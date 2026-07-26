package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_SUPPORT_GROUP_MANAGER", uniqueConstraints = {
		@UniqueConstraint(name = "UK_GROUP_MANAGER", columnNames = { "SUPPORT_GROUP_ID", "AGENT_ID" }) })
public class BPSupportGroupManager {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpSupportGroupManagerSeq")
	@SequenceGenerator(name = "bpSupportGroupManagerSeq", sequenceName = "BP_SUPPORT_GROUP_MANAGER_SEQ", allocationSize = 1)
	@Column(name = "MANAGER_ID")
	private Long managerId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPORT_GROUP_ID", nullable = false)
	private BPSupportGroup supportGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENT_ID", nullable = false)
	private AgentMaster agent;
	
	@Builder.Default
	@Column(name = "IS_GROUP_LEAD")
	private Boolean isGroupLead = false;
	
	@Builder.Default
	@Column(name = "IS_GROUP_MANAGER")
	private Boolean isGroupManager = false;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;
}
