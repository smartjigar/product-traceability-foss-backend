/*
 *  Copyright (c) 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - Initial implementation
 *
 */

package net.catenax.traceability.investigation.domain.model.edc.catalog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import net.catenax.traceability.investigation.domain.model.edc.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A request for a participant's {@link Catalog}.
 */
@JsonDeserialize(builder = CatalogRequest.Builder.class)
public class CatalogRequest implements RemoteMessage {

	private final String protocol;
	private final String connectorId;
	private final String connectorAddress;

	private CatalogRequest(@NotNull String protocol, @NotNull String connectorId, @NotNull String connectorAddress) {
		this.protocol = protocol;
		this.connectorId = connectorId;
		this.connectorAddress = connectorAddress;
	}

	@NotNull
	public String getProtocol() {
		return protocol;
	}

	@NotNull
	public String getConnectorId() {
		return connectorId;
	}

	@NotNull
	public String getConnectorAddress() {
		return connectorAddress;
	}

	public static class Builder {
		private String protocol;
		private String connectorId;
		private String connectorAddress;

		private Builder() {
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public Builder connectorId(String connectorId) {
			this.connectorId = connectorId;
			return this;
		}

		public Builder connectorAddress(String connectorAddress) {
			this.connectorAddress = connectorAddress;
			return this;
		}

		public CatalogRequest build() {
			Objects.requireNonNull(protocol, "protocol");
			Objects.requireNonNull(connectorId, "connectorId");
			Objects.requireNonNull(connectorAddress, "connectorAddress");

			return new CatalogRequest(protocol, connectorId, connectorAddress);
		}
	}
}
