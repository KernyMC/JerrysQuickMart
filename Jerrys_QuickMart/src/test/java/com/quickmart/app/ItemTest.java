package com.quickmart.app; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

/**
 * Unit tests for Item class
 */
public class ItemTest {
    
    private Item item;
    
    @BeforeEach
    void setUp() {
        item = new Item("Test Product", 10, new BigDecimal("5.99"), new BigDecimal("5.49"), true);
    }
    
    @Test
    void testItemCreation() {
        assertEquals("Test Product", item.getName());
        assertEquals(10, item.getQuantity());
        assertEquals(new BigDecimal("5.99"), item.getRegularPrice());
        assertEquals(new BigDecimal("5.49"), item.getMemberPrice());
        assertTrue(item.isTaxable());
    }
    
    	@Test
	void testHasStock() {
		assertTrue(item.hasStock(5));
		assertTrue(item.hasStock(10));
		assertFalse(item.hasStock(11));
		assertTrue(item.hasStock(0)); // hasStock allows 0 quantity
	}
    
    @Test
    void testReduceStock() {
        item.reduceStock(3);
        assertEquals(7, item.getQuantity());
        
        item.reduceStock(7);
        assertEquals(0, item.getQuantity());
    }
    
    @Test
    void testSetQuantity() {
        item.setQuantity(15);
        assertEquals(15, item.getQuantity());
        
        item.setQuantity(0);
        assertEquals(0, item.getQuantity());
    }
    
    	@Test
	void testToString() {
		String expected = "Test Product: 10, $5,99, $5,49, Taxable";
		assertEquals(expected, item.toString());
	}
    
    @Test
    void testNonTaxableItem() {
        Item nonTaxableItem = new Item("Bread", 5, new BigDecimal("2.50"), new BigDecimal("2.25"), false);
        assertFalse(nonTaxableItem.isTaxable());
        assertTrue(nonTaxableItem.toString().contains("Tax-Exempt"));
    }
} 