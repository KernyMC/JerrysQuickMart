package com.quickmart.app;

import java.math.BigDecimal;

/**
 * Represents a product in Jerry's Quick Mart inventory
 */
public class Item {
	private String name;
	private int quantity;
	private BigDecimal regularPrice;
	private BigDecimal memberPrice;
	private boolean isTaxable;
	
	public Item(String name, int quantity, BigDecimal regularPrice, BigDecimal memberPrice, boolean isTaxable) {
		this.name = name;
		this.quantity = quantity;
		this.regularPrice = regularPrice;
		this.memberPrice = memberPrice;
		this.isTaxable = isTaxable;
	}
	
	// Getters y setters
	public String getName() { return name; }
	public int getQuantity() { return quantity; }
	public BigDecimal getRegularPrice() { return regularPrice; }
	public BigDecimal getMemberPrice() { return memberPrice; }
	public boolean isTaxable() { return isTaxable; }
	
	public void setQuantity(int quantity) { this.quantity = quantity; }
	
	/**
	 * Checks if there's available stock
	 */
	public boolean hasStock(int requestedQuantity) {
		return quantity >= requestedQuantity;
	}
	
	/**
	 * Reduces the product stock
	 */
	public void reduceStock(int quantity) {
		if (this.quantity >= quantity) {
			this.quantity -= quantity;
		}
	}
	
	/**
	 * Gets the price based on customer type
	 */
	public BigDecimal getPrice(boolean isMember) {
		return isMember ? memberPrice : regularPrice;
	}
	
	/**
	 * Calculates member savings
	 */
	public BigDecimal getMemberSavings() {
		return regularPrice.subtract(memberPrice);
	}
	
	@Override
	public String toString() {
		return String.format("%s: %d, $%.2f, $%.2f, %s", 
			name, quantity, regularPrice, memberPrice, 
			isTaxable ? "Taxable" : "Tax-Exempt");
	}
} 