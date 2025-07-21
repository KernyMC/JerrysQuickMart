package com.quickmart.app;

import java.math.BigDecimal;

/**
 * Represents an item in the shopping cart
 */
public class CartItem {
	private Item item;
	private int quantity;
	private BigDecimal unitPrice;
	
	public CartItem(Item item, int quantity, boolean isMember) {
		this.item = item;
		this.quantity = quantity;
		this.unitPrice = item.getPrice(isMember);
	}
	
	// Getters
	public Item getItem() { return item; }
	public int getQuantity() { return quantity; }
	public BigDecimal getUnitPrice() { return unitPrice; }
	
	/**
	 * Calculates the item subtotal
	 */
	public BigDecimal getSubtotal() {
		return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}
	
	/**
	 * Calculates the item tax
	 */
	public BigDecimal getTax() {
		if (item.isTaxable()) {
			return getSubtotal().multiply(BigDecimal.valueOf(0.065));
		}
		return BigDecimal.ZERO;
	}
	
	/**
	 * Calculates member savings
	 */
	public BigDecimal getMemberSavings() {
		return item.getMemberSavings().multiply(BigDecimal.valueOf(quantity));
	}
	
	@Override
	public String toString() {
		return String.format("%s x%d @ $%.2f = $%.2f", 
			item.getName(), quantity, unitPrice, getSubtotal());
	}
} 