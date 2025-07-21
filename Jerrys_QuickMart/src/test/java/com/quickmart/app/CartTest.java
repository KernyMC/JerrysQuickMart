package com.quickmart.app; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for Cart class
 */
public class CartTest {
    
    private Cart regularCart;
    private Cart memberCart;
    private Item taxableItem;
    private Item nonTaxableItem;
    
    @BeforeEach
    void setUp() {
        regularCart = new Cart(false);
        memberCart = new Cart(true);
        
        // Taxable item: $5.99 regular, $5.49 member
        taxableItem = new Item("Soda", 10, new BigDecimal("5.99"), new BigDecimal("5.49"), true);
        
        // Non-taxable item: $3.50 regular, $3.25 member
        nonTaxableItem = new Item("Milk", 5, new BigDecimal("3.50"), new BigDecimal("3.25"), false);
    }
    
    @Test
    void testCartCreation() {
        assertTrue(regularCart.isEmpty());
        assertEquals(0, regularCart.getTotalItems());
        assertEquals(BigDecimal.ZERO, regularCart.getSubtotal());
        assertEquals(BigDecimal.ZERO, regularCart.getTotalTax());
        assertEquals(BigDecimal.ZERO, regularCart.getTotal());
    }
    
    	@Test
	void testAddItemToRegularCart() {
		assertTrue(regularCart.addItem(taxableItem, 2));
		assertEquals(2, regularCart.getTotalItems());
		assertEquals(new BigDecimal("11.98"), regularCart.getSubtotal()); // 2 * $5.99
		assertEquals(new BigDecimal("0.77870"), regularCart.getTotalTax()); // 6.5% of $11.98 (more precise)
		assertEquals(new BigDecimal("12.75870"), regularCart.getTotal());
		assertEquals(BigDecimal.ZERO, regularCart.getTotalMemberSavings()); // Regular customers don't get member savings
	}
    
    	@Test
	void testAddItemToMemberCart() {
		assertTrue(memberCart.addItem(taxableItem, 2));
		assertEquals(2, memberCart.getTotalItems());
		assertEquals(new BigDecimal("10.98"), memberCart.getSubtotal()); // 2 * $5.49
		assertEquals(new BigDecimal("0.71370"), memberCart.getTotalTax()); // 6.5% of $10.98 (more precise)
		assertEquals(new BigDecimal("11.69370"), memberCart.getTotal());
		assertEquals(new BigDecimal("1.00"), memberCart.getTotalMemberSavings()); // 2 * ($5.99 - $5.49)
	}
    
    @Test
    void testAddNonTaxableItem() {
        assertTrue(regularCart.addItem(nonTaxableItem, 3));
        assertEquals(3, regularCart.getTotalItems());
        assertEquals(new BigDecimal("10.50"), regularCart.getSubtotal()); // 3 * $3.50
        assertEquals(BigDecimal.ZERO, regularCart.getTotalTax()); // No tax
        assertEquals(new BigDecimal("10.50"), regularCart.getTotal());
    }
    
    @Test
    void testAddItemInsufficientStock() {
        assertFalse(regularCart.addItem(taxableItem, 15)); // Only 10 in stock
        assertTrue(regularCart.isEmpty());
    }
    
    @Test
    void testAddDuplicateItem() {
        assertTrue(regularCart.addItem(taxableItem, 2));
        assertTrue(regularCart.addItem(taxableItem, 3)); // Should merge to 5 total
        assertEquals(5, regularCart.getTotalItems());
        assertEquals(new BigDecimal("29.95"), regularCart.getSubtotal()); // 5 * $5.99
    }
    
    @Test
    void testRemoveItem() {
        regularCart.addItem(taxableItem, 5);
        assertTrue(regularCart.removeItem("Soda", 2));
        assertEquals(3, regularCart.getTotalItems());
        
        assertTrue(regularCart.removeItem("Soda", 3)); // Remove all
        assertTrue(regularCart.isEmpty());
    }
    
    @Test
    void testRemoveNonExistentItem() {
        assertFalse(regularCart.removeItem("NonExistent", 1));
    }
    
    @Test
    void testClearCart() {
        regularCart.addItem(taxableItem, 2);
        regularCart.addItem(nonTaxableItem, 1);
        
        regularCart.clear();
        assertTrue(regularCart.isEmpty());
        assertEquals(0, regularCart.getTotalItems());
        assertEquals(BigDecimal.ZERO, regularCart.getSubtotal());
    }
    
    	@Test
	void testMixedItemsCalculation() {
		regularCart.addItem(taxableItem, 2); // $11.98 subtotal, $0.77870 tax
		regularCart.addItem(nonTaxableItem, 1); // $3.50 subtotal, $0.00 tax
		
		assertEquals(3, regularCart.getTotalItems());
		assertEquals(new BigDecimal("15.48"), regularCart.getSubtotal()); // $11.98 + $3.50
		assertEquals(new BigDecimal("0.77870"), regularCart.getTotalTax()); // Only taxable items
		assertEquals(new BigDecimal("16.25870"), regularCart.getTotal()); // $15.48 + $0.77870
	}
    
    @Test
    void testMemberSavingsCalculation() {
        memberCart.addItem(taxableItem, 2); // $10.98 subtotal, savings $1.00
        memberCart.addItem(nonTaxableItem, 1); // $3.25 subtotal, savings $0.25
        
        assertEquals(new BigDecimal("14.23"), memberCart.getSubtotal()); // $10.98 + $3.25
        assertEquals(new BigDecimal("1.25"), memberCart.getTotalMemberSavings()); // $1.00 + $0.25
    }
} 