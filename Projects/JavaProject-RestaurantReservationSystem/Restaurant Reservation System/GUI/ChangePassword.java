package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePassword extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectReceptionistPanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField receptionistNameTxtFld; 
    private JPasswordField curpasswdTxtFld, passwdTxtFld, passwd2TxtFld;
    private JButton updateButton, exitButton, selectReceptionistButton;
    private VectorReceptionist vr;
    
    public ChangePassword(){
        super("Change User Password");
        this.setLayout(new BorderLayout());
        vr = new VectorReceptionist();
        vr.readFromFile();
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Change Password");
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
       
        receptionistNameTxtFld = new JTextField(20);
        curpasswdTxtFld = new JPasswordField(20);
        passwdTxtFld = new JPasswordField(20);
        passwd2TxtFld = new JPasswordField(20);
        
        
        center.add(createLabel("Receptionist Name"));
        center.add(receptionistNameTxtFld);
        center.add(createLabel(""));
        selectReceptionistPanel = new JPanel();
        selectReceptionistButton = new JButton("Select");

        selectReceptionistPanel.add(selectReceptionistButton);
        selectReceptionistButton.addActionListener(this);
        center.add(selectReceptionistPanel);

        center.add(createLabel("Current Password"));
        center.add(curpasswdTxtFld);        
        center.add(createLabel("New Password"));
        center.add(passwdTxtFld);
        center.add(createLabel("Verify Password"));
        center.add(passwd2TxtFld);
        
        //south panel
        south = new JPanel();
        this.add(south,BorderLayout.SOUTH);
        updateButton = new JButton ("Update");
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
        receptionistNameTxtFld.setText("");
        curpasswdTxtFld.setText("");
        passwdTxtFld.setText("");
        passwd2TxtFld.setText("");
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if(receptionistNameTxtFld.getText().equals("") || curpasswdTxtFld.getPassword().equals("") || passwdTxtFld.getPassword().equals("") || passwd2TxtFld.getPassword().equals("")){
            flag = true;
        }
        return flag;
    }
    
    private boolean validPasswdMatch() {
        String a  = new String(passwdTxtFld.getPassword());
        String b  = new String(passwd2TxtFld.getPassword());
        if (a.equals(b))
            return true;
        else
            return false;
    }

    private boolean validPasswdLength() {
        if (passwdTxtFld.getPassword().length > 6)
            return true;
        else
            return false;
    }
    
    private boolean validateCurrentPasswd(String pass) {
               
        if (new String(curpasswdTxtFld.getPassword()).equals(pass))
            return true;
        else
            return false;
    }
    
    public void disableButtons(){
        updateButton.setEnabled(false);
    }
    
    public void enableButtons(){
        updateButton.setEnabled(true);
    }
    
    public void disablePanelButtons(){
        selectReceptionistButton.setEnabled(false);
    }
    
    public void enablePanelButtons(){
        selectReceptionistButton.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
              
        if (event.getSource() == selectReceptionistButton){
            if(vr.searchReceptionistByName(receptionistNameTxtFld.getText()) == false){
                JOptionPane.showMessageDialog(null, "Receptionist does not exist!", "Receptionist check", JOptionPane.ERROR_MESSAGE);
            }else{
                Receptionist tempReceptionist = vr.accessReceptionistByName(receptionistNameTxtFld.getText());
                receptionistNameTxtFld.setText(tempReceptionist.getName());            
                enableButtons();
                disablePanelButtons();
                receptionistNameTxtFld.setEnabled(false);
            }
        }
        
        if (event.getSource() == updateButton) {
            Receptionist tempReceptionist = vr.accessReceptionistByName(receptionistNameTxtFld.getText());
            if (validateCurrentPasswd(tempReceptionist.getPassword()) == false)
                JOptionPane.showMessageDialog(null, "You current password is incorrect", "Current Password check", JOptionPane.ERROR_MESSAGE);
            else
                if (validateEmptyFields() == true)
                    JOptionPane.showMessageDialog(null, "You have empty fields!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
                else 
                    if (validPasswdLength() == false)
                        JOptionPane.showMessageDialog(null, "Password too short (>6 Characters)!", "Password Length validation check", JOptionPane.ERROR_MESSAGE);
                    else 
                        if (validPasswdMatch() == false)
                            JOptionPane.showMessageDialog(null, "Passwords do not match!", "New Password validation check", JOptionPane.ERROR_MESSAGE);
                        else {
                            //update receptionist password details
                            tempReceptionist = vr.accessReceptionistByName(receptionistNameTxtFld.getText());
                            tempReceptionist.setPassword(passwdTxtFld.getText());
                            vr.saveToFile();
                            JOptionPane.showMessageDialog(null, "Password has been updated successfully!", "Changing Password", JOptionPane.INFORMATION_MESSAGE);
                            clearFields();
                            receptionistNameTxtFld.setEnabled(true);
                            enablePanelButtons();
                            disableButtons();
                        }
        }
    }
}