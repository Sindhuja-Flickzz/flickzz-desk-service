package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmApprover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RitmApproverRepository extends JpaRepository<RitmApprover, Long> {

    List<RitmApprover> findByRitmId_RitmIdAndIsActiveTrueOrderByApproverSequenceAsc(Long ritmId);

    List<RitmApprover> findByRitmId_RitmIdOrderByApproverSequenceAsc(Long ritmId);
}