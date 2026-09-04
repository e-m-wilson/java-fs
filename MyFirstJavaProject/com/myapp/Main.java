package com.myapp;

public class Main {

    // 1. Create our own custom exception
    static class InsufficientFundsException extends Exception {

        public InsufficientFundsException(String message) {
            super(message);
        }
    }

    // 2. Method that can throw our custom exception
    public static void withdraw(double balance, double amount)
            throws InsufficientFundsException {

        if (amount > balance) {
            throw new InsufficientFundsException(
                "Not enough money. Your balance is $" + balance
            );
        }

        System.out.println(
            "Withdrawal successful. Remaining balance: $"
            + (balance - amount)
        );
    }

    // 3. Main method
    public static void main(String[] args) {

        double balance = 1000.00;

        try {
            System.out.println("Current balance: $" + balance);

            withdraw(balance, 1200.00);
        }
        catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed.");
            System.out.println(e.getMessage());
        }

        System.out.println("Program finished.");
    }
}
