package net.catenax.traceability.common.test;

import javax.validation.constraints.NotBlank;

public record AddUpdateEdcUrl(@NotBlank(message = "Provide bpn number") String bpnNumber,@NotBlank(message = "Provide EDC url") String edcUrl) {
}
