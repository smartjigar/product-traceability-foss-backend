package net.catenax.traceability.investigation.infrastructure.adapters.jpa;

import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

	public SimpleKeycloakAccount getSimpleKeycloakAccount() {
		Object details = Optional.ofNullable(SecurityContextHolder.getContext())
			.flatMap(it -> Optional.ofNullable(it.getAuthentication()))
			.flatMap(it -> Optional.ofNullable(it.getDetails()))
			.orElseThrow(() -> new IllegalStateException("No valid keycloak authentication found."));

		if (details instanceof SimpleKeycloakAccount simpleKeycloakAccount) {
			return simpleKeycloakAccount;
		}
		throw new IllegalStateException("No valid keycloak authentication found.");
	}

	public String fetchUserIdFromContext() {
		return getSimpleKeycloakAccount().getPrincipal().getName();
	}

	@Override
	public Optional<String> getCurrentAuditor() {
		//String userId = fetchUserIdFromContext();
		String userId = "testUser";
		return !userId.isEmpty() ? Optional.of(userId) : Optional.empty();
	}
}
