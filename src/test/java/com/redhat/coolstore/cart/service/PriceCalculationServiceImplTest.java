package com.redhat.coolstore.cart.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

public class PriceCalculationServiceImplTest {

	private List<ShoppingCartItem> shoppingCartItemList;
	
	private PriceCalculationService priceCalculationService = new PriceCalculationServiceImpl();
	
	@Before
	public void setUp(){
		
		ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
		shoppingCartItem.setPrice(10);
		shoppingCartItem.setQuantity(2);
		
		ShoppingCartItem shoppingCartItem2 = new ShoppingCartItem();
		shoppingCartItem2.setPrice(20);
		shoppingCartItem2.setQuantity(2);
		
		shoppingCartItemList = new ArrayList<ShoppingCartItem>();
		
		shoppingCartItemList.add(shoppingCartItem);
		shoppingCartItemList.add(shoppingCartItem2);
		
		
	}
	
	@After
	public void tearDown(){
		shoppingCartItemList = null;
	}
	
	@Test
	public void testPriceShoppingCart(){
		ShoppingCart sc = new ShoppingCart();
		sc.setShoppingCartItemList(shoppingCartItemList);
		priceCalculationService.priceShoppingCart(sc);
		assertThat(sc.getCartItemTotal(),equalTo(60.0));
		assertThat(sc.getCartTotal(),equalTo(60.0+6.99));
	}
}
