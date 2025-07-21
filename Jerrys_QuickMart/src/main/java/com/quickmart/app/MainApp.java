package com.quickmart.app;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Main application for Jerry's Quick Mart
 * 
 * ASSUMPTIONS:
 * - User input is provided through System.in (console)
 * - User will enter valid numeric inputs when prompted for IDs and quantities
 * - User will enter valid menu options (1-8)
 * - Customer type selection is mandatory before proceeding
 * - Cart is reset for each new transaction
 * - Application continues until user explicitly exits
 * - All monetary calculations use BigDecimal for precision
 * - Product IDs are sequential starting from 1
 * - Cart item IDs are sequential starting from 1
 * - User can cancel transaction at any time
 * - Payment is always in cash (no credit card support)
 * - Change is always provided in cash
 */
public class MainApp {
	private Scanner scanner;
	private Inventory inventory;
	private Cart cart;
	private boolean isMember;
	
	public MainApp() {
		this.scanner = new Scanner(System.in);
		this.inventory = new Inventory();
	}
	
	/**
	 * Starts the application
	 */
	public void start() {
		System.out.println("=== JERRY'S QUICK MART ===");
		System.out.println("Welcome to our grand opening in Orlando!");
		
		while (true) {
			try {
				// Select customer type
				selectCustomerType();
				
				// Initialize cart
				cart = new Cart(isMember);
				
				// Show main menu
				showMainMenu();
				
				// Ask if continue
				if (!continueShopping()) {
					break;
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
		
		System.out.println("Thank you for using Jerry's Quick Mart!");
		scanner.close();
	}
	
	/**
	 * Selects the customer type
	 */
	private void selectCustomerType() {
		System.out.println("\nSelect customer type:");
		System.out.println("1. Rewards Member");
		System.out.println("2. Regular Customer");
		
		while (true) {
			System.out.print("Option: ");
			String choice = scanner.nextLine().trim();
			
			if (choice.equals("1")) {
				isMember = true;
				System.out.println("Rewards Member customer selected.");
				break;
			} else if (choice.equals("2")) {
				isMember = false;
				System.out.println("Regular customer selected.");
				break;
			} else {
				System.out.println("Invalid option. Enter 1 or 2.");
			}
		}
	}
	
	/**
	 * Shows the main menu
	 */
	private void showMainMenu() {
		while (true) {
			System.out.println("\n=== MAIN MENU ===");
			System.out.println("1. View inventory");
			System.out.println("2. Add item to cart");
			System.out.println("3. View cart");
			System.out.println("4. Remove item from cart");
			System.out.println("5. Clear cart");
			System.out.println("6. Checkout and Print receipt");
			System.out.println("7. Cancel transaction");
			System.out.println("8. Exit");
			
			System.out.print("Select an option: ");
			String choice = scanner.nextLine().trim();
			
			switch (choice) {
				case "1":
					inventory.displayInventory();
					break;
				case "2":
					addItemToCart();
					break;
				case "3":
					cart.display();
					break;
				case "4":
					removeItemFromCart();
					break;
				case "5":
					cart.clear();
					System.out.println("Cart cleared.");
					break;
				case "6":
					if (checkout()) {
						return; // Transaction completed
					}
					break;
				case "7":
					cart.clear();
					System.out.println("Transaction cancelled.");
					return;
				case "8":
					return;
				default:
					System.out.println("Invalid option.");
			}
		}
	}
	
	/**
	 * Adds an item to the cart
	 */
	private void addItemToCart() {
		// Show inventory with IDs
		inventory.displayInventory();
		
		System.out.print("Select product ID: ");
		try {
			int productId = Integer.parseInt(scanner.nextLine().trim());
			
			// Get item by ID
			Item item = getItemById(productId);
			if (item == null) {
				System.out.println("Invalid product ID.");
				return;
			}
			
			System.out.println("Product: " + item.getName());
			System.out.println("Available stock: " + item.getQuantity());
			
			System.out.print("Quantity: ");
			int quantity = Integer.parseInt(scanner.nextLine().trim());
			
			if (quantity <= 0) {
				System.out.println("Quantity must be greater than 0.");
				return;
			}
			
			if (cart.addItem(item, quantity)) {
				System.out.println("Item added to cart.");
			} else {
				System.out.println("Insufficient stock.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
		}
	}
	
	/**
	 * Gets an item by its ID
	 */
	private Item getItemById(int id) {
		int currentId = 1;
		for (Item item : inventory.getAllItems()) {
			if (currentId == id) {
				return item;
			}
			currentId++;
		}
		return null;
	}
	
	/**
	 * Removes an item from the cart
	 */
	private void removeItemFromCart() {
		if (cart.isEmpty()) {
			System.out.println("The cart is empty.");
			return;
		}
		
		// Display cart with IDs
		displayCartWithIds();
		
		System.out.print("Select cart item ID to remove: ");
		try {
			int cartItemId = Integer.parseInt(scanner.nextLine().trim());
			
			// Get cart item by ID
			CartItem cartItem = getCartItemById(cartItemId);
			if (cartItem == null) {
				System.out.println("Invalid cart item ID.");
				return;
			}
			
			System.out.println("Product: " + cartItem.getItem().getName());
			System.out.println("Current quantity in cart: " + cartItem.getQuantity());
			
			System.out.print("Quantity to remove (0 to remove all): ");
			int quantity = Integer.parseInt(scanner.nextLine().trim());
			
			if (quantity < 0) {
				System.out.println("Invalid quantity.");
				return;
			}
			
			if (cart.removeItem(cartItem.getItem().getName(), quantity)) {
				System.out.println("Item removed from cart.");
			} else {
				System.out.println("Item not found in cart.");
			}
		} catch (NumberFormatException e) {
			System.out.println("Invalid input. Please enter a number.");
		}
	}
	
	/**
	 * Displays the cart with numbered IDs
	 */
	private void displayCartWithIds() {
		if (cart.isEmpty()) {
			System.out.println("The cart is empty.");
			return;
		}
		
		System.out.println("\n=== SHOPPING CART ===");
		int id = 1;
		for (CartItem item : cart.getItems()) {
			System.out.println(id + ". " + item.toString());
			id++;
		}
		System.out.printf("Subtotal: $%.2f%n", cart.getSubtotal());
		System.out.printf("Tax: $%.2f%n", cart.getTotalTax());
		System.out.printf("Total: $%.2f%n", cart.getTotal());
		if (cart.getItems().stream().anyMatch(item -> item.getMemberSavings().compareTo(BigDecimal.ZERO) > 0)) {
			System.out.printf("Member savings: $%.2f%n", cart.getTotalMemberSavings());
		}
		System.out.println();
	}
	
	/**
	 * Gets a cart item by its ID
	 */
	private CartItem getCartItemById(int id) {
		int currentId = 1;
		for (CartItem item : cart.getItems()) {
			if (currentId == id) {
				return item;
			}
			currentId++;
		}
		return null;
	}
	
	/**
	 * Processes the checkout
	 */
	private boolean checkout() {
		if (cart.isEmpty()) {
			System.out.println("The cart is empty.");
			return false;
		}
		
		cart.display();
		System.out.print("Confirm purchase? (y/n): ");
		String confirm = scanner.nextLine().trim().toLowerCase();
		
		if (!confirm.equals("y") && !confirm.equals("yes")) {
			System.out.println("Purchase cancelled.");
			return false;
		}
		
		System.out.print("Cash payment: $");
		try {
			BigDecimal cashPayment = new BigDecimal(scanner.nextLine().trim());
			
			if (cashPayment.compareTo(cart.getTotal()) < 0) {
				System.out.println("Insufficient payment.");
				return false;
			}
			
			// Process transaction
			Receipt receipt = TransactionManager.processTransaction(cart, cashPayment, inventory);
			
			System.out.println("\n=== TRANSACTION COMPLETED ===");
			receipt.display();
			
			return true;
		} catch (NumberFormatException e) {
			System.out.println("Invalid amount.");
			return false;
		} catch (IllegalArgumentException e) {
			System.out.println("Error: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Asks if continue with another transaction
	 */
	private boolean continueShopping() {
		System.out.print("\nDo you want to process another transaction? (y/n): ");
		String choice = scanner.nextLine().trim().toLowerCase();
		return choice.equals("y") || choice.equals("yes");
	}
} 