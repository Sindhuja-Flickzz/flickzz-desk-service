package com.flickzz.desk.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flickzz.desk.model.SystemAudit;
import com.flickzz.desk.repo.SystemAuditRepository;
import com.flickzz.desk.vo.request.SystemAuditRequest;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private SystemAuditRepository systemAuditRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
}
