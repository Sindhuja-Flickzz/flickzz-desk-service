package com.flickzz.desk.repo;

import com.flickzz.desk.model.RitmComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RitmCommentRepository extends JpaRepository<RitmComment, Long> {

    List<RitmComment> findAllByRitmRitmIdOrderByCommentIdDesc(Long ritmId);
}
