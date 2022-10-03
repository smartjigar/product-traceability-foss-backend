/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.traceability.assets.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.catenax.traceability.common.test.AssetRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public final class Asset {
	private final String id;
	private final String idShort;
	private final String nameAtManufacturer;
	private final String manufacturerPartId;
	private final String manufacturerId;
	private final String batchId;
	private String manufacturerName;
	private final String nameAtCustomer;
	private final String customerPartId;
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private final Instant manufacturingDate;
	private final String manufacturingCountry;
	private final boolean supplierPart;
	private List<ChildDescriptions> childDescriptions;
	private QualityType qualityType;

	public Asset(
		String id,
		String idShort,
		String nameAtManufacturer,
		String manufacturerPartId,
		String manufacturerId,
		String batchId,
		String manufacturerName,
		String nameAtCustomer,
		String customerPartId,
		Instant manufacturingDate,
		String manufacturingCountry,
		boolean supplierPart,
		List<ChildDescriptions> childDescriptions,
		QualityType qualityType
	) {
		this.id = id;
		this.idShort = idShort;
		this.nameAtManufacturer = nameAtManufacturer;
		this.manufacturerPartId = manufacturerPartId;
		this.manufacturerId = manufacturerId;
		this.batchId = batchId;
		this.manufacturerName = manufacturerName;
		this.nameAtCustomer = nameAtCustomer;
		this.customerPartId = customerPartId;
		this.manufacturingDate = manufacturingDate;
		this.manufacturingCountry = manufacturingCountry;
		this.supplierPart = supplierPart;
		this.childDescriptions = childDescriptions;
		this.qualityType = qualityType;
	}

	//TODO please REMOVE it before PR
	public Asset(AssetRequest assetRequest) throws ParseException {
		this.id = assetRequest.getId();
		this.idShort = assetRequest.getIdShort();
		this.nameAtManufacturer = assetRequest.getNameAtManufacturer();
		this.manufacturerPartId = assetRequest.getManufacturerPartId();
		this.manufacturerId = assetRequest.getManufacturerId();
		this.manufacturerName = assetRequest.getManufacturerName();
		this.nameAtCustomer = assetRequest.getNameAtCustomer();
		this.customerPartId = assetRequest.getCustomerPartId();
		this.manufacturingDate = new SimpleDateFormat("yyyy-MM-dd").parse(assetRequest.getManufacturingDate()).toInstant();
		this.manufacturingCountry = assetRequest.getManufacturingCountry();
		this.childDescriptions = assetRequest.getChildDescriptions().stream().map(e -> new ChildDescriptions(e.getId(), e.getIdShort())).collect(Collectors.toList());
		this.qualityType = assetRequest.getQualityType();
		this.supplierPart = true;
		this.batchId = "test";
	}

	public String getBatchId() {
		return batchId;
	}

	public void updateQualityType(QualityType qualityType) {
		this.qualityType = qualityType;
	}

	public String getId() {
		return id;
	}

	public String getIdShort() {
		return idShort;
	}

	public String getNameAtManufacturer() {
		return nameAtManufacturer;
	}

	public String getManufacturerPartId() {
		return manufacturerPartId;
	}

	public String getManufacturerId() {
		return manufacturerId;
	}

	public String getManufacturerName() {
		return manufacturerName;
	}

	public String getNameAtCustomer() {
		return nameAtCustomer;
	}

	public String getCustomerPartId() {
		return customerPartId;
	}

	public Instant getManufacturingDate() {
		return manufacturingDate;
	}

	public String getManufacturingCountry() {
		return manufacturingCountry;
	}

	public boolean isSupplierPart() {
		return supplierPart;
	}

	public List<ChildDescriptions> getChildDescriptions() {
		return childDescriptions;
	}

	public QualityType getQualityType() {
		return qualityType;
	}

	public record ChildDescriptions(String id, String idShort) {}

}
