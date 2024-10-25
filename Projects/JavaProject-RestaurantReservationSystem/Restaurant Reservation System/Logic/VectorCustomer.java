package Logic;
import java.util.*;
import java.io.*;
import javax.swing.*;

public class VectorCustomer{
   private Vector <Customer> customers;
   
   public VectorCustomer(){
       customers = new Vector<Customer>();
    }
    //add a customer
   public void addCustomer(Customer customer){
       customers.add(customer);
    }
    //remove a customer
   public void removeCustomer(Customer customer){
       customers.remove(customer);
    }
    //view all customers
   public void viewAllCustomers(){
       for(Customer tempCustomer: customers){
           System.out.println(tempCustomer.toString());
        }
    }
    //checks whether a customer exists
   public boolean searchCustomerBymobileNum(String mobileNum){
        boolean flag = false;
        for (Customer tempCustomer: customers){
            if(tempCustomer.getmobileNum().equals(mobileNum)){
                return true;
            }
        }
        return flag;
    }
    
    //access customer by mobile number
   public Customer accessCustomerBymobileNum(String mobileNum){
        Customer foundCustomer = new Customer();
        for(Customer tempCustomer: customers){
            if(tempCustomer.getmobileNum().equals(mobileNum)){
                return tempCustomer;
            }
        }
        return foundCustomer;
    }
    
    public void saveToFile() {
        try {
            File f = new File("customersFile.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customers);
            oos.flush();
            oos.close();
        } catch(IOException ioe) {
            JOptionPane.showMessageDialog(null, "Cannot write to file", "Writing file check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void readFromFile(){
        try{
            File f = new File("customersFile.obj");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            customers = (Vector<Customer>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException fnfe){
            JOptionPane.showMessageDialog(null, "File not found!", "File not found check", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe){
            JOptionPane.showMessageDialog(null, "Cannot read from file", "Reading file check", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException fnfe){
            JOptionPane.showMessageDialog(null, "Customer class not found", "Customer class check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public int getSize(){
        return customers.size();
    }
    
    public Customer getCustomerByIndex(int i){
        Customer tempCustomer = new Customer();
        if(i < getSize()){
            tempCustomer = customers.get(i);
        }
        return tempCustomer;
    }
    
    public void sortCustomers(){ //bubble sort method to sort by surname
        Customer nextCustomer = new Customer();
        Customer currentCustomer = new Customer();
        Customer tempCustomer = new Customer();
        for(int i = 0; i < getSize(); i++){
            for(int j = 0; j < getSize()-i-1; j++){
                currentCustomer = customers.elementAt(j);
                nextCustomer = customers.elementAt(j+1);
                if(currentCustomer.getSurname().compareTo(nextCustomer.getSurname()) > 0){
                    tempCustomer = currentCustomer;
                    customers.setElementAt(nextCustomer, j);
                    customers.setElementAt(tempCustomer, j+1);
                }
            }
        }
    }
}
