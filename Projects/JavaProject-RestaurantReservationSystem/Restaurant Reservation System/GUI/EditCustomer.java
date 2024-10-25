package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditCustomer extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectCustomerPanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField nameTxtFld, surnameTxtFld, emailTxtFld, mobileNumTxtFld;
    private JCheckBox loyaltyCheckFld;
    private JButton updateButton, deleteButton, exitButton, 
    viewCustomersButton, selectCustomerButton;
    private VectorCustomer vc;
    
    public EditCustomer(){
        super("Editing a customer");
        this.setLayout(new BorderLayout());
        vc = new VectorCustomer();
        vc.readFromFile();
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Editing a Customer");
        titleLabel.setFont(new Font ("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.magenta);
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
        center.setLayout(new GridLayout(9,2,0,20));
        this.add(center,BorderLayout.CENTER);
       
        mobileNumTxtFld = new JTextField();
        nameTxtFld = new JTextField();
        surnameTxtFld = new JTextField();
        emailTxtFld = new JTextField();
        loyaltyCheckFld = new JCheckBox();
        
        center.add(createLabel("Mobile number"));
        center.add(mobileNumTxtFld);
        center.add(createLabel(""));
        selectCustomerPanel = new JPanel();
        selectCustomerButton = new JButton("Select");
        viewCustomersButton = new JButton ("View");
        selectCustomerPanel.add(selectCustomerButton);
        selectCustomerButton.addActionListener(this);
        selectCustomerPanel.add(viewCustomersButton);
        viewCustomersButton.addActionListener(this);
        center.add(selectCustomerPanel);
        
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
        updateButton = new JButton ("Update");
        south.add(updateButton);
        updateButton.addActionListener(this);
        deleteButton = new JButton ("Delete");
        south.add(deleteButton);
        deleteButton.addActionListener(this);
        exitButton = new JButton ("Exit");
        south.add(exitButton);
        exitButton.addActionListener(this);
        
        disableButtons();
        this.setSize(800,600);
        this.setLocation(50,100);
        this.setVisible(true);
    }
    
    private JLabel createLabel(String title){
        return new JLabel(title);
    }
    
    private void clearFields(){
        mobileNumTxtFld.setText("");
        nameTxtFld.setText("");
        surnameTxtFld.setText("");
        emailTxtFld.setText("");
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if(mobileNumTxtFld.getText().equals("") || nameTxtFld.getText().equals("") || surnameTxtFld.getText().equals("") || emailTxtFld.getText().equals("")) {
            flag = true;
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
    
    public void disableButtons(){
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    public void enableButtons(){
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }
    
    public void disablePanelButtons(){
        selectCustomerButton.setEnabled(false);
        viewCustomersButton.setEnabled(false);
    }
    
    public void enablePanelButtons(){
        selectCustomerButton.setEnabled(true);
        viewCustomersButton.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
        
        if (event.getSource() == viewCustomersButton){
            ViewCustomers viewCustomers = new ViewCustomers();
        }
       
        if (event.getSource() == selectCustomerButton){
            if (vc.searchCustomerBymobileNum(mobileNumTxtFld.getText()) == false){
                JOptionPane.showMessageDialog(null, "Customer does not exist!", "Customer check", JOptionPane.ERROR_MESSAGE);
            } else {
                Customer tempCustomer = vc.accessCustomerBymobileNum(mobileNumTxtFld.getText());
                nameTxtFld.setText(tempCustomer.getName());
                surnameTxtFld.setText(tempCustomer.getSurname());
                emailTxtFld.setText(tempCustomer.geteMail());
                //mobileNumTxtFld.setText(tempCustomer.getmobileNum());
                loyaltyCheckFld.setSelected(tempCustomer.getLoyaltyFlag());
                enableButtons();
                disablePanelButtons();
                mobileNumTxtFld.setEnabled(false);
            }
        }
        
        if(event.getSource() == updateButton){
            if(validateEmptyFields() == true){
                JOptionPane.showMessageDialog(null, "You have empty fields!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
            }
           
            else if(validateEmail(emailTxtFld.getText()) == true){
                JOptionPane.showMessageDialog(null, "Invalid email!", "Email validation check", JOptionPane.ERROR_MESSAGE);
            }
            else if(validateMobileNum(mobileNumTxtFld.getText()) == false){
                JOptionPane.showMessageDialog(null, "Invalid mobile!", "Mobile number validation check", JOptionPane.ERROR_MESSAGE);
            }
            else{
                //update customer details
                //String name, String surname, String email, String mobileNum
                Customer tempCustomer = vc.accessCustomerBymobileNum(mobileNumTxtFld.getText());
                tempCustomer.setName(nameTxtFld.getText());
                tempCustomer.setSurname(surnameTxtFld.getText());
                tempCustomer.seteMail(emailTxtFld.getText());               
                //tempCustomer.setmobileNum(mobileNumTxtFld.getText());
                if (loyaltyCheckFld.isSelected())
                    tempCustomer.setLoyaltyFlag(true);
                else
                    tempCustomer.setLoyaltyFlag(false);
                    
                vc.saveToFile();
                JOptionPane.showMessageDialog(null, "Customer has been updated successfully!", "Updating a client", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                mobileNumTxtFld.setEnabled(true);
                enablePanelButtons();
                disableButtons();
            }
        }
        
        if (event.getSource() == deleteButton){
            Customer tempCustomer = vc.accessCustomerBymobileNum(mobileNumTxtFld.getText());
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + tempCustomer.getName()+ "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if(reply == JOptionPane.YES_OPTION){
                //remove client
                vc.removeCustomer(tempCustomer);
                vc.saveToFile();
                JOptionPane.showMessageDialog(null, "Customer has been removed successfully!", "Delete a client", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                mobileNumTxtFld.setEnabled(true);
                disableButtons();
                enablePanelButtons();
            }
        }      
    }
}