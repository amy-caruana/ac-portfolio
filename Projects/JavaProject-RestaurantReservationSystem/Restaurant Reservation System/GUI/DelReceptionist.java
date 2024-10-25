package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelReceptionist extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectReceptionistPanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField nameTxtFld;
    private JButton deleteButton, exitButton, viewReceptionistButton;
    private VectorReceptionist vrec;
    private String[] receptionistList;
    private JComboBox<String> receptionistIdComboFld;
    
    public DelReceptionist(){
        super("Editing a Receptionist Record");
        this.setLayout(new BorderLayout());

        //get the receptionists names for drop down
        vrec = new VectorReceptionist();
        vrec.readFromFile();
        vrec.sortReceptionists();
        int numOfReceptionists = vrec.getSize();
        receptionistList = new String[numOfReceptionists];
        for (int i = 0; i < numOfReceptionists; i++){
            receptionistList[i] = vrec.getReceptionistNameByIndex(i);
        }
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Delete a Receptionist Record");
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
        //nameTxtFld = new JTextField();
        receptionistIdComboFld = new JComboBox<String>(receptionistList);
        receptionistIdComboFld.setEditable(true); 
        
        //center.add(createLabel("Receptionist"));
        //center.add(nameTxtFld);
        center.add(createLabel("Receptionist"));
        center.add(receptionistIdComboFld);
        center.add(createLabel(""));
        selectReceptionistPanel = new JPanel();
        viewReceptionistButton = new JButton ("View");
        selectReceptionistPanel.add(viewReceptionistButton);
        viewReceptionistButton.addActionListener(this);
        center.add(selectReceptionistPanel);
        
        //center.add(createLabel("Name"));
        //center.add(nameTxtFld);

        //south panel
        south = new JPanel();
        this.add(south,BorderLayout.SOUTH);
        deleteButton = new JButton ("Delete");
        south.add(deleteButton);
        deleteButton.addActionListener(this);
        exitButton = new JButton ("Exit");
        south.add(exitButton);
        exitButton.addActionListener(this);
        
       enableButtons();
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
        
        if(nameTxtFld.getText().equals("")){
            flag = true;
        }
        return flag;
    }
    
    public void disableButtons(){
        deleteButton.setEnabled(false);
    }
    
    public void enableButtons(){
        deleteButton.setEnabled(true);
    }
    
    public void disablePanelButtons(){
        viewReceptionistButton.setEnabled(false);
    }
    
    public void enablePanelButtons(){
        viewReceptionistButton.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
        
        if (event.getSource() == viewReceptionistButton){
            ViewReceptionists viewReceptionists = new ViewReceptionists();
        }
        
        if (event.getSource() == deleteButton){
            if(vrec.searchReceptionistByName((String)receptionistIdComboFld.getSelectedItem()) == false){
                JOptionPane.showMessageDialog(null, "Receptionist does not exist!", "Receptionist check", JOptionPane.ERROR_MESSAGE);
            } else {
                Receptionist tempReceptionist = vrec.accessReceptionistByName((String)receptionistIdComboFld.getSelectedItem());
                int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + tempReceptionist.getName()+ "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
                if(reply == JOptionPane.YES_OPTION){
                    //remove receptionist
                    vrec.removeReceptionist(tempReceptionist);
                    vrec.saveToFile();
                    JOptionPane.showMessageDialog(null, "Receptionist has been removed successfully!", "Delete a receptionist", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    nameTxtFld.setEnabled(true);
                    disableButtons();
                    enablePanelButtons();
                }
            }
        }      
    }
}