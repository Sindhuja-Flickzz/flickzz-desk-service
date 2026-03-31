package com.flickzz.desk.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FlickzzDeskResponseHandler {
	
	public static ResponseEntity<FlickzzDeskResponse> handleSuccessResponse(FlickzzDeskSuccessCodes vo) {
    	FlickzzDeskResponse response = new FlickzzDeskResponse(vo.getCode(), vo.getTitle(), vo.getDescription(), null);
        return ResponseEntity.ok(response);
    }

	
	public static ResponseEntity<FlickzzDeskResponse> handleSuccessResponse(FlickzzDeskSuccessCodes vo, String description) {
    	FlickzzDeskResponse response = new FlickzzDeskResponse(vo.getCode(), vo.getTitle(), vo.getDescription(), null);
        return ResponseEntity.ok(response);
    }
	
	public static ResponseEntity<FlickzzDeskResponse> handleSuccessResponse(FlickzzDeskSuccessCodes vo, Object object) {
    	FlickzzDeskResponse response = new FlickzzDeskResponse(vo.getCode(), vo.getTitle(), vo.getDescription(), object);
        return ResponseEntity.ok(response);
    }

	public static ResponseEntity<FlickzzDeskResponse> handleSuccessResponse(FlickzzDeskSuccessCodes vo, String description, Object object) {
		FlickzzDeskResponse response = new FlickzzDeskResponse(vo.getCode(), vo.getTitle(), description, object);
        return ResponseEntity.ok(response);
	}
}
