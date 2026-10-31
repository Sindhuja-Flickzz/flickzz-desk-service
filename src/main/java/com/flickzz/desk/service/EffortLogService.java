package com.flickzz.desk.service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.model.AgentEffortLog;
import com.flickzz.desk.model.AgentMaster;
import com.flickzz.desk.model.EffortType;
import com.flickzz.desk.model.TicketMaster;
import com.flickzz.desk.repo.AgentEffortLogRepository;
import com.flickzz.desk.repo.AgentMasterRepository;
import com.flickzz.desk.repo.EffortTypeRepository;
import com.flickzz.desk.repo.TicketMasterRepository;
import com.flickzz.desk.vo.*;
import com.flickzz.desk.vo.request.EffortLogRequestVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.INVALID_REQUEST;

@Service
public class EffortLogService {

    private static final BigDecimal MAX_EFFORT_HOURS = new BigDecimal("9999.99");

    private final AgentEffortLogRepository effortLogRepository;
    private final AgentMasterRepository agentMasterRepository;
    private final EffortTypeRepository effortTypeRepository;
    private final TicketMasterRepository ticketMasterRepository;
    private final CommonMapper mapper;

    public EffortLogService(AgentEffortLogRepository effortLogRepository,
                            AgentMasterRepository agentMasterRepository,
                            EffortTypeRepository effortTypeRepository,
                            TicketMasterRepository ticketMasterRepository,
                            CommonMapper mapper) {
        this.effortLogRepository = effortLogRepository;
        this.agentMasterRepository = agentMasterRepository;
        this.effortTypeRepository = effortTypeRepository;
        this.ticketMasterRepository = ticketMasterRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EffortLogVO create(EffortLogRequestVO request) {
        validate(request, false);
        TicketMaster ticket = findTicket(request);
        AgentMaster agent = findAgent(request.getUserWorked(), request.getOrgId());
        EffortType effortType = findEffortType(request.getEffortTypeId(), request.getOrgId());

        AgentEffortLog effortLog = mapper.toAgentEffortLog(request, ticket, agent, effortType);
        return mapper.toEffortLogVO(effortLogRepository.save(effortLog));
    }

    private void validate(EffortLogRequestVO request, boolean updating) {
        if (request == null || (updating && (request.getEffortId() == null || request.getEffortId() <= 0))
                || request.getTicketId() == null || request.getTicketId() <= 0
                || request.getOrgId() == null || request.getOrgId() <= 0
                || request.getDate() == null
                || request.getEffortTypeId() == null || request.getEffortTypeId() <= 0
                || request.getUserWorked() == null || request.getUserWorked() <= 0
                || request.getUpdatedBy() == null || request.getUpdatedBy() <= 0
                || request.getIsUpdatedByAdmin() == null
                || (!updating && (request.getCreatedBy() == null || request.getCreatedBy() <= 0
                || request.getIsCreatedByAdmin() == null))) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Valid effort, ticket, organization, and audit data are required"));
        }
    }

    private TicketMaster findTicket(EffortLogRequestVO request) {
        TicketMaster ticket = findTicket(request.getTicketId(), request.getOrgId());
        if (ticket.getWorkItem() == null) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Ticket has no associated work item"));
        }
        return ticket;
    }

    private AgentMaster findAgent(Long agentId, Long orgId) {
        AgentMaster agent = agentMasterRepository.findById(agentId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + agentId)));
        if (!Boolean.TRUE.equals(agent.getIsActive()) || agent.getOrganization() == null
                || !Objects.equals(agent.getOrganization().getCompanyId(), orgId)) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Worked user does not belong to the provided organization"));
        }
        return agent;
    }

    private EffortType findEffortType(Long effortTypeId, Long orgId) {
        EffortType effortType = effortTypeRepository.findByEffortTypeIdAndCompany_CompanyId(effortTypeId, orgId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Effort type with ID " + effortTypeId)));
        if (!Boolean.TRUE.equals(effortType.getIsActive())) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Effort type is inactive"));
        }
        return effortType;
    }

    private TicketMaster findTicket(Long ticketId, Long orgId) {
        TicketMaster ticket = ticketMasterRepository.findById(ticketId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Ticket with ID " + ticketId)));
        if (ticket.getCompany() == null || !Objects.equals(ticket.getCompany().getCompanyId(), orgId)) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(), "Ticket does not belong to the provided organization"));
        }
        return ticket;
    }

    @Transactional
    public EffortLogVO update(EffortLogRequestVO request) {
        validate(request, true);
        AgentEffortLog effortLog = findEffortLog(request.getEffortId(), request.getTicketId(), request.getOrgId());
        TicketMaster ticket = findTicket(request);
        AgentMaster agent = findAgent(request.getUserWorked(), request.getOrgId());
        EffortType effortType = findEffortType(request.getEffortTypeId(), request.getOrgId());

        mapper.updateAgentEffortLog(effortLog, request, ticket, agent, effortType);
        return mapper.toEffortLogVO(effortLogRepository.save(effortLog));
    }

    private AgentEffortLog findEffortLog(Long effortId, Long ticketId, Long orgId) {
        return effortLogRepository.findByEffortIdAndTicket_TicketIdAndTicket_Company_CompanyId(
                        effortId, ticketId, orgId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Effort with ID " + effortId)));
    }

    @Transactional(readOnly = true)
    public List<EffortLogVO> list(Long ticketId, Long orgId, Boolean status) {
        validateIds(ticketId, orgId);
        findTicket(ticketId, orgId);
        return mapper.toEffortLogVOList(
                effortLogRepository.findByTicket_TicketIdAndTicket_Company_CompanyIdOrderByEffortDateDescEffortIdDesc(
                        ticketId, orgId));
    }

    private void validateIds(Long... ids) {
        for (Long id : ids) {
            if (id == null || id <= 0) {
                throw new FlickzzDeskException(INVALID_REQUEST,
                        getDescription(INVALID_REQUEST.getDescription(), "Effort, ticket, and organization IDs must be valid"));
            }
        }
    }

    @Transactional(readOnly = true)
    public TicketEffortSummaryVO getTicketEffortSummary(Long ticketId, Long orgId) {
        validateIds(ticketId, orgId);
        TicketMaster ticket = findTicket(ticketId, orgId);
//        if (ticket.getWorkItem() == null || !"RITM".equalsIgnoreCase(ticket.getWorkItem().getCode())) {
//            throw new FlickzzDeskException(INVALID_REQUEST,
//                    getDescription(INVALID_REQUEST.getDescription(), "Ticket is not a RITM"));
//        }

        List<TicketMaster> subtasks = ticketMasterRepository
                .findByTicketReferenceTicketIdAndCompanyCompanyIdOrderByTicketIdAsc(ticketId, orgId);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(ticketId);
        subtasks.stream().map(TicketMaster::getTicketId).forEach(ticketIds::add);

        Map<Long, Map<Long, AgentEffortAccumulator>> effortByTicketAndAgent = new LinkedHashMap<>();
        effortLogRepository
                .findByTicket_TicketIdInAndTicket_Company_CompanyIdOrderByEffortDateDescEffortIdDesc(
                        ticketIds, orgId)
                .forEach(effortLog -> {
                    Long effortLogTicketId = effortLog.getTicket().getTicketId();
                    Long agentId = effortLog.getAgent().getAgentId();
                    AgentEffortAccumulator agentEffort = effortByTicketAndAgent
                            .computeIfAbsent(effortLogTicketId, ignored -> new LinkedHashMap<>())
                            .computeIfAbsent(agentId, ignored -> new AgentEffortAccumulator(
                                    agentId, effortLog.getAgent().getAgentName()));
                    agentEffort.add(effortLog.getEffortHours(), effortLog.getEffortMinutes());
                });

        List<AgentEffortSummaryVO> ticketAgents = toAgentSummaries(effortByTicketAndAgent.get(ticketId));
        EffortAccumulator ticketEffort = sumAgents(effortByTicketAndAgent.get(ticketId));
        EffortAccumulator subtasksEffort = new EffortAccumulator();
        List<SubtaskEffortSummaryVO> subtaskSummaries = subtasks.stream().map(subtask -> {
            List<AgentEffortSummaryVO> agents = toAgentSummaries(
                    effortByTicketAndAgent.get(subtask.getTicketId()));
            EffortAccumulator subtaskEffort = sumAgents(effortByTicketAndAgent.get(subtask.getTicketId()));
            subtasksEffort.add(subtaskEffort.totalMinutes);
            return SubtaskEffortSummaryVO.builder()
                    .subtaskId(subtask.getTicketId())
                    .subtaskNumber(subtask.getTicketNumber())
                    .totalLoggedEffort(subtaskEffort.toDurationVO())
                    .agents(agents)
                    .build();
        }).toList();

        EffortAccumulator totalEffort = new EffortAccumulator();
        totalEffort.add(ticketEffort.totalMinutes);
        totalEffort.add(subtasksEffort.totalMinutes);

        return TicketEffortSummaryVO.builder()
                .ticketId(ticketId)
                .ticketNumber(ticket.getTicketNumber())
                .plannedEffort(EffortAccumulator.from(ticket.getPlannedHours(), ticket.getPlannedMinutes()).toDurationVO())
                .ticketLoggedEffort(ticketEffort.toDurationVO())
                .subtasksLoggedEffort(subtasksEffort.toDurationVO())
                .totalLoggedEffort(totalEffort.toDurationVO())
                .agents(ticketAgents)
                .subtasks(subtaskSummaries)
                .build();
    }

    private List<AgentEffortSummaryVO> toAgentSummaries(
            Map<Long, AgentEffortAccumulator> agentEfforts) {
        if (agentEfforts == null || agentEfforts.isEmpty()) {
            return List.of();
        }
        return agentEfforts.values().stream()
                .sorted(Comparator.comparing(AgentEffortAccumulator::getAgentName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(AgentEffortAccumulator::toVO)
                .toList();
    }

    private EffortAccumulator sumAgents(Map<Long, AgentEffortAccumulator> agentEfforts) {
        EffortAccumulator total = new EffortAccumulator();
        if (agentEfforts != null) {
            agentEfforts.values().forEach(agentEffort -> total.add(agentEffort.totalMinutes));
        }
        return total;
    }

    @Transactional(readOnly = true)
    public List<EffortLogVO> listByAgent(Long agentId, Long ticketId, Long orgId) {
        validateIds(agentId, ticketId, orgId);
        findTicket(ticketId, orgId);

        AgentMaster agent = agentMasterRepository.findById(agentId)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
                        getDescription(DOES_NOT_EXIST.getDescription(), "Agent with ID " + agentId)));
        if (agent.getOrganization() == null
                || !Objects.equals(agent.getOrganization().getCompanyId(), orgId)) {
            throw new FlickzzDeskException(INVALID_REQUEST,
                    getDescription(INVALID_REQUEST.getDescription(),
                            "Agent does not belong to the provided organization"));
        }

        return mapper.toEffortLogVOList(
                effortLogRepository
                        .findByAgent_AgentIdAndTicket_TicketIdAndTicket_Company_CompanyIdOrderByEffortDateDescEffortIdDesc(
                                agentId, ticketId, orgId));
    }

    @Transactional
    public void delete(Long effortId, Long ticketId, Long orgId) {
        validateIds(effortId, ticketId, orgId);
        effortLogRepository.delete(findEffortLog(effortId, ticketId, orgId));
    }

    private static class EffortAccumulator {
        protected long totalMinutes;

        private static EffortAccumulator from(Integer hours, Integer minutes) {
            EffortAccumulator effort = new EffortAccumulator();
            effort.add(hours, minutes);
            return effort;
        }

        protected void add(Integer hours, Integer minutes) {
            add((hours == null ? 0L : hours.longValue()) * 60L + (minutes == null ? 0L : minutes));
        }

        protected void add(long minutes) {
            totalMinutes += minutes;
        }

        private EffortDurationVO toDurationVO() {
            long hours = totalMinutes / 60;
            int minutes = (int) (totalMinutes % 60);
            return EffortDurationVO.builder()
                    .hours(hours)
                    .minutes(minutes)
                    .formatted(String.format(Locale.ROOT, "%02d:%02d", hours, minutes))
                    .build();
        }
    }

    private static class AgentEffortAccumulator extends EffortAccumulator {
        private final Long agentId;
        private final String agentName;

        private AgentEffortAccumulator(Long agentId, String agentName) {
            this.agentId = agentId;
            this.agentName = agentName;
        }

        private String getAgentName() {
            return agentName;
        }

        private AgentEffortSummaryVO toVO() {
            return AgentEffortSummaryVO.builder()
                    .agentId(agentId)
                    .agentName(agentName)
                    .effort(super.toDurationVO())
                    .build();
        }
    }
}
