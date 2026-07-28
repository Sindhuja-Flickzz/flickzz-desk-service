package com.flickzz.desk.config;

import java.security.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.*;
import org.springframework.security.core.context.*;

import com.flickzz.desk.model.*;
import com.flickzz.desk.security.*;
import com.flickzz.desk.vo.*;

public class FlickzzDeskUtility {

	public static String generateLog(String methodName, String className) {
		if (methodName.equalsIgnoreCase(FlickzzDeskConstants.ENTRY))
			return "Entered " + methodName + " method in " + className;
		else
			return "Exit " + methodName + " method in " + className;
	}

	public static String getDescription(String template, Object... params) {
		return String.format(template, params);
	}

	public static CustomUserDetails getUserDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
		return user;
	}

	public static String generateTemporaryPassword() {
		SecureRandom secureRandom = new SecureRandom();
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
		StringBuilder password = new StringBuilder(12);
		for (int i = 0; i < 12; i++) {
			password.append(characters.charAt(secureRandom.nextInt(characters.length())));
		}
		return password.toString();
	}

	public static String generateUniversalId(String uidPrefix, String currentUID) {
		String yearSuffix = String.valueOf(java.time.Year.now().getValue()).substring(2);
		int sequence = 0;

		if (currentUID != null && currentUID.length() > 1) {
			String sequenceStr = currentUID.substring(uidPrefix.length() + yearSuffix.length());
			sequence = Integer.parseInt(sequenceStr);
		}
		int nextSequence = sequence + 1;
		return uidPrefix + yearSuffix + nextSequence;
	}

	public static RegisterLoginResponseVO generateLoginResponse(String jwtToken, String refreshToken,
			Boolean isEnquiryUser, Boolean mfaEnabled, CompanyMaster company, String userRole, String qrCodeImageUri,
			Long userId) {
		return RegisterLoginResponseVO.builder().accessToken(jwtToken != null ? jwtToken : "")
				.refreshToken(refreshToken != null ? refreshToken : "").isEnquiryUser(isEnquiryUser)
				.mfaEnabled(mfaEnabled).userOrgId(company != null ? company.getCompanyId() : 0)
				.userOrgName(company != null ? company.getCompanyName() : "").userRole(userRole != null ? userRole : "")
				.secretImageUri(qrCodeImageUri).userId(userId).build();
	}

	public static Map<String, Object> buildPrioritySnapshot(BPPriority p) {
		Map<String, Object> snapshot = new HashMap<>();
		if (p == null) {
			return snapshot;
		}
		try {
			snapshot.put("priorityId", p.getPriorityId());
			snapshot.put("level", p.getLevel());
			snapshot.put("code", p.getCode());
			snapshot.put("description", p.getDescription());
			snapshot.put("isActive", p.getIsActive());
			snapshot.put("createdBy", p.getCreatedBy());
			snapshot.put("updatedBy", p.getUpdatedBy());
			snapshot.put("createdAt", p.getCreatedAt());
			snapshot.put("updatedAt", p.getUpdatedAt());
			try {
				if (p.getConfiguration() != null) {
					snapshot.put("configurationId", p.getConfiguration().getConfigurationId());
					if (p.getConfiguration().getBusinessPartner() != null && p.getConfiguration().getBusinessPartner().getCompany() != null) {
						snapshot.put("companyId", p.getConfiguration().getBusinessPartner().getCompany().getCompanyId());
					}
				}
			} catch (Exception ignore) {
			}
			try {
				if (p.getTicketType() != null) {
					snapshot.put("ticketTypeId", p.getTicketType().getTicketTypeId());
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return snapshot;
	}

	public static Map<String, Object> buildSlaSnapshot(BPSla s) {
		Map<String, Object> snapshot = new HashMap<>();
		if (s == null) {
			return snapshot;
		}
		try {
			snapshot.put("slaId", s.getSlaId());
			snapshot.put("firstResponseTime", s.getFirstResponseTime());
			snapshot.put("firstResponseTerm", s.getFirstResponseTerm());
			snapshot.put("resolutionTime", s.getResolutionTime());
			snapshot.put("resolutionTerm", s.getResolutionTerm());
			snapshot.put("updateFrequency", s.getUpdateFrequency());
			snapshot.put("updateFrequencyTerm", s.getUpdateFrequencyTerm());
			snapshot.put("isActive", s.getIsActive());
			snapshot.put("createdBy", s.getCreatedBy());
			snapshot.put("updatedBy", s.getUpdatedBy());
			snapshot.put("createdAt", s.getCreatedAt());
			snapshot.put("updatedAt", s.getUpdatedAt());
			try {
				if (s.getConfiguration() != null) {
					snapshot.put("configurationId", s.getConfiguration().getConfigurationId());
					if (s.getConfiguration().getBusinessPartner() != null && s.getConfiguration().getBusinessPartner().getCompany() != null) {
						snapshot.put("companyId", s.getConfiguration().getBusinessPartner().getCompany().getCompanyId());
					}
				}
			} catch (Exception ignore) {
			}
			try {
				if (s.getPriority() != null) {
					snapshot.put("priorityId", s.getPriority().getPriorityId());
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return snapshot;
	}

	public static Map<String, Object> buildCategorySnapshot(BPCategory c) {
		Map<String, Object> snapshot = new HashMap<>();
		if (c == null) {
			return snapshot;
		}
		try {
			snapshot.put("categoryId", c.getCategoryId());
			snapshot.put("categoryName", c.getCategoryName());
			List<String> subCategories = c.getSubCategories().stream().map(BPSubCategory::getSubCategoryName).toList();
			snapshot.put("subCategories", subCategories);
			snapshot.put("isActive", c.getIsActive());
			snapshot.put("createdBy", c.getCreatedBy());
			snapshot.put("updatedBy", c.getUpdatedBy());
			snapshot.put("createdAt", c.getCreatedAt());
			snapshot.put("updatedAt", c.getUpdatedAt());
			try {
				if (c.getConfiguration() != null) {
					snapshot.put("configurationId", c.getConfiguration().getConfigurationId());
					if (c.getConfiguration().getBusinessPartner() != null && c.getConfiguration().getBusinessPartner().getCompany() != null) {
						snapshot.put("companyId", c.getConfiguration().getBusinessPartner().getCompany().getCompanyId());
					}
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return snapshot;
	}

	public static Map<String, Object> buildSupportGroupSnapshot(BPSupportGroup supportGroup, Set<Long> agentIds, Set<Long> internalManagerIds, Set<Long> bpManagerIds) {
		Map<String, Object> snapshot = new HashMap<>();
		if (supportGroup == null) {
			return snapshot;
		}
		try {
			snapshot.put("supportGroupId", supportGroup.getSupportGroupId());
			snapshot.put("groupName", supportGroup.getGroupName());
			snapshot.put("members", agentIds);
			snapshot.put("internalManagers", internalManagerIds);
			snapshot.put("bpManagers", bpManagerIds);
			snapshot.put("isActive", supportGroup.getIsActive());
			snapshot.put("createdBy", supportGroup.getCreatedBy());
			snapshot.put("updatedBy", supportGroup.getUpdatedBy());
			snapshot.put("createdAt", supportGroup.getCreatedAt());
			snapshot.put("updatedAt", supportGroup.getUpdatedAt());
			try {
				if (supportGroup.getConfiguration() != null) {
					snapshot.put("configurationId", supportGroup.getConfiguration().getConfigurationId());
					if (supportGroup.getConfiguration().getBusinessPartner() != null && supportGroup.getConfiguration().getBusinessPartner().getCompany() != null) {
						snapshot.put("companyId", supportGroup.getConfiguration().getBusinessPartner().getCompany().getCompanyId());
					}
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return snapshot;
	}

	public static Map<String, Object> buildAssignmentSnapshot(BPAssignment a) {
		Map<String, Object> snapshot = new HashMap<>();
		if (a == null) {
			return snapshot;
		}
		try {
			snapshot.put("assignmentId", a.getAssignmentId());
			snapshot.put("isActive", a.getIsActive());
			snapshot.put("createdBy", a.getCreatedBy());
			snapshot.put("updatedBy", a.getUpdatedBy());
			snapshot.put("createdAt", a.getCreatedAt());
			snapshot.put("updatedAt", a.getUpdatedAt());
			try {
				if (a.getConfiguration() != null) {
					snapshot.put("configurationId", a.getConfiguration().getConfigurationId());
					if (a.getConfiguration().getBusinessPartner() != null && a.getConfiguration().getBusinessPartner().getCompany() != null) {
						snapshot.put("companyId", a.getConfiguration().getBusinessPartner().getCompany().getCompanyId());
					}
				}
			} catch (Exception ignore) {
			}
			try {
				if (a.getSupportGroup() != null) {
					snapshot.put("supportGroupId", a.getSupportGroup().getSupportGroupId());
				}
			} catch (Exception ignore) {
			}
			try {
				if (a.getSubCategory() != null) {
					snapshot.put("subCategoryId", a.getSubCategory().getSubCategoryId());
				}
			} catch (Exception ignore) {
			}
		} catch (Exception ignore) {
		}
		return snapshot;
	}

}
