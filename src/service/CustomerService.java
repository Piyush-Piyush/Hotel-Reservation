package service;

import model.Customer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CustomerService {

    private static final CustomerService customerService = new CustomerService();

    Map<String, Customer> customers = new HashMap<String, Customer>();

    private CustomerService() {}

    public static CustomerService getInstance() {
        return customerService;
    }

    public void addCustomer(String email, String firstName, String lastName) {
      customers.put(email, new Customer(firstName, lastName, email));
      System.out.println("Customer added successfully.");
    }

    public Customer getCustomer(String customerEmail){
        return customers.get(customerEmail);
    }

    public Collection<Customer> getAllCustomers(){
        return customers.values();
    }
}
