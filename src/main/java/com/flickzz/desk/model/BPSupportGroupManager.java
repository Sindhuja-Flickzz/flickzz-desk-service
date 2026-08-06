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
@ToString(exclude = {"supportGroup","agent"})
public class BPSupportGroupManager {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpSupportGroupManagerSeq")
	@SequenceGenerator(name = "bpSupportGroupManagerSeq", sequenceName = "BP_SUPPORT_GROUP_MANAGER_SEQ", allocationSize = 1)
	@Column(name = "ID")
	private Long managerId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUPPORT_GROUP_ID", nullable = false)
	private BPSupportGroup supportGroup;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGENT_ID", nullable = false)
	private AgentMaster agent;
	
	@Builder.Default
	@Column(name = "IS_INTERNAL")
	private Boolean isInternal = false;
	
	@Builder.Default
	@Column(name = "IS_BP")
	private Boolean isBP = false;

	@Builder.Default
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;

	@Builder.Default
	@Column(name = "IS_UNDER_APPROVAL")
	private Boolean isUnderApproval = false;
}
