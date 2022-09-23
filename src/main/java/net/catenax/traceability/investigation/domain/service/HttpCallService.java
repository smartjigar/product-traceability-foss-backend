package net.catenax.traceability.investigation.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MultivaluedMap;
import net.catenax.traceability.investigation.domain.model.edc.catalog.Catalog;
import net.catenax.traceability.investigation.domain.model.edc.policy.AtomicConstraint;
import net.catenax.traceability.investigation.domain.model.edc.policy.LiteralExpression;
import net.catenax.traceability.investigation.infrastructure.adapters.exception.BadRequestException;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;

@Component
public class HttpCallService {

	private final OkHttpClient httpClient;
	private final ObjectMapper objectMapper;
	@Value("${edc.catalog}")
	String catalogPath;

	public HttpCallService(OkHttpClient httpClient, ObjectMapper objectMapper) {
		this.httpClient = httpClient;
		this.objectMapper = objectMapper;
		objectMapper.registerSubtypes(AtomicConstraint.class, LiteralExpression.class);
	}


	public Catalog getCatalogFromProvider(
		String consumerEdcDataManagementUrl,
		String providerConnectorControlPlaneIDSUrl,
		Map<String, String> headers
	) throws IOException {
		var url = consumerEdcDataManagementUrl + catalogPath + providerConnectorControlPlaneIDSUrl;
		var request = new Request.Builder().url(url);
		headers.forEach(request::addHeader);

		return (Catalog) sendRequest(request.build(), Catalog.class);
	}


	public Object sendRequest(Request request, Class<?> responseObject) throws IOException {

		try (var response = httpClient.newCall(request).execute()) {
			var body = response.body();

			if (!response.isSuccessful() || body == null) {
				throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
			}

			String res = body.string();
			return objectMapper.readValue(res, responseObject);
		} catch (Exception e) {
			throw e;
		}
	}

	public void sendRequest(Request request) throws IOException {
		try (var response = httpClient.newCall(request).execute()) {
			var body = response.body();
			if (!response.isSuccessful() || body == null) {
				throw new BadRequestException(format("Control plane responded with: %s %s", response.code(), body != null ? body.string() : ""));
			}
		} catch (Exception e) {
			throw e;
		}
	}


	public HttpUrl getUrl(String connectorUrl, String subUrl, MultivaluedMap<String, String> parameters) {
		var url = connectorUrl;

		if (subUrl != null && !subUrl.isEmpty()) {
			url = url + "/" + subUrl;
		}

		HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();

		if (parameters == null) {
			return httpBuilder.build();
		}

		for (Map.Entry<String, List<String>> param : parameters.entrySet()) {
			for (String value : param.getValue()) {
				httpBuilder = httpBuilder.addQueryParameter(param.getKey(), value);
			}
		}

		return httpBuilder.build();
	}
}
