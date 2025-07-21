package com.quickmart.app;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generates and saves transaction receipts
 */
public class Receipt {
	private int transactionNumber;
	private LocalDateTime dateTime;
	private List<CartItem> items;
	private BigDecimal subtotal;
	private BigDecimal tax;
	private BigDecimal total;
	private BigDecimal cashPayment;
	private BigDecimal change;
	private BigDecimal memberSavings;
	private boolean isMember;
	
	public Receipt(int transactionNumber, Cart cart, BigDecimal cashPayment) {
		this.transactionNumber = transactionNumber;
		this.dateTime = LocalDateTime.now();
		this.items = cart.getItems();
		this.subtotal = cart.getSubtotal();
		this.tax = cart.getTotalTax();
		this.total = cart.getTotal();
		this.cashPayment = cashPayment;
		this.change = cashPayment.subtract(total);
		this.memberSavings = cart.getTotalMemberSavings();
		this.isMember = cart.getItems().stream().anyMatch(item -> 
			item.getMemberSavings().compareTo(BigDecimal.ZERO) > 0);
	}
	
	/**
	 * Generates the receipt content
	 */
	private String generateReceiptContent() {
		StringBuilder sb = new StringBuilder();
		
		// Fecha y número de transacción
		sb.append(dateTime.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))).append("\n");
		sb.append("TRANSACTION: ").append(String.format("%06d", transactionNumber)).append("\n\n");
		
		// Tabla de items
		sb.append(String.format("%-12s %-10s %-12s %-8s\n", "ITEM", "QUANTITY", "UNIT PRICE", "TOTAL"));
		for (CartItem item : items) {
			sb.append(String.format("%-12s %-10d $%-11.2f $%-7.2f\n",
				item.getItem().getName(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getSubtotal()));
		}
		sb.append("\n");
		
		// Separador
		sb.append("*".repeat(36)).append("\n");
		
		// Totales
		sb.append(String.format("TOTAL NUMBER OF ITEMS SOLD: %d\n", items.stream().mapToInt(CartItem::getQuantity).sum()));
		sb.append(String.format("SUB-TOTAL: $%.2f\n", subtotal));
		sb.append(String.format("TAX (6.5%%): $%.2f\n", tax));
		sb.append(String.format("TOTAL: $%.2f\n", total));
		sb.append(String.format("CASH: $%.2f\n", cashPayment));
		sb.append(String.format("CHANGE: $%.2f\n", change));
		sb.append("\n");
		
		// Separador
		sb.append("*".repeat(36)).append("\n");
		
		// Ahorro por ser miembro
		if (isMember && memberSavings.compareTo(BigDecimal.ZERO) > 0) {
			sb.append(String.format("YOU SAVED: $%.2f!\n", memberSavings));
		}
		
		return sb.toString();
	}
	
	/**
	 * Saves the receipt to a file
	 */
	public void saveToFile() {
		String fileName = String.format("transaction_%06d_%s.txt",
			transactionNumber,
			dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		
		try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
			writer.print(generateReceiptContent());
			System.out.println("Receipt saved as: " + fileName);
		} catch (IOException e) {
			System.err.println("Error saving receipt: " + e.getMessage());
		}
	}
	
	/**
	 * Displays the receipt in console
	 */
	public void display() {
		System.out.println(generateReceiptContent());
	}
	
	// Getters
	public int getTransactionNumber() { return transactionNumber; }
	public LocalDateTime getDateTime() { return dateTime; }
	public BigDecimal getTotal() { return total; }
	public BigDecimal getChange() { return change; }
} 