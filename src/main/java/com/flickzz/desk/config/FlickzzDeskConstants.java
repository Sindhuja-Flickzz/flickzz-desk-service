package com.flickzz.desk.config;

import java.util.*;

public class FlickzzDeskConstants {

	public static final String ROLE_ADMIN = "Admin";
	public static final String ROLE_AGENT = "Agent";
	public static final String ROLE_ADMIN_AGENT = "AdminAgent";
	public static final String ENTRY = "Entry";
	public static final String EXIT = "Exit";
	public static final String FD_USER = "User";
	public static final String LOGIN = "Login";
	public static final String CALENDAR = "Calender";
	public static final String PLANT = "Plant";
	public static final String SKILL = "Skill";
	public static final String AGENT = "Agent";
	public static final String NOTIFICATION = "NOTIFICATION";
	public static final String COMPANY = "Company";
	public static final String TICKET_TYPE = "Ticket Type";
	public static final String COMPANY_ROLE = "Company Role";
	public static final String COUNTRY = "Country";
	public static final String STATE = "State";
	public static final String CITY = "City";
	public static final String PRIORITY = "Priority";
	public static final String SLA = "SLA";
	public static final String CATEGORY = "Category";
	public static final String LANGUAGE = "Language";
	public static final String PROJECT = "Project";
	public static final String EPIC = "Epic";
	public static final String PROJECT_ID = "Project Id";
	public static final String TIME_ZONE = "Current time of timezone";
	public static final String BUSINESS_SERVICE = "Business Service";
	public static final String BUSINESS_PARTNER = "Business Partner";
	public static final String BUSINESS_PARTNER_CONFIG = "Business Partner Config";
	public static final String REQUEST_NUMBER = "Request Number";
	public static final String REQUEST_CONFIG = "Request Config";

	public static final Boolean ACTIVE = true;
	public static final Boolean INACTIVE = false;
	public static final Boolean UNDER_APPROVAL = Boolean.TRUE;
	public static final Boolean DEACTIVATE = Boolean.FALSE;
	public static final Integer INITIAL_VERSION = 1;

	public static final String CALENDAR_CODE = "Calender Code";
	public static final String CALENDAR_TYPE = "Calender Type";
	public static final String COMPANY_NAME = "Company name";
	public static final String PLANT_NAME = "Plant name";
	public static final String SKILL_NAME = "Skill name";
	public static final String WORK_ITEM = "Work item";
	public static final String FIELD_TYPE = "Field Type";
	public static final String AGENT_NAME = "Agent name";
	public static final String PROJECT_NAME = "Project name";
	public static final String PROGRESS_STATUS = "Progress status";
	public static final String CURRENCY = "Currency";
	public static final String MAIL_ID = "Mail Id";
	public static final String ACCESS_ID = "Access Id";
	public static final String PHONE = "Phone";

	public static final String USERNAME_OR_EMAIL = "Username or Email";
	public static final String USER_LIST = "User list";
	public static final String IMPACT = "Impact";
	public static final String IMPACT_CODE = "Impact Code";
	public static final String IMPACT_LEVEL = "Impact Level";
	public static final String EMAIL = "Email";
	public static final String PASSWORD = "Password";
	public static final String APPROVERS = "Approvers";
	public static final String CREATE = "CREATE";
	public static final String UPDATE = "UPDATE";
	public static final String DELETE = "DELETE";
	public static final String SUCCESS = "SUCCESS";
	public static final String FAILED = "FAILED";
	public static final String DRAFTED = "DRAFTED";

	public static final Boolean READ = Boolean.TRUE;
	public static final Boolean UNREAD = Boolean.FALSE;

	public static final String LEAD_COMPANY = "Lead company";
	public static final Map<String, String> WORK_ITEMS = Map.ofEntries(Map.entry("Incident", "INC"),
			Map.entry("Request Item", "RITM"), Map.entry("EPIC", "EPIC"), Map.entry("USER_STORY", "STORY"),
			Map.entry("TASK", "TASK"), Map.entry("SUB_TASK", "STSK"));
	public static final Map<String, Character> SLA_TERMS = new HashMap<String, Character>() {
		{
			put("days", 'D');
			put("hours", 'H');
			put("minutes", 'M');
		}
	};
}
