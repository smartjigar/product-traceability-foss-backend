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

package net.catenax.traceability.common.security;

import net.catenax.traceability.investigation.infrastructure.adapters.jpa.AuditorAwareImpl;
import org.jetbrains.annotations.NotNull;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectedKeycloakAuthenticationHandler implements HandlerMethodArgumentResolver {

	private static final Logger logger = LoggerFactory.getLogger(InjectedKeycloakAuthenticationHandler.class);

	private final String resourceRealm;

	public InjectedKeycloakAuthenticationHandler(String resourceRealm) {
		this.resourceRealm = resourceRealm;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(InjectedKeycloakAuthentication.class);
	}

	@Override
	public Object resolveArgument(@NotNull MethodParameter parameter,
								  ModelAndViewContainer mavContainer,
								  @NotNull NativeWebRequest webRequest,
								  WebDataBinderFactory binderFactory) {
		AuditorAwareImpl auditorAware = new AuditorAwareImpl();
		SimpleKeycloakAccount simpleKeycloakAccount = auditorAware.getSimpleKeycloakAccount();
		RefreshableKeycloakSecurityContext keycloakSecurityContext = simpleKeycloakAccount.getKeycloakSecurityContext();

		AccessToken token = keycloakSecurityContext.getToken();
		Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();

		AccessToken.Access access = resourceAccess.get(resourceRealm);
		String userId = simpleKeycloakAccount.getPrincipal().getName();
		if (access != null) {
			Set<KeycloakRole> keycloakRoles = access.getRoles().stream()
				.map(KeycloakRole::parse)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet());
			return new KeycloakAuthentication(keycloakRoles,userId);
		} else {
			logger.warn("Keycloak token didn't contain {} resource realm roles", resourceRealm);
			return new KeycloakAuthentication(Set.of(),userId);
		}

	}
}
