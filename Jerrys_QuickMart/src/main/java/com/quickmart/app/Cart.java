package com.quickmart.app;

import java.math.BigDecimal;
import java.util.*;

/**
 * Manages the shopping cart
 * 
 * ASSUMPTIONS:
 * - Cart is associated with a single customer type (member or regular)
 * - Customer type cannot be changed after cart creation
 * - Items in cart are unique by name (no duplicate items)
 * - Quantities are always positive integers
 * - Stock validation is performed before adding items
 * - Tax calculation is 6.5% for taxable items only
 * - Member prices are always lower than or equal to regular prices
 * - All monetary calculations use BigDecimal for precision
 * - Cart can be empty (no minimum purchase requirement)
 * - Cart is cleared after each transaction completion
 * - Inventory is updated immediately after purchase
 * - Member savings are calculated as difference between regular and member prices
 */
public class Cart {
	private List<CartItem> items;
	private boolean isMember;
	
	public Cart(boolean isMember) {
		this.items = new ArrayList<>();
		this.isMember = isMember;
	}
	
	/**
	 * Adds an item to the cart
	 */
	public boolean addItem(Item item, int quantity) {
		if (!item.hasStock(quantity)) {
			return false;
		}
		
		// Check if item already exists in cart
		for (CartItem cartItem : items) {
			if (cartItem.getItem().getName().equals(item.getName())) {
				// Update quantity if there's enough stock
				int newQuantity = cartItem.getQuantity() + quantity;
				if (item.hasStock(newQuantity)) {
					// Replace item with new quantity
					items.remove(cartItem);
					items.add(new CartItem(item, newQuantity, isMember));
					return true;
				}
				return false;
			}
		}
		
		// Add new item
		items.add(new CartItem(item, quantity, isMember));
		return true;
	}
	
	/**
	 * Removes an item from the cart
	 */
	public boolean removeItem(String itemName, int quantity) {
		for (int i = 0; i < items.size(); i++) {
			CartItem cartItem = items.get(i);
			if (cartItem.getItem().getName().equals(itemName)) {
				if (quantity >= cartItem.getQuantity()) {
					// Remove completely
					items.remove(i);
				} else {
					// Reduce quantity
					items.set(i, new CartItem(cartItem.getItem(), 
						cartItem.getQuantity() - quantity, isMember));
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Clears the cart
	 */
	public void clear() {
		items.clear();
	}
	
	/**
	 * Gets the subtotal
	 */
	public BigDecimal getSubtotal() {
		return items.stream()
			.map(CartItem::getSubtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	/**
	 * Gets the total tax
	 */
	public BigDecimal getTotalTax() {
		return items.stream()
			.map(CartItem::getTax)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	/**
	 * Gets the total amount
	 */
	public BigDecimal getTotal() {
		return getSubtotal().add(getTotalTax());
	}
	
	/**
	 * Gets the total member savings
	 */
	public BigDecimal getTotalMemberSavings() {
		if (!isMember) return BigDecimal.ZERO;
		return items.stream()
			.map(CartItem::getMemberSavings)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	/**
	 * Gets the total number of items
	 */
	public int getTotalItems() {
		return items.stream()
			.mapToInt(CartItem::getQuantity)
			.sum();
	}
	
	/**
	 * Gets all items in the cart
	 */
	public List<CartItem> getItems() {
		return new ArrayList<>(items);
	}
	
	/**
	 * Checks if the cart is empty
	 */
	public boolean isEmpty() {
		return items.isEmpty();
	}
	
	/**
	 * Displays the cart contents
	 */
	public void display() {
		if (isEmpty()) {
			System.out.println("The cart is empty.");
			return;
		}
		
		System.out.println("\n=== SHOPPING CART ===");
		for (CartItem item : items) {
			System.out.println(item.toString());
		}
		System.out.printf("Subtotal: $%.2f%n", getSubtotal());
		System.out.printf("Tax: $%.2f%n", getTotalTax());
		System.out.printf("Total: $%.2f%n", getTotal());
		if (isMember) {
			System.out.printf("Member savings: $%.2f%n", getTotalMemberSavings());
		}
		System.out.println();
	}
	
	/**
	 * Updates inventory after purchase
	 */
	public void updateInventory(Inventory inventory) {
		for (CartItem cartItem : items) {
			Item item = cartItem.getItem();
			item.reduceStock(cartItem.getQuantity());
			inventory.updateItemStock(item.getName(), item.getQuantity());
		}
	}
} 