package ONLINESHOPPING;

import java.time.LocalDate;
import java.util.*;




import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

    // Main Class
    class OnlineShoppingSystem {
        private static List<Product> products = new ArrayList<>();

        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            // Sample Products
            products.add(new Electronics(101, "Laptop", 1200.00, 24));
            products.add(new Clothing(102, "Shirt", 40.50, "M"));
            products.add(new Grocery(103, "Milk", 3.99, LocalDate.of(2024, 12, 15)));

            Customer customer = null;
            boolean loggedIn = false;

            while (true) {
                try {
                    if (customer == null) {
                        System.out.println("Register a new user:");
                        System.out.print("Enter user ID: ");
                        int userId = scanner.nextInt();
                        scanner.nextLine();  // Consume newline
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        System.out.print("Enter address: ");
                        String address = scanner.nextLine();

                        customer = new Customer(userId, name, email, password);
                        customer.setAddress(address);
                        System.out.println("User registered successfully.");
                    } else if (!loggedIn) {
                        System.out.println("Login:");
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String password = scanner.nextLine();
                        if (customer.login(email, password)) {
                            loggedIn = true;
                            System.out.println("Login successful.");
                        }
                    } else {
                        System.out.println("1. View Products");
                        System.out.println("2. Add Product to Cart");
                        System.out.println("3. Remove Product from Cart");
                        System.out.println("4. View Cart");
                        System.out.println("5. Checkout");
                        System.out.println("6. Logout");
                        System.out.print("Enter your choice: ");
                        int choice = scanner.nextInt();
                        scanner.nextLine();  // Consume newline

                        switch (choice) {
                            case 1:
                                viewProducts();
                                break;
                            case 2:
                                System.out.print("Enter Product ID to add to cart: ");
                                int productId = scanner.nextInt();
                                scanner.nextLine();
                                Product product = findProductById(productId);
                                if (product != null) {
                                    customer.addToCart(product);
                                    System.out.println("Product added to cart.");
                                } else {
                                    System.out.println("Product not found.");
                                }
                                break;
                            case 3:
                                System.out.print("Enter Product ID to remove from cart: ");
                                productId = scanner.nextInt();
                                scanner.nextLine();
                                customer.removeFromCart(productId);
                                System.out.println("Product removed from cart.");
                                break;
                            case 4:
                                customer.viewCart();
                                break;
                            case 5:
                                // Checkout
                                double totalAmount = 0;
                                for (Product p : customer.getCart()) {
                                    totalAmount += p.price;
                                }
                                if (totalAmount > 0) {
                                    PaymentProcessor paymentProcessor = new PaymentProcessor(1, totalAmount);
                                    paymentProcessor.pay(totalAmount);
                                    System.out.println("Thank you for your purchase!");
                                } else {
                                    System.out.println("Your cart is empty. Add items before checkout.");
                                }
                                break;
                            case 6:
                                loggedIn = false;
                                System.out.println("Logged out successfully.");
                                break;
                            default:
                                System.out.println("Invalid choice. Try again.");
                                break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }

        private static void viewProducts() {
            System.out.println("Available Products:");
            for (Product p : products) {
                System.out.println(p.getDetails());
            }
        }

        private static Product findProductById(int productId) {
            for (Product p : products) {
                if (p.getProductId() == productId) {
                    return p;
                }
            }
            return null;
        }
    }

    // User Class
    class User {
        private int userId;
        protected String name;
        private String email;
        private String password;

        public User(int userId, String name, String email, String password) throws RegistrationException {
            if (userId <= 0 || name.isEmpty() || !isValidEmail(email) || !isValidPassword(password)) {
                throw new RegistrationException("Invalid registration details!");
            }
            this.userId = userId;
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public boolean login(String email, String password) {
            return this.email.equals(email) && this.password.equals(password);
        }

        private boolean isValidEmail(String email) {
            return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
        }

        private boolean isValidPassword(String password) {
            return password.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Za-z]).{8,}$");
        }
    }

    // Customer Class
    class Customer extends User {
        private String address;
        private List<Product> cart = new ArrayList<>();

        public Customer(int userId, String name, String email, String password) throws RegistrationException {
            super(userId, name, email, password);
        }

        public void setAddress(String address) throws RegistrationException {
            if (address.isEmpty()) throw new RegistrationException("Address cannot be empty.");
            this.address = address;
        }

        public void addToCart(Product product) throws CartException {
            if (cart.contains(product)) throw new CartException("Product already in cart!");
            cart.add(product);
        }

        public void removeFromCart(int productId) throws CartException {
            Product productToRemove = null;
            for (Product product : cart) {
                if (product.getProductId() == productId) {
                    productToRemove = product;
                    break;
                }
            }
            if (productToRemove == null) {
                throw new CartException("Product with ID " + productId + " not found in the cart.");
            }
            cart.remove(productToRemove);
        }

        public List<Product> getCart() {
            return cart;
        }

        public void displayDetails() {
            System.out.println("Name: " + super.name + ", Address: " + address);
        }

        public void viewCart() {
            if(cart.isEmpty()) {
                System.out.println("Cart is empty.");
            } else {
                for (Product p : cart) {
                    System.out.println(p.getDetails());
                }
            }
        }
    }

    // Product Abstract Class
    abstract class Product {
        private static Set<Integer> productIds = new HashSet<>();
        protected int productId;
        protected String name;
        protected double price;

        public Product(int productId, String name, double price) {
            if (productId <= 0 || productIds.contains(productId)) {
                throw new IllegalArgumentException("Product ID must be unique and positive.");
            }
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty.");
            }
            if (price <= 0) {
                throw new IllegalArgumentException("Price must be positive.");
            }
            this.productId = productId;
            this.name = name;
            this.price = price;
            productIds.add(productId);
        }

        public int getProductId() {
            return productId;
        }

        public abstract String getDetails();
    }

    // Electronics Class
    class Electronics extends Product {
        private int warrantyPeriod;

        public Electronics(int productId, String name, double price, int warrantyPeriod) {
            super(productId, name, price);
            this.warrantyPeriod = warrantyPeriod;
        }

        @Override
        public String getDetails() {
            return "Electronics [ID: " + productId + ", Name: " + name + ", Price: " + price + ", Warranty: " + warrantyPeriod + " months]";
        }
    }

    // Clothing Class
    class Clothing extends Product {
        private String size;

        public Clothing(int productId, String name, double price, String size) {
            super(productId, name, price);
            if (!size.matches("S|M|L|XL")) {
                throw new IllegalArgumentException("Invalid size. Must be one of: S, M, L, XL.");
            }
            this.size = size;
        }

        @Override
        public String getDetails() {
            return "Clothing [ID: " + productId + ", Name: " + name + ", Price: " + price + ", Size: " + size + "]";
        }
    }


    // Grocery Class
    class Grocery extends Product {
        private LocalDate expiryDate;

        public Grocery(int productId, String name, double price, LocalDate expiryDate) {
            super(productId, name, price);
            if (expiryDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Expiry date must be in the future.");
            }
            this.expiryDate = expiryDate;
        }

        @Override
        public String getDetails() {
            return "Grocery [ID: " + productId + ", Name: " + name + ", Price: " + price + ", Expiry: " + expiryDate + "]";
        }
    }

    // Payment Interface
    interface Payment {
        boolean pay(double amount) throws PaymentException;
    }

    // PaymentProcessor Class
    class PaymentProcessor implements Payment {
        private int paymentId;
        private double amount;

        public PaymentProcessor(int paymentId, double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Payment amount must be positive.");
            }
            this.paymentId = paymentId;
            this.amount = amount;
        }

        public double getAmount() {
            return amount;
        }

        @Override
        public boolean pay(double amount) throws PaymentException {
            if (amount <= 0) throw new PaymentException("Payment amount must be positive.");
            System.out.println("Processing payment of $" + amount + "...");
            return true;
        }
    }

    // Exceptions
    class RegistrationException extends Exception {
        public RegistrationException(String message) {
            super(message);
        }
    }

    class CartException extends Exception {
        public CartException(String message) {
            super(message);
        }
    }

    class PaymentException extends Exception {
        public PaymentException(String message) {
            super(message);
        }
    }
// Grocery Class class Grocery extends Product { private LocalDate expiryDate; public Grocery(int productId, String name, double price, LocalDate expiryDate) { super(productId, name, price); if (expiryDate.isBefore(LocalDate.now())) { throw new IllegalArgumentException("Expiry date must be in the future."); } this.expiryDate = expiryDate; } @Override public String getDetails() { return "Grocery [ID: " + productId + ", Name: " + name + ", Price: " + price + ", Expiry: " + expiryDate + "]"; } } // Payment Interface interface Payment { boolean pay(double amount) throws PaymentException; } // PaymentProcessor Class class PaymentProcessor implements Payment { private int paymentId; private double amount; public PaymentProcessor(int paymentId, double amount) { if (amount <= 0) { throw new IllegalArgumentException("Payment amount must be positive."); } this.paymentId = paymentId; this.amount = amount; } public double getAmount() { return amount; } @Override public boolean pay(double amount) throws PaymentException { if (amount <= 0) throw new PaymentException("Payment amount must be positive."); System.out.println("Processing payment of $" + amount + "..."); return true; } } // Exceptions class RegistrationException extends Exception { public RegistrationException(String message) { super(message); } } class CartException extends Exception { public CartException(String message) { super(message); } } class PaymentException extends Exception { public PaymentException(String message) { super(message); } }

