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

package net.catenax.traceability.assets.infrastructure.adapters.feign.irs.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

class Submodel {

	@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
		property = "aspectType")
	@JsonSubTypes({
		@JsonSubTypes.Type(value = AssemblyPartRelationship.class, name = "urn:bamm:com.catenax.assembly_part_relationship:1.0.0#AssemblyPartRelationship"),
		@JsonSubTypes.Type(value = SerialPartTypization.class, name = "urn:bamm:com.catenax.serial_part_typization:1.0.0#SerialPartTypization"),
		@JsonSubTypes.Type(value = SerialPartTypization.class, name = "urn:bamm:com.catenax.batch:1.0.0#Batch")
	})
	private Object payload;

	@JsonCreator
	public Submodel(@JsonProperty("payload") Object payload) {
		this.payload = payload;
	}

	public Object getPayload() {
		return payload;
	}
}



