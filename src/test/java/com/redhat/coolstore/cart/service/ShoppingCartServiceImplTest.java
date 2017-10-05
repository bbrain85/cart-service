package com.redhat.coolstore.cart.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.util.ReflectionTestUtils;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

public class ShoppingCartServiceImplTest {

	private static final String PRICE_CALCULATION_SERVICE_FIELD = "priceCalculationService";
	private static final String CATALOG_SERVICE_FIELD = "catalogService";
	private static final String PRODUCT_ID = "p1";
	private static final String SHOPPING_CART_ID = "1111";
	Product product = new Product();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private PriceCalculationService priceCalculationService;

	@Mock
	private CatalogService catalogService;

	@Before
	public void setUp(){
		product.setItemId(PRODUCT_ID);
		product.setPrice(100.0);
	}
	
	
	@Test
	public void testGetShoppingCart() {
		ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl();
		ReflectionTestUtils.setField(shoppingCartService, CATALOG_SERVICE_FIELD, catalogService);
		ReflectionTestUtils.setField(shoppingCartService, PRICE_CALCULATION_SERVICE_FIELD, priceCalculationService);
		ShoppingCart shoppingCart2 = shoppingCartService.getShoppingCart(SHOPPING_CART_ID);
		assertThat(shoppingCart2.getId(), equalTo(SHOPPING_CART_ID));
	}

	@Test
	public void testAddToCart() {
		
		when(catalogService.getProduct(PRODUCT_ID)).thenReturn(product);

		ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl();
		shoppingCartService.getShoppingCart(SHOPPING_CART_ID);

		ReflectionTestUtils.setField(shoppingCartService, CATALOG_SERVICE_FIELD, catalogService);
		ReflectionTestUtils.setField(shoppingCartService, PRICE_CALCULATION_SERVICE_FIELD, priceCalculationService);

		ShoppingCart shoppingCart = shoppingCartService.addToCart(SHOPPING_CART_ID, product.getItemId(), 2);

		verify(catalogService).getProduct(PRODUCT_ID);
		verify(priceCalculationService, atLeastOnce()).priceShoppingCart(any());

		assertThat(shoppingCart.getId(), equalTo(SHOPPING_CART_ID));
	}

	@Test
	public void testRemoveFromCart() {
		when(catalogService.getProduct(PRODUCT_ID)).thenReturn(product);
		
		ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl();
		
		ReflectionTestUtils.setField(shoppingCartService, CATALOG_SERVICE_FIELD, catalogService);
		ReflectionTestUtils.setField(shoppingCartService, PRICE_CALCULATION_SERVICE_FIELD, priceCalculationService);

		ShoppingCart shoppingCart= shoppingCartService.getShoppingCart(SHOPPING_CART_ID); 
		shoppingCart = shoppingCartService.addToCart(SHOPPING_CART_ID, product.getItemId(), 2);
		shoppingCart = shoppingCartService.removeFromCart(SHOPPING_CART_ID, product.getItemId(), 1);
		
		ShoppingCartItem shoppingCartItem = null;
		Iterator<ShoppingCartItem> shoppingCartIterator = shoppingCart.getShoppingCartItemList().iterator();
		while(shoppingCartIterator.hasNext()){
			shoppingCartItem = shoppingCartIterator.next();
			if (shoppingCartItem.getProduct().getItemId().equals(product.getItemId())){
				break;
			}
		}
		
		assertThat(shoppingCartItem.getQuantity(), equalTo(1));
		verify(catalogService).getProduct(PRODUCT_ID);
	}

	@Test
	public void testCalculateCartPrice() {
		
		when(catalogService.getProduct(PRODUCT_ID)).thenReturn(product);
		
		ShoppingCartService shoppingCartService = new ShoppingCartServiceImpl();
		ReflectionTestUtils.setField(shoppingCartService, CATALOG_SERVICE_FIELD, catalogService);
		ReflectionTestUtils.setField(shoppingCartService, PRICE_CALCULATION_SERVICE_FIELD, new PriceCalculationServiceImpl());

		ShoppingCart shoppingCart= shoppingCartService.getShoppingCart(SHOPPING_CART_ID); 
		shoppingCart = shoppingCartService.addToCart(SHOPPING_CART_ID, product.getItemId(), 2);
		shoppingCart = shoppingCartService.removeFromCart(SHOPPING_CART_ID, product.getItemId(), 1);
		
		ShoppingCartItem shoppingCartItem = null;
		Iterator<ShoppingCartItem> shoppingCartIterator = shoppingCart.getShoppingCartItemList().iterator();
		while(shoppingCartIterator.hasNext()){
			shoppingCartItem = shoppingCartIterator.next();
			if (shoppingCartItem.getProduct().getItemId().equals(product.getItemId())){
				break;
			}
		}
		
		assertThat(shoppingCartItem.getQuantity(), equalTo(1));
		assertThat(shoppingCartItem.getPrice(), equalTo(100.0));
		verify(catalogService).getProduct(PRODUCT_ID);
	}
}
