-- Required DDL indexes derived from create_query.sql
-- Covers FK columns, status/active flags, and common lookup/query fields.

CREATE INDEX IF NOT EXISTS idx_fd_country_master_active
    ON FD_COUNTRY_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_state_master_country_id
    ON FD_STATE_MASTER (COUNTRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_state_master_active
    ON FD_STATE_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_city_master_state_id
    ON FD_CITY_MASTER (STATE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_city_master_country_id
    ON FD_CITY_MASTER (COUNTRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_city_master_active
    ON FD_CITY_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_language_master_active
    ON FD_LANGUAGE_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_user_country_id
    ON FD_USER (COUNTRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_city_id
    ON FD_USER (CITY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_language_id
    ON FD_USER (LANGUAGE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_role
    ON FD_USER (ROLE);

CREATE INDEX IF NOT EXISTS idx_fd_user_active
    ON FD_USER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_company_master_country_id
    ON FD_COMPANY_MASTER (COUNTRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_company_master_state_id
    ON FD_COMPANY_MASTER (STATE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_company_master_city_id
    ON FD_COMPANY_MASTER (CITY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_company_master_active
    ON FD_COMPANY_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_company_approver_companyid
    ON FD_COMPANY_APPROVER (COMPANYID);

CREATE INDEX IF NOT EXISTS idx_fd_company_approver_agent_id
    ON FD_COMPANY_APPROVER (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_business_partner_company_id
    ON FD_BUSINESS_PARTNER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_business_partner_mapping_id
    ON FD_BUSINESS_PARTNER (MAPPING_ID);

CREATE INDEX IF NOT EXISTS idx_fd_business_partner_active
    ON FD_BUSINESS_PARTNER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_registration_company_id
    ON FD_ENQUIRY_REGISTRATION (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_registration_country_id
    ON FD_ENQUIRY_REGISTRATION (COUNTRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_registration_state_id
    ON FD_ENQUIRY_REGISTRATION (STATE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_registration_city_id
    ON FD_ENQUIRY_REGISTRATION (CITY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_registration_active
    ON FD_ENQUIRY_REGISTRATION (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_info_enquiry_id
    ON FD_ENQUIRY_INFO (ENQUIRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_enquiry_info_is_used
    ON FD_ENQUIRY_INFO (IS_USED);

CREATE INDEX IF NOT EXISTS idx_fd_auth_user_id
    ON FD_AUTH (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_auth_enquiry_id
    ON FD_AUTH (ENQUIRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_auth_active
    ON FD_AUTH (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_auth_expires_at
    ON FD_AUTH (EXPIRES_AT);

CREATE INDEX IF NOT EXISTS idx_fd_login_master_user_id
    ON FD_LOGIN_MASTER (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_login_master_auth_id
    ON FD_LOGIN_MASTER (AUTH_ID);

CREATE INDEX IF NOT EXISTS idx_fd_login_master_role
    ON FD_LOGIN_MASTER (ROLE);

CREATE INDEX IF NOT EXISTS idx_fd_login_master_active
    ON FD_LOGIN_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_type_company_id
    ON FD_CALENDAR_TYPE (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_type_active
    ON FD_CALENDAR_TYPE (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_master_type_id
    ON FD_CALENDAR_MASTER (CALENDAR_TYPE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_master_company_id
    ON FD_CALENDAR_MASTER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_master_active
    ON FD_CALENDAR_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_holiday_calendar_id
    ON FD_CALENDAR_HOLIDAY (CALENDAR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_holiday_date
    ON FD_CALENDAR_HOLIDAY (HOLIDAY_DATE);

CREATE INDEX IF NOT EXISTS idx_fd_calendar_workdays_calendar_id
    ON FD_CALENDAR_WORKDAYS (CALENDAR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_plant_master_country_id
    ON FD_PLANT_MASTER (COUNTRY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_plant_master_calendar_id
    ON FD_PLANT_MASTER (CALENDAR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_plant_master_company_id
    ON FD_PLANT_MASTER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_plant_master_active
    ON FD_PLANT_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_plant_weekoff_plant_id
    ON FD_PLANT_WEEKOFF (PLANT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_plant_mapping_agent_id
    ON FD_AGENT_PLANT_MAPPING (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_plant_mapping_plant_id
    ON FD_AGENT_PLANT_MAPPING (PLANT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_plant_mapping_active
    ON FD_AGENT_PLANT_MAPPING (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_skill_master_company_id
    ON FD_SKILL_MASTER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_skill_master_active
    ON FD_SKILL_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_agent_master_user_id
    ON FD_AGENT_MASTER (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_master_company_id
    ON FD_AGENT_MASTER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_master_calendar_id
    ON FD_AGENT_MASTER (CALENDAR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_master_active
    ON FD_AGENT_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_agent_skills_mapping_agent_id
    ON FD_AGENT_SKILLS_MAPPING (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_skills_mapping_skill_id
    ON FD_AGENT_SKILLS_MAPPING (SKILL_ID);

CREATE INDEX IF NOT EXISTS idx_fd_agent_skills_mapping_active
    ON FD_AGENT_SKILLS_MAPPING (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_business_service_company_id
    ON FD_BUSINESS_SERVICE (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_business_service_active
    ON FD_BUSINESS_SERVICE (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_service_offering_service_id
    ON FD_SERVICE_OFFERING (SERVICE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_service_offering_active
    ON FD_SERVICE_OFFERING (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_request_config_company_id
    ON FD_REQUEST_CONFIG (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_request_config_request_type
    ON FD_REQUEST_CONFIG (REQUEST_TYPE);

CREATE INDEX IF NOT EXISTS idx_fd_request_config_active
    ON FD_REQUEST_CONFIG (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_request_config_is_enabled
    ON FD_REQUEST_CONFIG (IS_ENABLED);

CREATE INDEX IF NOT EXISTS idx_fd_impact_master_company_id
    ON FD_IMPACT_MASTER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_impact_master_impact_level
    ON FD_IMPACT_MASTER (IMPACT_LEVEL);

CREATE INDEX IF NOT EXISTS idx_fd_impact_master_active
    ON FD_IMPACT_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_opened_by
    ON FD_TICKET (OPENED_BY);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_requested_for
    ON FD_TICKET (REQUESTED_FOR);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_business_service_id
    ON FD_TICKET (BUSINESS_SERVICE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_service_offering_id
    ON FD_TICKET (SERVICE_OFFERING_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_impact_id
    ON FD_TICKET (IMPACT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_company_id
    ON FD_TICKET (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_request_type
    ON FD_TICKET (REQUEST_TYPE);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_active
    ON FD_TICKET (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_request_item_ticket_id
    ON FD_REQUEST_ITEM (TICKET_ID);

CREATE INDEX IF NOT EXISTS idx_fd_incident_ticket_id
    ON FD_INCIDENT (TICKET_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_watchlist_ticket_id
    ON FD_TICKET_WATCHLIST (TICKET_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_watchlist_user_id
    ON FD_TICKET_WATCHLIST (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_project_company_id
    ON FD_PROJECT (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_project_active
    ON FD_PROJECT (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_progress_status_company_id
    ON FD_PROGRESS_STATUS (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_progress_status_sequence
    ON FD_PROGRESS_STATUS (PROGRESS_SEQUENCE);

CREATE INDEX IF NOT EXISTS idx_fd_epic_project_id
    ON FD_EPIC (PROJECT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_epic_progress_id
    ON FD_EPIC (PROGRESS_ID);

CREATE INDEX IF NOT EXISTS idx_fd_epic_active
    ON FD_EPIC (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_user_story_epic_id
    ON FD_USER_STORY (EPIC_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_story_predecessor_id
    ON FD_USER_STORY (PREDECESSOR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_story_agent_id
    ON FD_USER_STORY (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_story_progress_id
    ON FD_USER_STORY (PROGRESS_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_story_active
    ON FD_USER_STORY (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_task_story_id
    ON FD_TASK (STORY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_task_agent_id
    ON FD_TASK (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_task_progress_id
    ON FD_TASK (PROGRESS_ID);

CREATE INDEX IF NOT EXISTS idx_fd_task_active
    ON FD_TASK (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_sub_task_task_id
    ON FD_SUB_TASK (TASK_ID);

CREATE INDEX IF NOT EXISTS idx_fd_sub_task_progress_id
    ON FD_SUB_TASK (PROGRESS_ID);

CREATE INDEX IF NOT EXISTS idx_fd_sub_task_agent_id
    ON FD_SUB_TASK (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_sub_task_active
    ON FD_SUB_TASK (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_project_lead_assignment_company_id
    ON FD_PROJECT_LEAD_ASSIGNMENT (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_project_lead_assignment_story_id
    ON FD_PROJECT_LEAD_ASSIGNMENT (STORY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_project_lead_assignment_active
    ON FD_PROJECT_LEAD_ASSIGNMENT (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_template_details_company_id
    ON FD_TEMPLATE_DETAILS (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_template_details_work_item_id
    ON FD_TEMPLATE_DETAILS (WORK_ITEM_ID);

CREATE INDEX IF NOT EXISTS idx_fd_template_details_active
    ON FD_TEMPLATE_DETAILS (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_template_detail_field_template_id
    ON FD_TEMPLATE_DETAIL_FIELD (TEMPLATE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_template_detail_field_field_type_id
    ON FD_TEMPLATE_DETAIL_FIELD (FIELD_TYPE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_template_detail_field_active
    ON FD_TEMPLATE_DETAIL_FIELD (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_template_field_option_field_id
    ON FD_TEMPLATE_FIELD_OPTION (FIELD_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ticket_type_master_active
    ON FD_TICKET_TYPE_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_configuration_business_partner_id
    ON FD_BP_CONFIGURATION (BUSINESS_PARTNER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_configuration_active
    ON FD_BP_CONFIGURATION (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_priority_configuration_id
    ON FD_BP_PRIORITY (CONFIGURATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_priority_ticket_type_id
    ON FD_BP_PRIORITY (TICKET_TYPE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_priority_active
    ON FD_BP_PRIORITY (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_sla_configuration_id
    ON FD_BP_SLA (CONFIGURATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_sla_priority_id
    ON FD_BP_SLA (PRIORITY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_sla_active
    ON FD_BP_SLA (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_category_configuration_id
    ON FD_BP_CATEGORY (CONFIGURATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_category_active
    ON FD_BP_CATEGORY (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_sub_category_category_id
    ON FD_BP_SUB_CATEGORY (CATEGORY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_sub_category_active
    ON FD_BP_SUB_CATEGORY (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_configuration_id
    ON FD_BP_SUPPORT_GROUP (CONFIGURATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_active
    ON FD_BP_SUPPORT_GROUP (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_member_group_id
    ON FD_BP_SUPPORT_GROUP_MEMBER (SUPPORT_GROUP_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_member_agent_id
    ON FD_BP_SUPPORT_GROUP_MEMBER (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_member_active
    ON FD_BP_SUPPORT_GROUP_MEMBER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_manager_group_id
    ON FD_BP_SUPPORT_GROUP_MANAGER (SUPPORT_GROUP_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_manager_agent_id
    ON FD_BP_SUPPORT_GROUP_MANAGER (AGENT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_support_group_manager_active
    ON FD_BP_SUPPORT_GROUP_MANAGER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_bp_assignment_configuration_id
    ON FD_BP_ASSIGNMENT (CONFIGURATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_assignment_sub_category_id
    ON FD_BP_ASSIGNMENT (SUB_CATEGORY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_assignment_support_group_id
    ON FD_BP_ASSIGNMENT (SUPPORT_GROUP_ID);

CREATE INDEX IF NOT EXISTS idx_fd_bp_assignment_active
    ON FD_BP_ASSIGNMENT (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_system_audit_user_id
    ON FD_SYSTEM_AUDIT (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_system_audit_company_id
    ON FD_SYSTEM_AUDIT (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_system_audit_entity_id
    ON FD_SYSTEM_AUDIT (ENTITY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_system_audit_entity_name
    ON FD_SYSTEM_AUDIT (ENTITY_NAME);

CREATE INDEX IF NOT EXISTS idx_fd_system_audit_action
    ON FD_SYSTEM_AUDIT (ACTION);

CREATE INDEX IF NOT EXISTS idx_fd_system_audit_created_at
    ON FD_SYSTEM_AUDIT (CREATED_AT);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_configuration_id
    ON FD_CONFIG_CHANGE_REQUEST (CONFIGURATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_requested_by_org
    ON FD_CONFIG_CHANGE_REQUEST (REQUESTED_BY_ORG);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_approval_org
    ON FD_CONFIG_CHANGE_REQUEST (APPROVAL_ORG);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_requested_by_user_id
    ON FD_CONFIG_CHANGE_REQUEST (REQUESTED_BY_USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_status
    ON FD_CONFIG_CHANGE_REQUEST (STATUS);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_requested_on
    ON FD_CONFIG_CHANGE_REQUEST (REQUESTED_ON);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_approval_ccr_id
    ON FD_CONFIG_CHANGE_APPROVAL (CCR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_approval_user_id
    ON FD_CONFIG_CHANGE_APPROVAL (APPROVER_USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_approval_org_id
    ON FD_CONFIG_CHANGE_APPROVAL (APPROVER_ORG_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_approval_remark_id
    ON FD_CONFIG_CHANGE_APPROVAL (REMARK_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_approval_status
    ON FD_CONFIG_CHANGE_APPROVAL (STATUS);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_remark_ccr_id
    ON FD_CONFIG_CHANGE_REQUEST_REMARK (CCR_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_remark_approval_id
    ON FD_CONFIG_CHANGE_REQUEST_REMARK (APPROVAL_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_remark_user_id
    ON FD_CONFIG_CHANGE_REQUEST_REMARK (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_request_remark_org_id
    ON FD_CONFIG_CHANGE_REQUEST_REMARK (ORGANIZATION_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_notification_change_request_id
    ON FD_CONFIG_CHANGE_NOTIFICATION (CHANGE_REQUEST_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_notification_recipient_user_id
    ON FD_CONFIG_CHANGE_NOTIFICATION (RECIPIENT_USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_notification_recipient_org_id
    ON FD_CONFIG_CHANGE_NOTIFICATION (RECIPIENT_ORG_ID);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_notification_is_read
    ON FD_CONFIG_CHANGE_NOTIFICATION (IS_READ);

CREATE INDEX IF NOT EXISTS idx_fd_config_change_notification_created_on
    ON FD_CONFIG_CHANGE_NOTIFICATION (CREATED_ON);

CREATE INDEX IF NOT EXISTS idx_fd_user_language_mapping_user_id
    ON FD_USER_LANGUAGE_MAPPING (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_language_mapping_language_id
    ON FD_USER_LANGUAGE_MAPPING (LANGUAGE_ID);

CREATE INDEX IF NOT EXISTS idx_fd_user_language_mapping_active
    ON FD_USER_LANGUAGE_MAPPING (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_company_id
    ON FD_RITM_MASTER (COMPANY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_requested_by
    ON FD_RITM_MASTER (REQUESTED_BY);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_requested_for
    ON FD_RITM_MASTER (REQUESTED_FOR);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_category_id
    ON FD_RITM_MASTER (CATEGORY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_sub_category_id
    ON FD_RITM_MASTER (SUB_CATEGORY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_support_group_id
    ON FD_RITM_MASTER (SUPPORT_GROUP_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_priority_id
    ON FD_RITM_MASTER (PRIORITY_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_assigned_to
    ON FD_RITM_MASTER (ASSIGNED_TO);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_status
    ON FD_RITM_MASTER (STATUS);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_active
    ON FD_RITM_MASTER (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_master_created_at
    ON FD_RITM_MASTER (CREATED_AT);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_attachment_ritm_id
    ON FD_RITM_ATTACHMENT (RITM_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_attachment_uploaded_by
    ON FD_RITM_ATTACHMENT (UPLOADED_BY);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_attachment_active
    ON FD_RITM_ATTACHMENT (IS_ACTIVE);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_watchlist_ritm_id
    ON FD_RITM_WATCHLIST (RITM_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_watchlist_user_id
    ON FD_RITM_WATCHLIST (USER_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_audit_ritm_id
    ON FD_RITM_AUDIT (RITM_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_audit_changed_by
    ON FD_RITM_AUDIT (CHANGED_BY);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_audit_changed_at
    ON FD_RITM_AUDIT (CHANGED_AT);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_audit_detail_audit_id
    ON FD_RITM_AUDIT_DETAIL (AUDIT_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_comment_ritm_id
    ON FD_RITM_COMMENT (RITM_ID);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_comment_created_by
    ON FD_RITM_COMMENT (CREATED_BY);

CREATE INDEX IF NOT EXISTS idx_fd_ritm_comment_active
    ON FD_RITM_COMMENT (IS_ACTIVE);