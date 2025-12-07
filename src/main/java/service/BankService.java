package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.*;

public interface BankService extends Remote {
    
    // --- Customer Operations ---
    Customer createCustomer(Customer c) throws RemoteException;
    Customer updateCustomer(Customer c) throws RemoteException;
    Customer deleteCustomer(Customer c) throws RemoteException;
    Customer findCustomerById(int id) throws RemoteException;
    List<Customer> findAllCustomers() throws RemoteException;

    // === Account Operations ===
    Account createAccount(Account a) throws RemoteException;
    Account updateAccount(Account a) throws RemoteException;
    Account deleteAccount(Account a) throws RemoteException;
    Account findAccountById(String id) throws RemoteException;
    List<Account> findAllAccounts() throws RemoteException;

    // === Transaction Operations ===
    Transaction createTransaction(Transaction t) throws RemoteException;
    List<Transaction> findAllTransactions() throws RemoteException;

    // === Loan Operations ===
    Loan createLoan(Loan l) throws RemoteException;
    Loan updateLoan(Loan l) throws RemoteException;
    Loan deleteLoan(Loan l) throws RemoteException;
    Loan findLoanById(int id) throws RemoteException;
    List<Loan> findAllLoans() throws RemoteException;

    // === Card Operations ===
    Card createCard(Card c) throws RemoteException;
    Card updateCard(Card c) throws RemoteException;
    Card deleteCard(Card c) throws RemoteException;
    Card findCardById(String id) throws RemoteException;
    List<Card> findAllCards() throws RemoteException;

    // === LoanRepayment Operations ===
    LoanRepayment createLoanRepayment(LoanRepayment lr) throws RemoteException;
    LoanRepayment updateLoanRepayment(LoanRepayment lr) throws RemoteException;
    LoanRepayment deleteLoanRepayment(LoanRepayment lr) throws RemoteException;
    LoanRepayment findLoanRepaymentById(int id) throws RemoteException;
    List<LoanRepayment> findAllLoanRepayments() throws RemoteException;
    
    // === RiskProfile Operations ===
    RiskProfile createRiskProfile(RiskProfile rp) throws RemoteException;
    RiskProfile updateRiskProfile(RiskProfile rp) throws RemoteException;
    RiskProfile deleteRiskProfile(RiskProfile rp) throws RemoteException;
    RiskProfile findRiskProfileById(int id) throws RemoteException;
    List<RiskProfile> findAllRiskProfiles() throws RemoteException;
}