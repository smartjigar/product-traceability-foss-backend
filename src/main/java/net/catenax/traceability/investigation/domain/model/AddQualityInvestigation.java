package net.catenax.traceability.investigation.domain.model;

import net.catenax.traceability.common.utility.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

public record AddQualityInvestigation(@NotEmpty(message = Constants.PARTS_IDS_MUST_PRESENT) Set<String> partIds,
									  @NotBlank(message = Constants.DESCRIPTION_MUST_PRESENT) String description) {
}
