package com.sparta.finalticket.global.exception;

import com.sparta.finalticket.domain.user.dto.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler({
		IllegalArgumentException.class})
	public ResponseEntity<CommonResponse<Void>> handleIllegalArgumentException(Exception ex) {
		CommonResponse<Void> response = CommonResponse.<Void>builder()
			.msg(ex.getMessage())
			.statusCode(HttpStatus.BAD_REQUEST.value())
			.build();
		return ResponseEntity.badRequest().body(response);
	}
}