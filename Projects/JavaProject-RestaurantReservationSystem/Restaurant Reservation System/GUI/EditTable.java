package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditTable extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectTablePanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField tableNumberTxtFld, tableCapacityTxtFld;
    private JButton updateButton, deleteButton, exitButton, viewTablesButton, selectTableButton;
    private VectorTables vt;
    private String[] tableList;
    private JComboBox<String> tablesComboFld;
    
    public EditTable(){
        super("Editing a table");
        this.setLayout(new BorderLayout());
        
        //get the table options for drop down
        vt = new VectorTables();
        vt.readFromFile();
        vt.sortTables();
        int numOfTables = vt.getSize();
        tableList = new String[numOfTables];
        for (int i = 0; i < numOfTables; i++){
            tableList[i] = vt.getTableByIndex(i).getTableNumber();
        }
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Editing a Table");
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
       
        //tableNumberTxtFld = new JTextField();
        tablesComboFld = new JComboBox<String>(tableList);
        tablesComboFld.setEditable(true);
        tableCapacityTxtFld = new JTextField();
        
        //center.add(createLabel("Table number"));
        //center.add(tableNumberTxtFld);
        center.add(createLabel("Table Number"));
        center.add(tablesComboFld);
        center.add(createLabel(""));
        selectTablePanel = new JPanel();
        selectTableButton = new JButton("Select");
        viewTablesButton = new JButton ("View");
        selectTablePanel.add(selectTableButton);
        selectTableButton.addActionListener(this);
        selectTablePanel.add(viewTablesButton);
        viewTablesButton.addActionListener(this);
        center.add(selectTablePanel);

        center.add(createLabel("Table Capacity"));
        center.add(tableCapacityTxtFld);
        
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
        //tableNumberTxtFld.setText("");
        tableCapacityTxtFld.setText("");
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if (tablesComboFld.getSelectedItem().equals("") || tableCapacityTxtFld.getText().equals("")){
            flag = true;
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
        selectTableButton.setEnabled(false);
        viewTablesButton.setEnabled(false);
    }
    
    public void enablePanelButtons(){
        selectTableButton.setEnabled(true);
        viewTablesButton.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
        
        if (event.getSource() == viewTablesButton){
            ViewTables viewTables = new ViewTables();
        }
       
        if (event.getSource() == selectTableButton){
            if(vt.searchTableByNumber((String)tablesComboFld.getSelectedItem()) == false){
                JOptionPane.showMessageDialog(null, "Table does not exist!", "Table check", JOptionPane.ERROR_MESSAGE);
            } else {
                Table tempTable = vt.accessTableByNumber((String)tablesComboFld.getSelectedItem());
                //tableNumberTxtFld.setText(tempTable.getTableNumber());
                tableCapacityTxtFld.setText(String.valueOf(tempTable.getTableCapacity()));
                
                enableButtons();
                disablePanelButtons();
                //tableNumberTxtFld.setEnabled(false);
            }
        }
        
        if(event.getSource() == updateButton){
            if(validateEmptyFields() == true){
                JOptionPane.showMessageDialog(null, "You have empty fields!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
            } else {
                //update table details
                //String table number, String table capacity
                Table tempTable = vt.accessTableByNumber((String)tablesComboFld.getSelectedItem());
                tempTable.setTableNumber((String)tablesComboFld.getSelectedItem());
                tempTable.setTableCapacity(Integer.parseInt(tableCapacityTxtFld.getText()));
                
                vt.saveToFile();
                JOptionPane.showMessageDialog(null, "Table has been updated successfully!", "Updating a table", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                tableNumberTxtFld.setEnabled(true);
                enablePanelButtons();
                disableButtons();
            }
        }
        
        if (event.getSource() == deleteButton){
            //Table tempTable = vt.accessTableByNumber(tableNumberTxtFld.getText());
             Table tempTable = vt.accessTableByNumber((String)tablesComboFld.getSelectedItem());
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + tempTable.getTableNumber() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if(reply == JOptionPane.YES_OPTION){
                //remove table
                vt.removeTable(tempTable);
                vt.saveToFile();
                JOptionPane.showMessageDialog(null, "Table has been removed successfully!", "Delete a table", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                //tableNumberTxtFld.setEnabled(true);
                disableButtons();
                enablePanelButtons();
            }
        }      
    }
}