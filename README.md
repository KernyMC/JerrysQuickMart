# Jerry's Quick Mart - Point of Sale System

## Solution Overview
This solution is written in pure Java (no external libraries except JUnit for testing). It implements a robust, object-oriented Point of Sale (POS) system for Jerry's Quick Mart, supporting inventory management, cart operations, and receipt generation. The code is clean, modular, and easily extendible, following best OOP practices.

## Assumptions
- All transactions are in cash (no credit/debit support).
- The inventory file supports both US (`$3.75`) and European (`$3,75`) price formats.
- Tax is 6.5% for taxable items.
- Receipts are saved as `.txt` files with transaction number and date.
- Inventory is updated after each purchase to prevent overselling.
- No external libraries are used except for JUnit (testing only).
- The application is run from the project directory containing `pom.xml` and `inventory.txt`.

## Features
- Select customer type: Rewards Member or Regular Customer
- Add items to cart
- Remove individual items from cart (with empty cart option)
- View cart (including totals)
- Checkout and print receipt (as .txt file)
- Cancel transaction
- Inventory is loaded from a text file and updated after each checkout
- All transactions are in cash
- Product ID system for easier selection
- Member savings calculation and display
- Robust error handling for invalid input and stock
- Cart can be cleared entirely
- Transaction numbering is persistent across sessions
- Unit tests for all core logic (JUnit 5)

## Inventory Input Format
The inventory is provided in a text file (`inventory.txt`), with one item per line:

```
<item>: <quantity>, <regular price>, <member price>, <tax status>
```

**Example:**
```
Milk: 5, $3.75, $3.50, Tax-Exempt
Red Bull: 10, $4.30, $4.00, Taxable
Flour: 1, $3.10, $2.75, Tax-Exempt
```

## Sample Output (Receipt)
A receipt is generated for each transaction, named as `transaction_<number>_<date>.txt`:

```
December 8, 2016
TRANSACTION: 000001

ITEM       QUANTITY   UNIT PRICE   TOTAL
Milk       2          $3.50        $7.00
Red Bull   3          $4.00        $8.00
Flour      1          $2.75        $2.75

************************************
TOTAL NUMBER OF ITEMS SOLD: 6
SUB-TOTAL: $17.75
TAX (6.5%): $0.52
TOTAL: $18.27
CASH: $20.00
CHANGE: $1.73

************************************
YOU SAVED: $1.75!
```

After checkout, the inventory is updated and saved:
```
Milk: 3, $3.75, $3.50, Tax-Exempt
Red Bull: 7, $4.30, $4.00, Taxable
Flour: 0, $3.10, $2.75, Tax-Exempt
```

## How to Run the Program

### 1. Run the program with Maven
From the project folder (`Jerrys_QuickMart`):
```sh
mvn clean compile
mvn exec:java
```

### 2. Run unit tests
From the same folder:
```sh
mvn test
```

### 3. Run the program as executable JAR
First, generate the executable JAR:
```sh
mvn clean package
```
This will create the file `target/Jerrys_QuickMart-1.0-SNAPSHOT.jar`.

Then, run the program with:
```sh
java -jar target/Jerrys_QuickMart-1.0-SNAPSHOT.jar
```

### 4. Run the Windows batch file (.bat)
A Windows batch file is provided for easy execution:
- Double-click `JerrysQuickMart.bat` to run the application
- The batch file will automatically check for Java installation and required files
- No command line knowledge required

**Note:**
- The `inventory.txt` file must be in the same folder as the batch file.
- Java 17 or later must be installed on the system.

---

## How does the program work?

Jerry's Quick Mart POS is a simple, robust, and fully interactive console application. The user selects the customer type (Rewards Member or Regular), then can add or remove products from the cart, view totals, and checkout. The inventory is always updated after each purchase, and receipts are generated as .txt files. All data is persisted between sessions (inventory and transaction counter).

### General Flow:
1. The user selects the customer type.
2. The main menu allows viewing inventory, adding/removing items, viewing the cart, clearing the cart, checking out, or cancelling.
3. At checkout, the program calculates totals, tax, and member savings, then saves a receipt and updates the inventory.
4. The process can be repeated for new transactions.

## Project Structure

- `Item`: Represents a product in the store, with prices, stock, and tax status.
- `CartItem`: Represents an item in the shopping cart, with quantity and price logic.
- `Cart`: Manages the shopping cart, calculations, and inventory updates.
- `Inventory`: Loads and saves inventory from/to a text file, parses prices and stock.
- `Receipt`: Generates and saves transaction receipts as .txt files.
- `TransactionManager`: Handles transaction numbering and coordinates the transaction process.
- `MainApp`: The main user interface and application flow (console-based).

All logic is modular, clean, and easy to extend. The code is written in pure Java, with JUnit tests for all core logic.

### Example Input Data (inventory.txt)
```
Milk: 5, $3.75, $3.50, Tax-Exempt
Red Bull: 10, $4.30, $4.00, Taxable
Flour: 1, $3.10, $2.75, Tax-Exempt
```

### Example Main Menu (Console)
```
=== MAIN MENU ===
1. View inventory
2. Add item to cart
3. View cart
4. Remove item from cart
5. Clear cart
6. Checkout and Print receipt
7. Cancel transaction
8. Exit
Select an option: _
```

### Example Output (Receipt)
```
December 8, 2016
TRANSACTION: 000001

ITEM         QUANTITY   UNIT PRICE   TOTAL   
Milk         2          $3.50        $7.00   
Red Bull     3          $4.00        $12.00  
Flour        1          $2.75        $2.75   

************************************
TOTAL NUMBER OF ITEMS SOLD: 6
SUB-TOTAL: $21.75
TAX (6.5%): $0.78
TOTAL: $22.53
CASH: $25.00
CHANGE: $2.47

************************************
YOU SAVED: $1.75!
```


## Object Oriented Design
- Each class has a single responsibility (SRP):
  - `Item`: Product entity
  - `CartItem`: Cart item with calculations
  - `Cart`: Shopping cart management
  - `Inventory`: Inventory persistence and loading
  - `Receipt`: Receipt formatting and generation
  - `TransactionManager`: Transaction numbering
  - `MainApp`: User interface and flow
- All logic is encapsulated and easily extendible.
- The code is modular, clean, and ready for real-world use or further extension.


