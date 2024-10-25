package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCustomer extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField nameTxtFld, surnameTxtFld, emailTxtFld, mobileNumTxtFld;
    private JCheckBox loyaltyCheckFld;
    private JButton clearButton, saveButton, exitButton;
    private VectorCustomer vc;
    
    public AddCustomer(){
        super("Adding a customer");
        this.setLayout(new BorderLayout());
        vc = new VectorCustomer();
        vc.readFromFile();
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Adding a Customer");
        titleLabel.setFont(new Font ("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.red);
        north.add(titleLabel);
        
        //West and east panels
        west = new JPanel();
        east = new JPanel();
        this.add(west, BorderLayout.WEST); 
        this.add(east, BorderLayout.EAST);
        westLabel = new JLabel("          ");//10 spaces
        eastLabel = new JLabel("          ");//10 spaces
        west.add(westLabel);
        east.add(eastLabel);
        
        //Center panel
        center = new JPanel();
        center.setLayout(new GridLayout(6,0,0,20));
        this.add(center,BorderLayout.CENTER);
    
        mobileNumTxtFld = new JTextField();
        nameTxtFld = new JTextField();
        surnameTxtFld = new JTextField();
        emailTxtFld = new JTextField();
        loyaltyCheckFld = new JCheckBox();
      
        center.add(createLabel("Mobile number"));
        center.add(mobileNumTxtFld);
        center.add(createLabel("Name"));
        center.add(nameTxtFld);
        center.add(createLabel("Surname"));
        center.add(surnameTxtFld);        
        center.add(createLabel("Email"));
        center.add(emailTxtFld);
        center.add(createLabel("Loyalty Scheme"));
        center.add(loyaltyCheckFld);
        
        //south panel
        south = new JPanel();
        this.add(south,BorderLayout.SOUTH);
        clearButton = new JButton ("Clear");
        south.add(clearButton);
        clearButton.addActionListener(this);
        saveButton = new JButton ("Save");
        south.add(saveButton);
        saveButton.addActionListener(this);
        exitButton = new JButton ("Exit");
        south.add(exitButton);
        exitButton.addActionListener(this);
        
        this.setSize(800,600);
        this.setLocation(50,100);
        this.setVisible(true);
    }
    
    private JLabel createLabel(String title){
        return new JLabel(title);
    }
    
    private void clearFields(){
        nameTxtFld.setText("");
        surnameTxtFld.setText("");
        emailTxtFld.setText("");
        mobileNumTxtFld.setText("");
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if (nameTxtFld.getText().equals("") || surnameTxtFld.getText().equals("") || emailTxtFld.getText().equals("") || mobileNumTxtFld.getText().equals("")) {
            flag = true;
        }
        return flag;
    }
    
    private boolean validateMobileNum(String mobileNum){
        boolean flag = false;
        //Matcher matcher = Pattern.compile("(99/79)?[0-9]{8}").matcher(mobileNum);; 
 
        //if(matcher.find())
        if (mobileNum.matches("[79][0-9]{7}"))
        {
            flag = true;
        }
        else
        {
            flag = false;
        }
        return flag;
    } 
    
    private boolean validateEmail(String email){
        boolean flag = false;
        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(email);
        if(matcher.find())
        {
            flag = false;
        }
        else
        {
            flag = true;
        }
        return flag;
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
        
        if (event.getSource() == clearButton){
            clearFields();
        }
        
        if(event.getSource() == saveButton){
            if(validateEmptyFields() == true){
                JOptionPane.showMessageDialog(null, "You have empty fields!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
            }
            else if(vc.searchCustomerBymobileNum(mobileNumTxtFld.getText()) == true){
                JOptionPane.showMessageDialog(null, "Customer id already exists", "Customer id check", JOptionPane.ERROR_MESSAGE);
            }
            else if(validateEmail(emailTxtFld.getText()) == true){
                JOptionPane.showMessageDialog(null, "Invalid email!", "Email validation check", JOptionPane.ERROR_MESSAGE);
            }
            else if(validateMobileNum(mobileNumTxtFld.getText()) == false){
                JOptionPane.showMessageDialog(null, "Invalid mobile!", "Mobile number validation check", JOptionPane.ERROR_MESSAGE);
            }
            else{
                //save client
                //String id, String name, String surname, int age, String email, String mobileNum, String favCar, int rentalTimes
                Customer tempCustomer = new Customer(nameTxtFld.getText(), surnameTxtFld.getText(), mobileNumTxtFld.getText(),emailTxtFld.getText());
                tempCustomer.setPoints(0);                  //initialise the loyality points of a new customer registration
                if (loyaltyCheckFld.isSelected())
                    tempCustomer.setLoyaltyFlag(true);
                else
                    tempCustomer.setLoyaltyFlag(false);
                    
                vc.addCustomer(tempCustomer);
                vc.saveToFile();
                JOptionPane.showMessageDialog(null, "Customer has been added successfully!", "Adding a customer", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            }
        }
    }
}