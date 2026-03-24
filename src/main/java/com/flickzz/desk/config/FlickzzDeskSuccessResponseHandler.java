package com.flickzz.desk.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.flickzz.desk.vo.GeneralRespVO;

@RestControllerAdvice
public class FlickzzDeskSuccessResponseHandler {
	
	public static ResponseEntity<GeneralRespVO> handleSuccessResponse(FlickzzDeskSuccessCodes vo) {
    	FlickzzDeskSuccessResponse response = new FlickzzDeskSuccessResponse(vo.getCode(), vo.getTitle(), vo.getDescription());
    	GeneralRespVO generalRespVO = new GeneralRespVO("GM-200", response);
        return ResponseEntity.ok(generalRespVO);
    }

	
	public static ResponseEntity<GeneralRespVO> handleSuccessResponse(FlickzzDeskSuccessCodes vo, String description) {
    	FlickzzDeskSuccessResponse response = new FlickzzDeskSuccessResponse(vo.getCode(), vo.getTitle(), vo.getDescription());
    	GeneralRespVO generalRespVO = new GeneralRespVO("GM-200", response);
        return ResponseEntity.ok(generalRespVO);
    }
	
	public static ResponseEntity<GeneralRespVO> handleSuccessResponse(FlickzzDeskSuccessCodes vo, Object object) {
    	FlickzzDeskSuccessResponse response = new FlickzzDeskSuccessResponse(vo.getCode(), vo.getTitle(), vo.getDescription());
    	GeneralRespVO generalRespVO = new GeneralRespVO("GM-200", response, object);
        return ResponseEntity.ok(generalRespVO);
    }

	public static ResponseEntity<GeneralRespVO> handleSuccessResponse(FlickzzDeskSuccessCodes vo, String description, Object object) {
		FlickzzDeskSuccessResponse response = new FlickzzDeskSuccessResponse(vo.getCode(), vo.getTitle(), description);
    	GeneralRespVO generalRespVO = new GeneralRespVO("GM-200", response, object);
        return ResponseEntity.ok(generalRespVO);
	}
}
