package com.inad.crm.hotel.small.controller.upload;

import com.inad.crm.hotel.small.business.IUploadService;
import com.inad.crm.hotel.small.commons.dto.DtoInFileResponse;
import com.inad.crm.hotel.small.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping(Constants.URL_BASE + "/uploads/excel")
public class UploadController {

	@Autowired
	private IUploadService uploadService;

	@PostMapping(path = "/colonies", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DtoInFileResponse> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file) {
		log.debug("Load started of file");
		DtoInFileResponse response = uploadService.uploadExcelFile(file);
		log.debug("Load finished of file");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
}
