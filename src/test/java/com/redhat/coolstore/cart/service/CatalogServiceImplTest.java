package com.redhat.coolstore.cart.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.redhat.coolstore.cart.model.Product;

public class CatalogServiceImplTest {

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());

	@Test
	public void testGetProduct() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("catalog-response.json");
		WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/product/111111"))
				.willReturn(aResponse().withStatus(200).withHeader("Content-type", "application/json")
						.withBody(IOUtils.toString(is, Charset.defaultCharset()))));
		
		CatalogServiceImpl catalogService = new CatalogServiceImpl();
        ReflectionTestUtils.setField(catalogService, "catalogServiceUrl", "http://localhost:" + wireMockRule.port(), null);
        
        Product product = catalogService.getProduct("111111");
        assertThat(product.getName(),equalTo("Product 111111"));
	}
}
