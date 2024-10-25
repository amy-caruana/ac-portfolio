package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddReceptionist extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField nameTxtFld, passwordTxtFld;
    private JButton clearButton, saveButton, exitButton;
    private VectorReceptionist vrec;
    private String[] resRights = {"User", "Admin"};
    private JComboBox<String> receptionistRightsComboFld;
    
    public AddReceptionist(){
        super("Adding a receptionist");
        this.setLayout(new BorderLayout());
        vrec = new VectorReceptionist();
        vrec.readFromFile();
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Adding a Receptionist");
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
    
        nameTxtFld = new JTextField();
        passwordTxtFld = new JTextField();
        receptionistRightsComboFld = new JComboBox<String>(resRights);
        receptionistRightsComboFld.setEditable(true);
        
        center.add(createLabel("Name"));
        center.add(nameTxtFld);
        center.add(createLabel("Password"));
        center.add(passwordTxtFld);
        center.add(createLabel("User type"));
        center.add(receptionistRightsComboFld);

        
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
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if (nameTxtFld.getText().equals("")) {
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
                JOptionPane.showMessageDialog(null, "You have an empty field!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
            } else 
                if (vrec.searchReceptionistByName(nameTxtFld.getText()) == true){
                    JOptionPane.showMessageDialog(null, "Receptionist already exists", "Receptionist check", JOptionPane.ERROR_MESSAGE);
                } else {
                    //save receptionist
                    //String name, String userRights;
                    Receptionist tempReceptionist = new Receptionist(nameTxtFld.getText(),passwordTxtFld.getText(),(String)receptionistRightsComboFld.getSelectedItem()); 
                    vrec.addReceptionist(tempReceptionist);
                    vrec.saveToFile();
                    JOptionPane.showMessageDialog(null, "Receptionist has been added successfully!", "Adding a receptionist", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                }
        }
    }
}