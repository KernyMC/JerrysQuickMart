package com.quickmart.app;

import java.io.*;
import java.math.BigDecimal;

/**
 * Manages incremental transaction numbering
 * 
 * ASSUMPTIONS:
 * - Transaction counter file is named "transaction_counter.txt" in application root
 * - Transaction numbers start from 1 and increment sequentially
 * - Transaction counter persists between application sessions
 * - If counter file is missing or corrupted, counter resets to 1
 * - Payment is always sufficient (validated before processing)
 * - All transactions are processed immediately (no queuing)
 * - Receipt files are saved in application root directory
 * - Inventory is updated immediately after transaction
 * - Transaction numbers are unique across all sessions
 * - No transaction rollback mechanism (transactions are final)
 * - File operations use default system encoding
 */
public class TransactionManager {
	private static final String TRANSACTION_COUNTER_FILE = "transaction_counter.txt";
	private static int currentTransactionNumber = 1;
	
	static {
		loadTransactionCounter();
	}
	
	/**
	 * Loads transaction counter from file
	 */
	private static void loadTransactionCounter() {
		try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_COUNTER_FILE))) {
			String line = reader.readLine();
			if (line != null && !line.trim().isEmpty()) {
				currentTransactionNumber = Integer.parseInt(line.trim());
			}
		} catch (IOException | NumberFormatException e) {
			// If file doesn't exist or there's an error, start from 1
			currentTransactionNumber = 1;
		}
	}
	
	/**
	 * Saves transaction counter to file
	 */
	private static void saveTransactionCounter() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTION_COUNTER_FILE))) {
			writer.println(currentTransactionNumber);
		} catch (IOException e) {
			System.err.println("Error saving transaction counter: " + e.getMessage());
		}
	}
	
	/**
	 * Gets the next transaction number
	 */
	public static int getNextTransactionNumber() {
		int nextNumber = currentTransactionNumber;
		currentTransactionNumber++;
		saveTransactionCounter();
		return nextNumber;
	}
	
	/**
	 * Processes a complete transaction
	 */
	public static Receipt processTransaction(Cart cart, BigDecimal cashPayment, Inventory inventory) {
		// Validate payment
		if (cashPayment.compareTo(cart.getTotal()) < 0) {
			throw new IllegalArgumentException("Insufficient payment");
		}
		
		// Get transaction number
		int transactionNumber = getNextTransactionNumber();
		
		// Create receipt
		Receipt receipt = new Receipt(transactionNumber, cart, cashPayment);
		
		// Update inventory
		cart.updateInventory(inventory);
		inventory.saveInventory();
		
		// Save receipt
		receipt.saveToFile();
		
		return receipt;
	}
} 