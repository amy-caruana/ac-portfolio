package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewPoints extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectCustomerPanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField nameTxtFld, mobileNumTxtFld, loyaltyPointsTxtFld;
    private JCheckBox loyaltyCheckFld;
    private JButton exitButton, selectCustomerButton;
    private VectorCustomer vc;
    
    public ViewPoints(){
        super("View customer Loyalty Points");
        this.setLayout(new BorderLayout());
        vc = new VectorCustomer();
        vc.readFromFile();
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Customer Loyalty Poits");
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
                disablePanelButtons();
                mobileNumTxtFld.setEnabled(false);
                nameTxtFld.setEnabled(false);
                loyaltyCheckFld.setEnabled(false);
                loyaltyPointsTxtFld.setEnabled(false);
            }
        }     
    }
}