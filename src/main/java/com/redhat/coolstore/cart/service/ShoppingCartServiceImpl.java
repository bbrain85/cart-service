package com.redhat.coolstore.cart.service;

import java.util.HashMap;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redhat.coolstore.cart.model.Product;
import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

@Component
public class ShoppingCartServiceImpl implements ShoppingCartService {

	private HashMap<String, ShoppingCart> inMemoryDatabase = new HashMap<String, ShoppingCart>();

	@Autowired
	private CatalogService catalogService;

	@Autowired
	private PriceCalculationService priceCalculationService;

	@Override
	public ShoppingCart calculateCartPrice(ShoppingCart sc) {
		priceCalculationService.priceShoppingCart(sc);
		return sc;
	}

	@Override
	public ShoppingCart getShoppingCart(String cartId) {
		ShoppingCart shoppingCart = inMemoryDatabase.get(cartId);
		if (shoppingCart == null) {
			shoppingCart = new ShoppingCart();
			shoppingCart.setId(cartId);
			inMemoryDatabase.put(cartId, shoppingCart);
			return shoppingCart;
		}
		return shoppingCart;
	}

	@Override
	public ShoppingCart addToCart(String cartId, String itemId, int quantity) {
		ShoppingCart shoppingCart = inMemoryDatabase.get(cartId);
		if(shoppingCart == null) return null;
		
		Product product = catalogService.getProduct(itemId);
		
		ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
		shoppingCartItem.setQuantity(quantity);
		shoppingCartItem.setProduct(product);
		shoppingCartItem.setPrice(product.getPrice());
		shoppingCart.getShoppingCartItemList().add(shoppingCartItem);
		priceCalculationService.priceShoppingCart(shoppingCart);
		
		return shoppingCart;
	}

	@Override
	public ShoppingCart removeFromCart(String cartId, String itemId, int quantity) {
		ShoppingCart shoppingCart = inMemoryDatabase.get(cartId);
		if(shoppingCart == null) return null;
		
		ShoppingCartItem shoppingCartItem = null;
		Iterator<ShoppingCartItem> shoppingCartIterator = shoppingCart.getShoppingCartItemList().iterator();
		while(shoppingCartIterator.hasNext()){
			shoppingCartItem = shoppingCartIterator.next();
			if (shoppingCartItem.getProduct().getItemId().equals(itemId)){
				shoppingCartItem.setQuantity(quantity);
				priceCalculationService.priceShoppingCart(shoppingCart);
				break;
			}
		}
		return shoppingCart;
	}

}
