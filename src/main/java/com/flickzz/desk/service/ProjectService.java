package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskConstants.*;
import static com.flickzz.desk.config.FlickzzDeskUtility.*;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.*;

import java.util.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.flickzz.desk.exception.*;
import com.flickzz.desk.mapper.*;
import com.flickzz.desk.model.*;
import com.flickzz.desk.repo.*;
import com.flickzz.desk.vo.*;

@Service
public class ProjectService {

	private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private CompanyMasterRepository companyMasterRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private CommonMapper mapper;

	@Autowired
	private UserStoryRepository userStoryRepository;

	@Autowired
	private ProgressStatusRepository progressStatusRepository;

	@Transactional
	public ProjectVO createProject(ProjectRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getProjectName() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), PROJECT_NAME));
			}

			if (request == null || request.getOrgId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), COMPANY));
			}

			if (request.getEpics() == null || request.getEpics().isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), "Epics"));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findByCompanyIdAndIsActive(request.getOrgId(),
					ACTIVE);
			if (company.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			Optional<Project> existingProject = projectRepository.findByProjectNameAndCompanyCompanyIdAndIsActive(
					request.getProjectName(), request.getOrgId(), ACTIVE);

			if (existingProject.isPresent()) {
				throw new FlickzzDeskException(ALREADY_EXISTS,
						getDescription(ALREADY_EXISTS.getDescription(), PROJECT_NAME));
			}

			Project project = mapper.toProject(request, company.get());

			// Fetch the progress status with least sequence
			List<ProgressStatus> progressStatuses = progressStatusRepository
					.findByCompanyCompanyIdAndIsActiveOrderByProgressSequenceAsc(request.getOrgId(), ACTIVE);
			if (progressStatuses.isEmpty()) {
				throw new FlickzzDeskException(SET_TEXT, getDescription(SET_TEXT.getDescription(), PROGRESS_STATUS));
			}

			ProgressStatus defaultProgressStatus = progressStatuses.get(0);
			int maxProgressStatus = progressStatusRepository.findMaxProgressStatus(request.getOrgId());

			List<Epic> epics = new ArrayList<>();
			Map<String, UserStory> storyMap = new HashMap<>();

			for (EpicVO epicVO : request.getEpics()) {
				Epic epic = mapper.toEpic(epicVO, project, defaultProgressStatus, maxProgressStatus,
						request.getCreatedBy());

				List<UserStory> userStories = new ArrayList<>();
				int storySeq = 1;
				for (UserStoryVO usVO : epicVO.getUserStories()) {
					if (storySeq == 1) {
						project.setPlannedStartDate(usVO.getPlannedStartDate());
					}
					UserStory userStory = UserStory.builder().description(usVO.getTitle()).title(usVO.getTitle())
							.storyCode(usVO.getMappingStoryId()).storySequence(storySeq++)
							.progressStatus(defaultProgressStatus).maxProgressStatus(maxProgressStatus)
							.plannedStartDate(usVO.getPlannedStartDate()).plannedEndDate(usVO.getPlannedEndDate())
							.epic(epic).createdBy(request.getCreatedBy()).build();

					List<ProjectLeadAssignment> leads = new ArrayList<>();
					for (ProjectLeadAssignmentVO leadVO : usVO.getLeads()) {
						Long companyId = leadVO.getCompanyId(); // Assuming CompanyMasterVO has
																// getCompanyId()
						Optional<CompanyMaster> leadCompany = companyMasterRepository.findById(companyId);
						if (!leadCompany.isPresent()) {
							throw new FlickzzDeskException(DOES_NOT_EXIST,
									getDescription(DOES_NOT_EXIST.getDescription(), LEAD_COMPANY) + companyId);
						}
						ProjectLeadAssignment pla = ProjectLeadAssignment.builder().company(leadCompany.get())
								.userStory(userStory).createdBy(request.getCreatedBy()).build();
						leads.add(pla);
					}
					userStory.setProjectLeadAssignments(leads);

					List<Task> tasks = new ArrayList<>();
					if (usVO.getTasks() != null && !usVO.getTasks().isEmpty()) {
						int taskSeq = 1;
						for (TaskVO taskVO : usVO.getTasks()) {
							Task task = Task.builder().title(taskVO.getTitle()).description(taskVO.getDescription())
									.taskSequence(
											taskVO.getTaskSequence() != null ? taskVO.getTaskSequence() : taskSeq++)
									.progressStatus(defaultProgressStatus).maxProgressStatus(maxProgressStatus)
									.plannedStartDate(taskVO.getPlannedStartDate())
									.plannedEndDate(taskVO.getPlannedEndDate()).userStory(userStory)
									.createdBy(request.getCreatedBy()).build();
							tasks.add(task);
						}
					}
					userStory.setTasks(tasks);
					userStories.add(userStory);
					storyMap.put(usVO.getMappingStoryId(), userStory);
					project.setPlannedEndDate(usVO.getPlannedEndDate());
				}
				epic.setUserStories(userStories);
				epics.add(epic);
			}
			project.setEpics(epics);

			// Wire predecessors before persisting so the self-reference is not transient
			for (EpicVO epicVO : request.getEpics()) {
				for (UserStoryVO usVO : epicVO.getUserStories()) {
					if (usVO != null && usVO.getMappingPredecessorId() != null
							&& !usVO.getMappingPredecessorId().equals(0L)) {
						UserStory userStory = storyMap.get(usVO.getMappingStoryId());
						UserStory predecessor = storyMap.get(usVO.getMappingPredecessorId().toString());
						if (userStory != null && predecessor != null) {
							userStory.setPredecessor(predecessor);
						}
					}
				}
			}

			// Save project with all nested entities
			project = projectRepository.save(project);

			// Return ProjectVO
			return mapper.toProjectVO(project);

		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createProject method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	private UserStoryVO findUserStoryVO(ProjectRequestVO request, String storyId) {
		for (EpicVO epic : request.getEpics()) {
			for (UserStoryVO us : epic.getUserStories()) {
				if ((!us.getMappingStoryId().equalsIgnoreCase("0")) && storyId.equals(us.getMappingStoryId())) {
					return us;
				}
			}
		}
		return null;
	}

	public List<ProjectVO> getProjectList(String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {

			return projectRepository.findByCompanyCompanyIdAndIsActive(orgId, ACTIVE).stream()
					.map(project -> mapper.toNoBackRefProjectVO(project)).toList();
		} catch (Exception e) {
			log.error("Exception in getProjectList method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		} finally {
			log.debug(generateLog(EXIT, this.getClass().getName()));
		}
	}

	public ProjectVO getProjectInfo(String projectId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Optional<Project> projectOpt = projectRepository.findById(Long.valueOf(projectId));
			if (!projectOpt.isPresent()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PROJECT));
			}
			return mapper.toNoBackRefProjectVO(projectOpt.get());
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getProjectInfo method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		} finally {
			log.debug(generateLog(EXIT, this.getClass().getName()));
		}
	}

	public List<ProgressStatusVO> getProgressStatusList(String orgId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			return progressStatusRepository
					.findByCompanyCompanyIdAndIsActiveOrderByProgressSequenceAsc(Long.valueOf(orgId), ACTIVE).stream()
					.map(progressStatus -> mapper.toProgressStatusVO(progressStatus)).toList();
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in getProjectInfo method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		} finally {
			log.debug(generateLog(EXIT, this.getClass().getName()));
		}
	}

	public void deleteProject(String projectId) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			Project project = projectRepository.findById(Long.valueOf(projectId))
					.orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
							getDescription(DOES_NOT_EXIST.getDescription(), PROJECT)));
			project.setIsActive(INACTIVE);
			projectRepository.save(project);
		} catch (Exception e) {
			log.error("Exception in deleteProject method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		} finally {
			log.debug(generateLog(EXIT, this.getClass().getName()));
		}
	}

	public ProjectVO updateProject(ProjectRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getProjectId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD,
						getDescription(INVALID_FIELD.getDescription(), PROJECT_ID));
			}

			Optional<Project> existingProjectOpt = projectRepository.findById(request.getProjectId());
			if (existingProjectOpt.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), PROJECT));
			}
			Project project = existingProjectOpt.get();
			if (request == null || request.getOrgId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), COMPANY));
			}

			if (request.getEpics() == null || request.getEpics().isEmpty()) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), "Epics"));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findByCompanyIdAndIsActive(request.getOrgId(),
					ACTIVE);
			if (company.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			List<ProgressStatus> progressStatuses = progressStatusRepository
					.findByCompanyCompanyIdAndIsActiveOrderByProgressSequenceAsc(request.getOrgId(), ACTIVE);
			if (progressStatuses.isEmpty()) {
				throw new FlickzzDeskException(SET_TEXT, getDescription(SET_TEXT.getDescription(), PROGRESS_STATUS));
			}

			ProgressStatus defaultProgressStatus = progressStatuses.get(0);
			int maxProgressStatus = progressStatusRepository.findMaxProgressStatus(request.getOrgId());

			List<Epic> epics = new ArrayList<>();
			Map<String, UserStory> storyMap = new HashMap<>();

			project.getEpics().clear();
			for (EpicVO epicVO : request.getEpics()) {
				Epic epic = mapper.toEpic(epicVO, project, defaultProgressStatus, maxProgressStatus,
						request.getCreatedBy());

				List<UserStory> userStories = new ArrayList<>();
				int storySeq = 1;
				for (UserStoryVO usVO : epicVO.getUserStories()) {
					if (storySeq == 1) {
						project.setPlannedStartDate(usVO.getPlannedStartDate());
					}
					UserStory userStory = UserStory.builder().description(usVO.getTitle()).title(usVO.getTitle())
							.storyCode(usVO.getMappingStoryId()).storySequence(storySeq++)
							.progressStatus(defaultProgressStatus).maxProgressStatus(maxProgressStatus)
							.plannedStartDate(usVO.getPlannedStartDate()).plannedEndDate(usVO.getPlannedEndDate())
							.epic(epic).createdBy(request.getCreatedBy()).build();

					List<ProjectLeadAssignment> leads = new ArrayList<>();
					for (ProjectLeadAssignmentVO leadVO : usVO.getLeads()) {
						Long companyId = leadVO.getCompanyId(); // Assuming CompanyMasterVO has
																// getCompanyId()
						Optional<CompanyMaster> leadCompany = companyMasterRepository.findById(companyId);
						if (!leadCompany.isPresent()) {
							throw new FlickzzDeskException(DOES_NOT_EXIST,
									getDescription(DOES_NOT_EXIST.getDescription(), LEAD_COMPANY) + companyId);
						}
						ProjectLeadAssignment pla = ProjectLeadAssignment.builder().company(leadCompany.get())
								.userStory(userStory).createdBy(request.getCreatedBy()).build();
						leads.add(pla);
					}
					userStory.setProjectLeadAssignments(leads);

					List<Task> tasks = new ArrayList<>();
					if (usVO.getTasks() != null && !usVO.getTasks().isEmpty()) {
						int taskSeq = 1;
						for (TaskVO taskVO : usVO.getTasks()) {
							Task task = Task.builder().title(taskVO.getTitle()).description(taskVO.getDescription())
									.taskSequence(
											taskVO.getTaskSequence() != null ? taskVO.getTaskSequence() : taskSeq++)
									.progressStatus(defaultProgressStatus).maxProgressStatus(maxProgressStatus)
									.plannedStartDate(taskVO.getPlannedStartDate())
									.plannedEndDate(taskVO.getPlannedEndDate()).userStory(userStory)
									.createdBy(request.getCreatedBy()).build();
							tasks.add(task);
						}
					}
					userStory.setTasks(tasks);
					userStories.add(userStory);
					storyMap.put(usVO.getMappingStoryId(), userStory);
					project.setPlannedEndDate(usVO.getPlannedEndDate());
				}
				epic.setUserStories(userStories);
				epics.add(epic);
			}
			project.getEpics().addAll(epics);

			// Wire predecessors before persisting so the self-reference is not transient
			for (EpicVO epicVO : request.getEpics()) {
				for (UserStoryVO usVO : epicVO.getUserStories()) {
					if (usVO != null && usVO.getMappingPredecessorId() != null
							&& !usVO.getMappingPredecessorId().equals(0L)) {
						UserStory userStory = storyMap.get(usVO.getMappingStoryId());
						UserStory predecessor = storyMap.get(usVO.getMappingPredecessorId().toString());
						if (userStory != null && predecessor != null) {
							userStory.setPredecessor(predecessor);
						}
					}
				}
			}

			project.setIsSaved(request.isSave());
			project.setIsSubmited(request.isSubmit());
			// Save project with all nested entities
			project = projectRepository.save(project);

			// Return ProjectVO
			return mapper.toProjectVO(project);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in updateProject method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		}
	}

	public ProgressStatusVO createProgresstatus(ProgressStatusRequestVO request) {
		log.debug(generateLog(ENTRY, this.getClass().getName()));
		try {
			if (request == null || request.getOrgId() == null) {
				throw new FlickzzDeskException(INVALID_FIELD, getDescription(INVALID_FIELD.getDescription(), COMPANY));
			}

			Optional<CompanyMaster> company = companyMasterRepository.findByCompanyIdAndIsActive(request.getOrgId(),
					ACTIVE);
			if (company.isEmpty()) {
				throw new FlickzzDeskException(DOES_NOT_EXIST,
						getDescription(DOES_NOT_EXIST.getDescription(), COMPANY));
			}

			int maxSequence = progressStatusRepository.findMaxProgressStatus(request.getOrgId());

			ProgressStatus progressStatus = ProgressStatus.builder().company(company.get())
					.progressName(request.getProgressName()).progressSequence(maxSequence + 1)
					.colorCode(request.getColorCode()).createdBy(request.getCreatedBy()).build();

			progressStatus = progressStatusRepository.save(progressStatus);

			return mapper.toProgressStatusVO(progressStatus);
		} catch (FlickzzDeskException e) {
			throw e;
		} catch (Exception e) {
			log.error("Exception in createProgresstatus method in ProjectService", e);
			throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
		} finally {
			log.debug(generateLog(EXIT, this.getClass().getName()));
		}
	}
}
