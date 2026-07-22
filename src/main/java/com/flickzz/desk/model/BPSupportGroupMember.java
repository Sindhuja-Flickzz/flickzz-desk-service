package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FD_BP_SUPPORT_GROUP_MEMBER", uniqueConstraints = {
		@UniqueConstraint(name = "UK_GROUP_MEMBER", columnNames = { "SUPPORT_GROUP_ID", "AGENT_ID" }) })
public class BPSupportGroupMember {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bpSupportGroupMemberSeq")
	@SequenceGenerator(name = "bpSupportGroupMemberSeq", sequenceName = "BP_SUPPORT_GROUP_MEMBER_SEQ", allocationSize = 1)
	@Column(name = "MEMBER_ID")
	private Long memberId;

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
	@Column(name = "IS_ACTIVE")
	private Boolean isActive = true;
}