package com.quickmart.app;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Manages product inventory from file
 * 
 * ASSUMPTIONS:
 * - Inventory file is named "inventory.txt" and located in the application root
 * - File format: "ItemName: quantity, $regularPrice, $memberPrice, TaxStatus"
 * - Prices can use either comma (European) or dot (US) as decimal separator
 * - TaxStatus must be exactly "Taxable" or "Tax-Exempt" (case-sensitive)
 * - Item names are unique and case-sensitive
 * - Quantities are non-negative integers
 * - Prices are positive decimal numbers
 * - File encoding is UTF-8
 * - Empty lines are ignored
 * - Invalid lines are logged but don't stop the loading process
 */
public class Inventory {
	private Map<String, Item> items;
	private static final String INVENTORY_FILE = "inventory.txt";
	
	public Inventory() {
		this.items = new HashMap<>();
		loadInventory();
	}
	
	/**
	 * Loads inventory from file
	 */
	private void loadInventory() {
		File file = new File(INVENTORY_FILE);
		System.out.println("DEBUG: Trying to read inventory from: " + file.getAbsolutePath());
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					parseInventoryLine(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error loading inventory: " + e.getMessage());
			System.err.println("DEBUG: Current working directory: " + System.getProperty("user.dir"));
		}
	}
	
	/**
	 * Parses a line from the inventory file
	 * Supports both comma and dot decimal formats
	 */
	private void parseInventoryLine(String line) {
		try {
			// Format: Item: quantity, $regularPrice, $memberPrice, TaxStatus
			String[] parts = line.split(":");
			if (parts.length != 2) {
				System.err.println("Invalid format in line: " + line);
				return;
			}
			String itemName = parts[0].trim();
			// Split only by comma+space, not decimal commas in prices
			String[] details = parts[1].trim().split(", ");
			if (details.length != 4) {
				System.err.println("Insufficient data in line: " + line);
				return;
			}
			int quantity = Integer.parseInt(details[0].trim());
			// Parse prices with flexible decimal format support
			BigDecimal regularPrice = parsePrice(details[1].trim());
			BigDecimal memberPrice = parsePrice(details[2].trim());
			boolean isTaxable = details[3].trim().equals("Taxable");
			items.put(itemName, new Item(itemName, quantity, regularPrice, memberPrice, isTaxable));
		} catch (NumberFormatException e) {
			System.err.println("Error in numeric format in line: " + line + " - " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error parsing line: " + line + " - " + e.getMessage());
		}
	}
	
	/**
	 * Parses price string with flexible decimal format support
	 * Accepts both comma and dot decimal separators
	 * 
	 * ASSUMPTIONS:
	 * - Price string starts with "$" symbol
	 * - If both comma and dot are present, comma is thousands separator (US format: $1,234.56)
	 * - If only comma is present, comma is decimal separator (European format: $3,75)
	 * - If only dot is present, dot is decimal separator (US format: $3.75)
	 * - Price is a valid decimal number after cleaning
	 * - No currency symbols other than "$" are expected
	 */
	private BigDecimal parsePrice(String priceStr) {
		// Remove dollar sign and trim
		String cleanPrice = priceStr.replace("$", "").trim();
		
		// Handle different decimal formats
		if (cleanPrice.contains(",") && cleanPrice.contains(".")) {
			// If both comma and dot are present, assume comma is thousands separator
			// and dot is decimal separator (e.g., $1,234.56)
			cleanPrice = cleanPrice.replace(",", "");
		} else if (cleanPrice.contains(",") && !cleanPrice.contains(".")) {
			// If only comma is present, assume it's a decimal separator (European format)
			// Convert to standard format
			cleanPrice = cleanPrice.replace(",", ".");
		}
		
		return new BigDecimal(cleanPrice);
	}
	
	/**
	 * Saves the updated inventory to file
	 */
	public void saveInventory() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(INVENTORY_FILE))) {
			for (Item item : items.values()) {
				writer.println(item.toString());
			}
		} catch (IOException e) {
			System.err.println("Error saving inventory: " + e.getMessage());
		}
	}
	
	/**
	 * Gets an item by name
	 */
	public Item getItem(String name) {
		return items.get(name);
	}
	
	/**
	 * Gets all items
	 */
	public Collection<Item> getAllItems() {
		return items.values();
	}
	
	/**
	 * Checks if an item exists
	 */
	public boolean hasItem(String name) {
		return items.containsKey(name);
	}
	
	/**
	 * Updates an item's stock
	 */
	public void updateItemStock(String itemName, int newQuantity) {
		Item item = items.get(itemName);
		if (item != null) {
			item.setQuantity(newQuantity);
		}
	}
	
	/**
	 * Displays the current inventory with product IDs
	 */
	public void displayInventory() {
		System.out.println("\n=== CURRENT INVENTORY ===");
		int id = 1;
		for (Item item : items.values()) {
			System.out.println(id + ". " + item.toString());
			id++;
		}
		System.out.println();
	}
} 