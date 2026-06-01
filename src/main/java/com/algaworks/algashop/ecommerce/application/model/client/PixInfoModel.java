package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class PixInfoModel {
	private String copyPasteCode;
	private String qrCodeUrl;
	private OffsetDateTime expiresOn; //todo expires at

	public PixInfoModel() {
	}

	public PixInfoModel(String copyPasteCode, String qrCodeUrl, OffsetDateTime expiresOn) {
		this.copyPasteCode = copyPasteCode;
		this.qrCodeUrl = qrCodeUrl;
		this.expiresOn = expiresOn;
	}
}

