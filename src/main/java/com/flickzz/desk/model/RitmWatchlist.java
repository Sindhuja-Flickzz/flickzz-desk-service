package com.flickzz.desk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FD_RITM_WATCHLIST", uniqueConstraints = {@UniqueConstraint(name = "UK_RITM_WATCHLIST", columnNames = {"RITM_ID", "WATCHED_BY"})})
public class RitmWatchlist {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ritmWatchlistSeq"
    )
    @SequenceGenerator(
            name = "ritmWatchlistSeq",
            sequenceName = "FD_RITM_WATCHLIST_SEQ",
            allocationSize = 1
    )
    @Column(name = "WATCHLIST_ID")
    private Long watchlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RITM_ID", nullable = false)
    private RitmMaster ritm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WATCHED_BY", nullable = false)
    private AgentMaster watchedBy;

    @Builder.Default
    @Column(name = "IS_ACTIVE")
    private Boolean isActive = true;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "REMOVED_AT")
    private LocalDateTime removedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (isActive == null) {
            isActive = true;
        }
    }
}
