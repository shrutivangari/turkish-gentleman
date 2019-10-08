package com.shruti.turkishgentleman.clients.producer;

import com.github.javafaker.Faker;
import com.github.javafaker.Finance;
import com.github.javafaker.Name;
import com.shruti.turkishgentleman.model.Customer;
import com.shruti.turkishgentleman.model.Purchase;
import com.shruti.turkishgentleman.model.Store;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Component
public class DataTypes {

    public static final int NUMBER_UNIQUE_CUSTOMERS = 100;
    public static final int NUMBER_UNIQUE_STORES = 15;
    private static Faker dateFaker = new Faker();

    private static Supplier<Date> timestampGenerator = () -> dateFaker.date().past(15, TimeUnit.MINUTES, new Date());

    private DataTypes() {
    }

    public static List<Purchase> generatePurchases(int numberIterations) {
        List<Purchase> purchases = new ArrayList<>();

        Faker faker = new Faker();
        List<Customer> customers = generateCustomers(NUMBER_UNIQUE_CUSTOMERS);
        List<Store> stores = generateStores();

        Random random = new Random();
        for(int i=0;i<numberIterations;i++) {
            String itemPurchased = faker.commerce().productName();
            int quantity = faker.number().numberBetween(1, 5);
            double price = Double.parseDouble(faker.commerce().price(4.00, 295.00));
            Date purchaseDate = timestampGenerator.get();

            Customer customer = customers.get(random.nextInt(NUMBER_UNIQUE_CUSTOMERS));
            Store store = stores.get(random.nextInt(NUMBER_UNIQUE_STORES));

            Purchase purchase = Purchase.builder()
                    .creditCardNumber(customer.getCreditCardNumber())
                    .customerId(customer.getCustomerId())
                    .department(store.getDepartment())
                    .employeeId(store.getEmployeeId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getFirstName())
                    .itemPurchased(itemPurchased)
                    .quanity(quantity)
                    .price(price)
                    .purchaseDate(purchaseDate)
                    .zipCode(store.getZipCode())
                    .storeId(store.getStoreId())
                    .build();

            if(purchase.getDepartment().toLowerCase().contains("electronics")) {
                Purchase cafePurchase = generateCafePurchase(purchase, faker);
                purchases.add(cafePurchase);
            }

            purchases.add(purchase);
        }

        return purchases;
    }

    private static Purchase generateCafePurchase(Purchase purchase, Faker faker) {
        Date date = purchase.getPurchaseDate();
        Instant adjusted = date.toInstant().minus(faker.number().numberBetween(5, 18), ChronoUnit.MINUTES);
        Date cafeDate = Date.from(adjusted);

        return Purchase.builder(purchase).department("Coffee")
                .itemPurchased(faker.options().option("Mocha", "Mild Roast", "Red-Eye", "Dark Roast"))
                .price(Double.parseDouble(faker.commerce().price(3.00, 6.00))).quanity(1).purchaseDate(cafeDate).build();

    }

    private static List<Store> generateStores() {
        List<Store> stores = new ArrayList<>(NUMBER_UNIQUE_STORES);
        Faker faker = new Faker();
        for (int i = 0; i < NUMBER_UNIQUE_STORES; i++) {
            String department = (i % 5 == 0) ? "Electronics" : faker.commerce().department();
            String employeeId = Long.toString(faker.number().randomNumber(5, false));
            String zipCode = faker.options().option("47197-9482", "97666", "113469", "334457");
            String storeId = Long.toString(faker.number().randomNumber(6, true));
            if (i + 1 == NUMBER_UNIQUE_STORES) {
                employeeId = "000000"; //Seeding id for employee security check
            }
            stores.add(new Store(employeeId, zipCode, storeId, department));
        }

        return stores;
    }

    public static List<Customer> generateCustomers(int numberCustomers) {
        List<Customer> customers = new ArrayList<>(numberCustomers);
        Faker faker = new Faker();
        List<String> creditCards = generateCreditCardNumbers(numberCustomers);
        for (int i = 0; i < numberCustomers; i++) {
            Name name = faker.name();
            String creditCard = creditCards.get(i);
            String customerId = faker.idNumber().valid();
            customers.add(new Customer(name.firstName(), name.lastName(), customerId, creditCard));
        }
        return customers;
    }

    private static List<String> generateCreditCardNumbers(int numberCards) {
        int counter = 0;
        Pattern visaMasterCardAmex = Pattern.compile("(\\d{4}-){3}\\d{4}");
        List<String> creditCardNumbers = new ArrayList<>(numberCards);
        Finance finance = new Faker().finance();
        while (counter < numberCards) {
            String cardNumber = finance.creditCard();
            if (visaMasterCardAmex.matcher(cardNumber).matches()) {
                creditCardNumbers.add(cardNumber);
                counter++;
            }
        }
        return creditCardNumbers;
    }

}
