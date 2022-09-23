package net.catenax.traceability.investigation.domain.model;

import net.catenax.traceability.common.utility.Constants;

import javax.validation.constraints.NotNull;

public record UpdateQualityInvestigationStatus(
	@NotNull(message = Constants.STATUS_MUST_BE_PRESENT) InvestigationStatus status) {
}
