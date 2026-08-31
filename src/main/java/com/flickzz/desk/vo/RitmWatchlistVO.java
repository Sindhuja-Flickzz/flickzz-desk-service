package com.flickzz.desk.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RitmWatchlistVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long watchlistId;
    private RitmMasterVO ritm;
    private AgentMasterVO watchedBy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime removedAt;
}
