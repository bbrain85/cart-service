package com.redhat.coolstore.cart.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.service.CatalogServiceImpl;
import com.redhat.coolstore.cart.service.ShoppingCartService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@org.junit.runner.RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CartEndpointTest {

	@LocalServerPort
	private int port;

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());
	
	private static final String CATALOG_SERVICE_FIELD = "catalogService";
	private static final String PRODUCT_ID = "111111";
	private static final String SHOPPING_CART_ID = "1111";
	private Product product = new Product();
	
	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CartEndpoint cartEndpoint;

	@Before
	public void before() {
		RestAssured.baseURI = String.format("http://localhost:%d/cart", port);
		product.setItemId(PRODUCT_ID);
		product.setPrice(100.0);
	}

	@Test
	public void retrieveCartById() throws Exception {
		RestAssured.given().get("/{cartId}", SHOPPING_CART_ID).then().assertThat().statusCode(200)
				.contentType(ContentType.JSON).body("id", equalTo(SHOPPING_CART_ID))
				.body("cartItemTotal", equalTo(0.0f));
	}

	@Test
	public void addToCart() throws Exception {

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("catalog-response.json");
		WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/product/"+PRODUCT_ID))
				.willReturn(aResponse().withStatus(200).withHeader("Content-type", "application/json")
						.withBody(IOUtils.toString(is, Charset.defaultCharset()))));
		
		CatalogServiceImpl catalogService = new CatalogServiceImpl();
		ReflectionTestUtils.setField(catalogService, "catalogServiceUrl", "http://localhost:" + wireMockRule.port());
		
		ReflectionTestUtils.setField(shoppingCartService, CATALOG_SERVICE_FIELD, catalogService);
		
		ReflectionTestUtils.setField(cartEndpoint, "shoppingCartService", shoppingCartService);
		
		
		RestAssured.given().get("/{cartId}", SHOPPING_CART_ID).then().assertThat().statusCode(200)
				.contentType(ContentType.JSON);

		RestAssured.given().post("/{cartId}/{itemId}/{quantity}", SHOPPING_CART_ID, PRODUCT_ID, 2).then().assertThat()
				.statusCode(200).contentType(ContentType.JSON).body("id", equalTo(SHOPPING_CART_ID))
				.body("shoppingCartItemList[0].quantity", equalTo(2));
	}

	@Test
	public void removeFromCart() throws Exception {

		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("catalog-response.json");
		WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/product/"+PRODUCT_ID))
				.willReturn(aResponse().withStatus(200).withHeader("Content-type", "application/json")
						.withBody(IOUtils.toString(is, Charset.defaultCharset()))));
		
		CatalogServiceImpl catalogService = new CatalogServiceImpl();
		ReflectionTestUtils.setField(catalogService, "catalogServiceUrl", "http://localhost:" + wireMockRule.port());
		
		ReflectionTestUtils.setField(shoppingCartService, CATALOG_SERVICE_FIELD, catalogService);
		
		ReflectionTestUtils.setField(cartEndpoint, "shoppingCartService", shoppingCartService);
		
		RestAssured.given().get("/{cartId}", SHOPPING_CART_ID).then().assertThat().statusCode(200)
				.contentType(ContentType.JSON);

		RestAssured.given().post("/{cartId}/{itemId}/{quantity}", SHOPPING_CART_ID, PRODUCT_ID, 2).then().assertThat()
				.statusCode(200).contentType(ContentType.JSON).body("id", equalTo(SHOPPING_CART_ID))
				.body("shoppingCartItemList[0].quantity", equalTo(2));

		RestAssured.given().delete("/{cartId}/{itemId}/{quantity}", SHOPPING_CART_ID, PRODUCT_ID, 1).then().assertThat()
				.statusCode(200).contentType(ContentType.JSON).body("id", equalTo(SHOPPING_CART_ID))
				.body("shoppingCartItemList[0].quantity", equalTo(1));
	}
}
