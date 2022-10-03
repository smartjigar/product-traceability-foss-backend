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

package net.catenax.traceability.common.test;

import net.catenax.traceability.assets.domain.model.QualityType;

import java.util.List;

@Deprecated
//TODO temporary commit
public class AssetRequest {
	private String id;
	private String idShort;
	private String nameAtManufacturer;
	private String manufacturerPartId;
	private String manufacturerId;
	private String manufacturerName;
	private String nameAtCustomer;
	private String customerPartId;

	private String manufacturingDate;
	private String manufacturingCountry;
	private List<ChildDescription> childDescriptions;
	private QualityType qualityType;


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

	public String getManufacturingDate() {
		return manufacturingDate;
	}

	public String getManufacturingCountry() {
		return manufacturingCountry;
	}

	public List<ChildDescription> getChildDescriptions() {
		return childDescriptions;
	}

	public QualityType getQualityType() {
		return qualityType;
	}

}
