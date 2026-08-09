package com.flickzz.desk.vo;

import com.flickzz.desk.model.LanguageMaster;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLanguageMappingVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long mappingId;

	private UserVO user;

	private LanguageMasterVO language;

	private Boolean active;

	private Long createdBy;

	private LocalDateTime createdAt;

	private Boolean creatorAdmin;

	private Boolean updaterAdmin;

	private Long updatedBy;

	private LocalDateTime updatedAt;
}
