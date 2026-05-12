package com.flickzz.desk.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketWatchlistVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long mappingId;
	private TicketVO ticket; // FK reference to FD_TICKET
	private UserVO user; // FK reference to FD_USER
}
