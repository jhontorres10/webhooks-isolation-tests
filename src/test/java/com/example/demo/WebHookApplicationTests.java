package com.example.demo;

import com.example.demo.controller.OutboundController;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.github.tomakehurst.wiremock.http.RequestMethod.POST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.webhooks.Webhooks.webhook;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebHookApplicationTests {

	@LocalServerPort
	int randomServerPort;

	@Autowired
	private MockMvc mockMvc;

	@SpyBean
	private OutboundController outboundController;

	@RegisterExtension
	static WireMockExtension wm1 = WireMockExtension.newInstance()
			.options(wireMockConfig().dynamicPort().dynamicHttpsPort())
			.build();

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("faker.webhook", () -> "http://localhost:" + wm1.getPort());
	}

	@Test
	void contextLoads() throws Exception {
		wm1.stubFor(get(urlPathEqualTo("/provider"))
				.willReturn(ok())
				.withServeEventListener("webhook", webhook()
						.withMethod(POST)
						.withUrl("http://localhost:%s/outbound".formatted(randomServerPort))
						.withHeader("Content-Type", "application/json")
						.withBody("{ \"result\": \"SUCCESS\" }"))
		);

		mockMvc.perform(MockMvcRequestBuilders.get("/inbound"))
				.andDo(print())
				.andExpect(status().isOk());

		await().atMost(5, SECONDS).untilAsserted(() -> {
			// do more testing with your callback.
			verify(outboundController).outbound(any(Map.class));
		});
	}

}
