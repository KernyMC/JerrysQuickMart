package com.quickmart.app; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for CartItem class
 */
public class CartItemTest {
    
    private Item taxableItem;
    private Item nonTaxableItem;
    private CartItem taxableCartItem;
    private CartItem nonTaxableCartItem;
    private CartItem memberCartItem;
    
    @BeforeEach
    void setUp() {
        // Taxable item: $5.99 regular, $5.49 member
        taxableItem = new Item("Soda", 10, new BigDecimal("5.99"), new BigDecimal("5.49"), true);
        
        // Non-taxable item: $3.50 regular, $3.25 member
        nonTaxableItem = new Item("Milk", 5, new BigDecimal("3.50"), new BigDecimal("3.25"), false);
        
        taxableCartItem = new CartItem(taxableItem, 3, false); // Regular customer
        nonTaxableCartItem = new CartItem(nonTaxableItem, 2, false); // Regular customer
        memberCartItem = new CartItem(taxableItem, 2, true); // Member customer
    }
    
    @Test
    void testCartItemCreation() {
        assertEquals(taxableItem, taxableCartItem.getItem());
        assertEquals(3, taxableCartItem.getQuantity());
        assertEquals(new BigDecimal("5.99"), taxableCartItem.getUnitPrice());
        assertEquals(new BigDecimal("17.97"), taxableCartItem.getSubtotal()); // 3 * $5.99
    }
    
    	@Test
	void testRegularCustomerPricing() {
		assertEquals(new BigDecimal("5.99"), taxableCartItem.getUnitPrice());
		assertEquals(new BigDecimal("17.97"), taxableCartItem.getSubtotal());
		// El ahorro para regular debe ser 1.50 (3 * (5.99 - 5.49))
		assertEquals(new BigDecimal("1.50"), taxableCartItem.getMemberSavings());
	}
    
    @Test
    void testMemberCustomerPricing() {
        assertEquals(new BigDecimal("5.49"), memberCartItem.getUnitPrice());
        assertEquals(new BigDecimal("10.98"), memberCartItem.getSubtotal()); // 2 * $5.49
        assertEquals(new BigDecimal("1.00"), memberCartItem.getMemberSavings()); // 2 * ($5.99 - $5.49)
    }
    
    @Test
    void testTaxableItemTaxCalculation() {
        // Tax rate is 6.5%
        BigDecimal expectedTax = new BigDecimal("17.97").multiply(new BigDecimal("0.065"));
        assertEquals(expectedTax, taxableCartItem.getTax());
    }
    
    @Test
    void testNonTaxableItemTaxCalculation() {
        assertEquals(BigDecimal.ZERO, nonTaxableCartItem.getTax());
    }
    
    	@Test
	void testMemberTaxableItemTaxCalculation() {
		// Tax on member price: $10.98 * 0.065 = $0.71370 (more precise)
		assertEquals(new BigDecimal("0.71370"), memberCartItem.getTax());
	}
    
    	@Test
	void testToString() {
		String expected = "Soda x3 @ $5,99 = $17,97";
		assertEquals(expected, taxableCartItem.toString());
	}
	
	@Test
	void testMemberToString() {
		String expected = "Soda x2 @ $5,49 = $10,98";
		assertEquals(expected, memberCartItem.toString());
	}
    
    	@Test
	void testZeroQuantity() {
		CartItem zeroItem = new CartItem(taxableItem, 0, false);
		assertEquals(0, zeroItem.getQuantity());
		assertTrue(zeroItem.getSubtotal().compareTo(BigDecimal.ZERO) == 0);
		assertTrue(zeroItem.getTax().compareTo(BigDecimal.ZERO) == 0);
		assertTrue(zeroItem.getMemberSavings().compareTo(BigDecimal.ZERO) == 0);
	}
    
    	@Test
	void testLargeQuantity() {
		CartItem largeItem = new CartItem(taxableItem, 100, false);
		assertEquals(100, largeItem.getQuantity());
		assertEquals(new BigDecimal("599.00"), largeItem.getSubtotal()); // 100 * $5.99
		assertEquals(new BigDecimal("38.93500"), largeItem.getTax()); // $599.00 * 0.065 (more precise)
	}
    
    @Test
    void testMemberSavingsWithNonTaxableItem() {
        CartItem memberNonTaxable = new CartItem(nonTaxableItem, 1, true);
        assertEquals(new BigDecimal("0.25"), memberNonTaxable.getMemberSavings()); // $3.50 - $3.25
        assertEquals(BigDecimal.ZERO, memberNonTaxable.getTax());
    }
} 