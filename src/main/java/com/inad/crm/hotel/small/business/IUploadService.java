package com.inad.crm.hotel.small.business;

import com.inad.crm.hotel.small.commons.dto.DtoInFileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IUploadService {

	DtoInFileResponse uploadExcelFile(MultipartFile file);
}
