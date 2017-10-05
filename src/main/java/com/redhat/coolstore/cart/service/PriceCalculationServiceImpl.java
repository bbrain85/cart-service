package com.redhat.coolstore.cart.service;

import org.springframework.stereotype.Component;

import com.redhat.coolstore.cart.model.ShoppingCart;
import com.redhat.coolstore.cart.model.ShoppingCartItem;

@Component
public class PriceCalculationServiceImpl implements PriceCalculationService {

	@Override
	public void priceShoppingCart(ShoppingCart sc) {

		if ( sc.getShoppingCartItemList()==null) return;
		
		for (ShoppingCartItem shoppingCartItem : sc.getShoppingCartItemList()) {
			sc.setCartItemTotal(sc.getCartItemTotal() + (shoppingCartItem.getPrice() * shoppingCartItem.getQuantity()));
		}

		if (sc.getCartItemTotal() >= 0.01 && sc.getCartItemTotal() <= 24.99) {
			sc.setShippingTotal(2.99);
		} else if (sc.getCartItemTotal() >= 25 && sc.getCartItemTotal() <= 49.99) {
			sc.setShippingTotal(4.99);
		} else if (sc.getCartItemTotal() >= 50 && sc.getCartItemTotal() <= 74.99) {
			sc.setShippingTotal(6.99);
		} else if (sc.getCartItemTotal() >= 75) {
			sc.setShippingTotal(0);
		}
		sc.setCartTotal(sc.getCartItemTotal() + sc.getShippingTotal());
	}

}
