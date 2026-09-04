package com.myapp.Banking;

// encapsulation - restricts access to object's fields, and provides controlled access to these via getters/setters

public class BankAccount {

    private String accountNumber;
    private double balance;
    
    // contructor - a special method used to initialize a newly created object
    public BankAccount(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        if(initialBalance > 0){
           this.balance = initialBalance; 
        } 
        
    }

    public String getAccountNumber() {
        return "The account number is: " + accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    // setter to affect change on the balance class member variable 
    public void deposit(double amount) {
        if(amount > 0) {
            balance += amount;
        }
    }

    public void withdraw(double amount) {
        if(amount > 0 && amount <= balance) {
            balance -= amount;
        }
    }

}
