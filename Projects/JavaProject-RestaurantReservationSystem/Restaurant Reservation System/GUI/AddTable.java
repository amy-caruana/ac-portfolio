package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddTable extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField tableNumTxtFld, tableCapacityTxtFld;
    private JButton clearButton, saveButton, exitButton;
    private VectorTables vt;
    
    public AddTable(){
        super("Adding a table");
        this.setLayout(new BorderLayout());
        vt = new VectorTables();
        vt.readFromFile();
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Adding a Table");
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
    
        tableNumTxtFld = new JTextField();
        tableCapacityTxtFld = new JTextField();      
        
        center.add(createLabel("Table Number"));
        center.add(tableNumTxtFld);
        center.add(createLabel("Table Seating Capacity"));
        center.add(tableCapacityTxtFld);
       
        
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
        tableNumTxtFld.setText("");
        tableCapacityTxtFld.setText("");
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if (tableNumTxtFld.getText().equals("") || tableCapacityTxtFld.getText().equals("")){
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
        
        if(event.getSource() == saveButton) {
            if(validateEmptyFields() == true) {
                JOptionPane.showMessageDialog(null, "You have empty fields!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
            } else 
                if (vt.searchTableByNumber(tableNumTxtFld.getText()) == true){
                    JOptionPane.showMessageDialog(null, "Table id already exists", "Table id check", JOptionPane.ERROR_MESSAGE);
                } else {
                    //save table
                    //String id, String table number, String table seating capacity
                    Table tempTable = new Table(tableNumTxtFld.getText(), Integer.parseInt(tableCapacityTxtFld.getText()));
                    vt.addTable(tempTable);
                    vt.saveToFile();
                    JOptionPane.showMessageDialog(null, "Table has been added successfully!", "Adding a table", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                }
        }
    }
}