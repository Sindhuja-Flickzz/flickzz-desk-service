package com.flickzz.desk.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.mapper.CommonMapper;
import com.flickzz.desk.vo.SystemAuditVO;
import com.flickzz.desk.vo.request.SystemAuditFilterRequestVO;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flickzz.desk.model.SystemAudit;
import com.flickzz.desk.repo.SystemAuditRepository;
import com.flickzz.desk.vo.request.SystemAuditRequest;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import static com.flickzz.desk.config.FlickzzDeskUtility.generateLog;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DEFAULT_ERROR_CODE;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private SystemAuditRepository systemAuditRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommonMapper mapper;

    public void recordAudit(SystemAuditRequest req) {
        try {
            SystemAudit audit = new SystemAudit();
            audit.setModule(req.getModule());
            audit.setArea(req.getArea());
            audit.setEntityName(req.getEntityName());
            audit.setEntityId(req.getEntityId());
            audit.setAction(req.getAction());
            audit.setOldValue((req.getOldValue() == null || req.getOldValue().isBlank()) ? null : req.getOldValue());
            audit.setNewValue((req.getNewValue() == null || req.getNewValue().isBlank()) ? null : req.getNewValue());
            audit.setChangedFields((req.getChangedFields() == null || req.getChangedFields().isBlank()) ? null : req.getChangedFields());
            if (req.getUserId() != null) {
                audit.setUserId(req.getUserId());
            }
            audit.setUserName(req.getUserName());
            audit.setCompanyId(req.getCompanyId());
            audit.setStatus(req.getStatus());
            audit.setErrorMessage(req.getErrorMessage());
            audit.setCreatedAt(LocalDateTime.now());
            systemAuditRepository.save(audit);
        } catch (Exception e) {
            log.error("Failed to persist system audit", e);
        }
    }

    public void recordExceptionAudit(SystemAuditRequest req, Exception ex) {
        if (req == null) return;
        try {
            if (req.getStatus() == null) req.setStatus("ERROR");
            if (req.getErrorMessage() == null && ex != null) req.setErrorMessage(ex.getMessage());
            recordAudit(req);
        } catch (Exception e) {
            log.error("Failed to persist exception audit", e);
        }
    }

    public List<SystemAuditVO> getAuditListByOrg(Long orgId) {
        log.info(generateLog("getAuditList", this.getClass().getName()));
        try {
            return systemAuditRepository.findByCompanyId(orgId).stream().map(mapper::toSystemAuditVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getAuditList method in CommonService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    public List<SystemAuditVO> getAuditListByFilter(SystemAuditFilterRequestVO filter) {
        log.info(generateLog("getAuditListByFilter", this.getClass().getName()));
        try {
            Specification<SystemAudit> specification = buildAuditSpecification(filter);
            return systemAuditRepository.findAll(specification).stream().map(mapper::toSystemAuditVO).toList();
        } catch (FlickzzDeskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Exception in getAuditListByFilter method in CommonService");
            throw new FlickzzDeskException(DEFAULT_ERROR_CODE);
        }
    }

    private Specification<SystemAudit> buildAuditSpecification(SystemAuditFilterRequestVO filter) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addExactPredicate(predicates, builder, root.get("action"), filter != null ? filter.getAction() : null);
            addExactPredicate(predicates, builder, root.get("area"), filter != null ? filter.getArea() : null);
            addExactPredicate(predicates, builder, root.get("status"), filter != null ? filter.getStatus() : null);
            addSearchPredicate(predicates, builder, root.get("userName"), filter != null ? filter.getChangedBy() : null);
            addSearchPredicate(predicates, builder, root.get("errorMessage"), filter != null ? filter.getSearch() : null);
            addDatePredicate(predicates, builder, root.get("createdAt"), filter != null ? filter.getFromDate() : null, true);
            addDatePredicate(predicates, builder, root.get("createdAt"), filter != null ? filter.getToDate() : null, false);
            addExactPredicate(predicates, builder, root.get("companyId"), filter != null ? filter.getOrgId() : null);
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addExactPredicate(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder builder,
                                   Expression<?> expression, Object value) {
        if (value != null) {
            predicates.add(builder.equal(expression, value));
        }
    }

    private void addSearchPredicate(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder builder,
                                    Expression<String> expression, String value) {
        if (StringUtils.hasText(value)) {
            predicates.add(builder.like(builder.lower(expression), "%" + value.toLowerCase() + "%"));
        }
    }

    private void addDatePredicate(List<Predicate> predicates, jakarta.persistence.criteria.CriteriaBuilder builder,
                                  Expression<LocalDateTime> expression, LocalDateTime value, boolean isFromDate) {
        if (value != null) {
            predicates.add(isFromDate ? builder.greaterThanOrEqualTo(expression, value)
                    : builder.lessThanOrEqualTo(expression, value));
        }
    }
}
