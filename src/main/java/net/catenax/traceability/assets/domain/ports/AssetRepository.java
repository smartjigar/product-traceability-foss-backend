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

package net.catenax.traceability.assets.domain.ports;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.model.PageResult;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface AssetRepository {
	Asset getAssetById(String assetId);

	Asset getAssetByChildId(String assetId, String childId);

	PageResult<Asset> getAssets(Pageable pageable);

	PageResult<Asset> getSupplierAssets(Pageable pageable);

	public PageResult<Asset> getOwnAssets(Pageable pageable);

	List<Asset> getAssets();

	Asset save(Asset asset);

	List<Asset> saveAll(List<Asset> assets);

    long countAssets();

	void clean();

	List<Asset> getAssetByIdIn(Set<String> partIds);
}
