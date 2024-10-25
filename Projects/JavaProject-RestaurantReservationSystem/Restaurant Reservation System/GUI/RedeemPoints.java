package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedeemPoints extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectCustomerPanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField nameTxtFld, mobileNumTxtFld, loyaltyPointsTxtFld;
    private JCheckBox loyaltyCheckFld;
    private JButton updateButton, deleteButton, exitButton, 
    viewCustomersButton, selectCustomerButton;
    private VectorCustomer vc;
    private int points;
    private int vpoints = 100;                  //number of points required for a voucher
    private int vworth = 25;                    //cost of a voucher (in Euro)
    
    public RedeemPoints(){
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
        loyaltyPointsTxtFld = new JTextField();
        loyaltyCheckFld = new JCheckBox();
        
        
        center.add(createLabel("Mobile number"));
        center.add(mobileNumTxtFld);
        center.add(createLabel(""));
        selectCustomerPanel = new JPanel();
        selectCustomerButton = new JButton("Select");
        selectCustomerPanel.add(selectCustomerButton);
        selectCustomerButton.addActionListener(this);
        center.add(selectCustomerPanel);
        
        center.add(createLabel("Name"));
        center.add(nameTxtFld);
        center.add(createLabel("Loyalty Scheme"));
        center.add(loyaltyCheckFld);
        center.add(createLabel("Loyalty Points"));
        center.add(loyaltyPointsTxtFld);
        
        //south panel
        south = new JPanel();
        this.add(south,BorderLayout.SOUTH);
        updateButton = new JButton ("Redeem Points");
        south.add(updateButton);
        updateButton.addActionListener(this);
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
    }
    
    
    public void disableButtons(){
        updateButton.setEnabled(false);
    }
    
    public void enableButtons(){
        updateButton.setEnabled(true);
    }
    
    public void disablePanelButtons(){
        selectCustomerButton.setEnabled(false);
    }
    
    public void enablePanelButtons(){
        selectCustomerButton.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
       
        if (event.getSource() == selectCustomerButton){
            if (vc.searchCustomerBymobileNum(mobileNumTxtFld.getText()) == false){
                JOptionPane.showMessageDialog(null, "Customer does not exist!", "Customer check", JOptionPane.ERROR_MESSAGE);
            } else {
                Customer tempCustomer = vc.accessCustomerBymobileNum(mobileNumTxtFld.getText());
                nameTxtFld.setText(tempCustomer.getName() + " " + tempCustomer.getSurname());
                loyaltyCheckFld.setSelected(tempCustomer.getLoyaltyFlag());
                loyaltyPointsTxtFld.setText(Integer.toString(tempCustomer.getPoints()));
                points = tempCustomer.getPoints();
                disablePanelButtons();
                enableButtons();
                mobileNumTxtFld.setEnabled(false);
                nameTxtFld.setEnabled(false);
                loyaltyCheckFld.setEnabled(false);
                loyaltyPointsTxtFld.setEnabled(false);
            }
        }     
        
        if (event.getSource() == updateButton)
            if (points < vpoints)
                JOptionPane.showMessageDialog(null, "You do not have Enough points to redeem.", "Points check", JOptionPane.ERROR_MESSAGE);
            else {
                int vouchers = 0;
                if ((points > vpoints) && (points < (vpoints * 2))) { 
                    String msg = "Proceed to redeem 1 voucher (" + vpoints + " points)? ";
                    int reply = JOptionPane.showConfirmDialog(null, msg, "Redeem points", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        vouchers = 1;
                    }
                } else {
                    int pv = points / vpoints;
                    String s = "how many vouchers would you like to redeem (1 - " + pv + ")?";
                    String m = JOptionPane.showInputDialog(s, 1);
                    if (m.isEmpty())
                        JOptionPane.showMessageDialog(null, "No vouchers redeemed!", "Customer check", JOptionPane.ERROR_MESSAGE);
                    else {
                        vouchers = Integer.parseInt(m);
                    }
                }

                if (vouchers > 0) {
                    //update customer loyality points
                    Customer tempCustomer = vc.accessCustomerBymobileNum(mobileNumTxtFld.getText());
                    tempCustomer.setPoints(points - (vpoints * vouchers));
                    vc.saveToFile();
                    JOptionPane.showMessageDialog(null, "Customer loyalty point have been updated successfully!", "Updating a client", JOptionPane.INFORMATION_MESSAGE);
                    String discount = "Issue a discount voucher of :" + (vouchers * vworth) + " Euro from bill";
                    JOptionPane.showMessageDialog(null, discount, "Voucher", JOptionPane.INFORMATION_MESSAGE);
                    disableButtons();
                }
            }    
    }
}