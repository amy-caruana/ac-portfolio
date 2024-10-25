package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import Logic.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;  
import java.util.Date;
import java.text.ParseException;
import java.text.DateFormat;    
import java.util.Calendar; 

public class EditReservation extends JFrame implements ActionListener{
    private JPanel north, south, east, west, center, selectReservationPanel;
    private JLabel titleLabel, westLabel, eastLabel;
    private JTextField surnameTxtFld, mobileTxtFld,receptionistIdTxtFld,
    reservationTimeTxtFld,reservationNumberTxtFld,tableIdTxtFld,capacityTxtFld,dateTxtFld;
    private JButton updateButton, deleteButton, exitButton, checkDateButton,
    viewReservationButton, selectReservationButton;
    private VectorReservation vr;
    private VectorReceptionist vrec;
    private VectorTables vtab;
    private VectorCustomer vcus;
    private String[] resTimes = {"", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"};
    private String[] receptionistList;
    private String[] tableList, tableNames;
    private JComboBox<String> reservationTimeComboFld;
    private JComboBox<String> receptionistIdComboFld;
    private JComboBox<String> tablesComboFld;
    
    public EditReservation(){
        super("Editing a reservation");
        this.setLayout(new BorderLayout());
        vr = new VectorReservation();
        vr.readFromFile();
        
        //get the receptionists names for drop down
        vrec = new VectorReceptionist();
        vrec.readFromFile();
        vrec.sortReceptionists();
        int numOfReceptionists = vrec.getSize();
        receptionistList = new String[numOfReceptionists];
        for (int i = 0; i < numOfReceptionists; i++){
            receptionistList[i] = vrec.getReceptionistNameByIndex(i);
        }
        
        //get the table options for drop down
        vtab = new VectorTables();
        vtab.readFromFile();
        vtab.sortTables();
        int numOfTables = vtab.getSize();
        tableList = new String[numOfTables];
        tableNames = new String[numOfTables];
        for (int i = 0; i < numOfTables; i++){
            tableList[i] = vtab.getTableByIndex(i).getTableNumber() + " (Seats " + Integer.toString(vtab.getTableByIndex(i).getTableCapacity()) + ")";
            tableNames[i] = vtab.getTableByIndex(i).getTableNumber();
        }        
        
        //north panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north, BorderLayout.NORTH);
        titleLabel = new JLabel ("Editing a Reservation");
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
       
        reservationNumberTxtFld = new JTextField();
        surnameTxtFld = new JTextField();              
        mobileTxtFld = new JTextField();
        receptionistIdComboFld = new JComboBox<String>(receptionistList);
        receptionistIdComboFld.setEditable(true); 
        reservationTimeComboFld = new JComboBox<String>(resTimes);
        reservationTimeComboFld.setEditable(true);
        tablesComboFld = new JComboBox<String>(tableList);
        tablesComboFld.setEditable(true);
        reservationNumberTxtFld = new JTextField();
        capacityTxtFld = new JTextField();
        dateTxtFld = new JTextField();        
        
        reservationNumberTxtFld.setText("");
        center.add(createLabel("Reservation Number"));
        center.add(reservationNumberTxtFld);
        center.add(createLabel(""));
        selectReservationPanel = new JPanel();
        selectReservationButton = new JButton("Select");
        viewReservationButton = new JButton ("View");
        selectReservationPanel.add(selectReservationButton);
        selectReservationButton.addActionListener(this);
        selectReservationPanel.add(viewReservationButton);
        viewReservationButton.addActionListener(this);
        
        center.add(selectReservationPanel);
        center.add(createLabel("Customer Surname"));
        center.add(surnameTxtFld);
        center.add(createLabel("Customer Mobile Number"));
        center.add(mobileTxtFld);
        center.add(createLabel(" Reservation Date (dd/mm/yyyy)"));
        center.add(dateTxtFld);
        center.add(createLabel("Receptionist"));
        center.add(receptionistIdComboFld);
        center.add(createLabel("Reservation Time"));
        center.add(reservationTimeComboFld);
        center.add(createLabel("Table Number"));
        center.add(tablesComboFld);
        center.add(createLabel("Party Number"));
        center.add(capacityTxtFld);
        
        //south panel
        south = new JPanel();
        checkDateButton = new JButton ("Check Date");
        south.add(checkDateButton);
        checkDateButton.addActionListener(this);
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
        reservationNumberTxtFld.setText("");
        surnameTxtFld.setText("");
        mobileTxtFld.setText("");
        dateTxtFld.setText(""); 
        capacityTxtFld.setText(""); 
        reservationTimeComboFld.setSelectedItem("");
        receptionistIdComboFld.setSelectedItem("");
        tablesComboFld.setSelectedItem("");
    }
    
    private boolean validateEmptyFields(){
        boolean flag = false;
        
        if (surnameTxtFld.getText().equals("") || mobileTxtFld.getText().equals("") || dateTxtFld.getText().equals("") ||
        receptionistIdComboFld.getSelectedItem().equals("") || reservationTimeComboFld.getSelectedItem().equals("") ||
        reservationNumberTxtFld.getText().equals("") || tablesComboFld.getSelectedItem().equals("") || capacityTxtFld.getText().equals("")) {
            flag = true;
        }
        return flag;
    }
    
    public boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }
        catch (ParseException e) {
            return false;
        }
        
        if (!sdf.format(testDate).equals(date)) {
            JOptionPane.showMessageDialog(null,"Invalid date format","Invalid date format", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        
        if (testDate.before(new Date())) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null,"Invalid date (in the past!)","Invalid date (in the past!)", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
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
        selectReservationButton.setEnabled(false);
        viewReservationButton.setEnabled(false);
    }
    
    public void enablePanelButtons(){
        selectReservationButton.setEnabled(true);
        viewReservationButton.setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent event){
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        if (event.getSource() == exitButton){
            this.dispose();
        }
        
        if (event.getSource() == checkDateButton){
            checkAvailability checkAvailability = new checkAvailability(dateTxtFld.getText());
        }
        
        if (event.getSource() == viewReservationButton){
            ViewReservations viewReservation = new ViewReservations();
        }
        
        if (event.getSource() == selectReservationButton){
            if(vr.searchReservationByreservationNumber(reservationNumberTxtFld.getText()) == false){
                JOptionPane.showMessageDialog(null, "Reservation does not exist!", "Reservation check", JOptionPane.ERROR_MESSAGE);
            } else {
                Reservation tempReservation = vr.accessReservationByreservationNumber(reservationNumberTxtFld.getText());
                reservationNumberTxtFld.setText(tempReservation.getreservationNumber());
                surnameTxtFld.setText(tempReservation.getSurname());
                mobileTxtFld.setText(tempReservation.getmobileNum());
                dateTxtFld.setText(dateFormat.format(tempReservation.getDate()));
                receptionistIdComboFld.setSelectedItem(tempReservation.getreceptionist());
                reservationTimeComboFld.setSelectedItem(tempReservation.getreservationTime());
                
                for (int t = 0; t < tableList.length; t++) {
                    if (tempReservation.gettableNumber().equals(tableNames[t])) {
                        tablesComboFld.setSelectedIndex(t);
                    }
                }
                
                capacityTxtFld.setText(tempReservation.getcapacity());
                
                enableButtons();
                disablePanelButtons();
                reservationNumberTxtFld.setEnabled(false);
            }
        }
        
        if(event.getSource() == updateButton){
            if(validateEmptyFields() == true) {
                JOptionPane.showMessageDialog(null, "You have empty fields!", "Empty fields check", JOptionPane.ERROR_MESSAGE);
            } else {
                if (isValidDate(dateTxtFld.getText())){
                    //System.out.println("...date is valid");
                    //save reservation  
                    try {
                        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(dateTxtFld.getText());
                        if (vr.checkSlot(date1, (String)reservationTimeComboFld.getSelectedItem())) {
                            if (vtab.checkTableSeats(Integer.parseInt(capacityTxtFld.getText()))) {
                                //System.out.println("Updating record " + reservationNumberTxtFld.getText());
                                //update reservation details
                                Reservation tempReservation = vr.accessReservationByreservationNumber(reservationNumberTxtFld.getText());
                                tempReservation.setreservationNumber(reservationNumberTxtFld.getText());
                                tempReservation.setSurname(surnameTxtFld.getText());
                                tempReservation.setmobileNum(mobileTxtFld.getText());
                                tempReservation.setDate(date1);
                                tempReservation.setreservationTime((String)reservationTimeComboFld.getSelectedItem());
                                tempReservation.settableNumber((String)tableNames[tablesComboFld.getSelectedIndex()]);
                                tempReservation.setreceptionist((String)receptionistIdComboFld.getSelectedItem());
                                tempReservation.setcapacity(capacityTxtFld.getText());  
        
                                vr.saveToFile();
                                JOptionPane.showMessageDialog(null, "Reservation has been updated successfully!", "Updating a reservation", JOptionPane.INFORMATION_MESSAGE);                        
                                clearFields();
                                reservationNumberTxtFld.setEnabled(true);
                                enablePanelButtons();
                                disableButtons();
                            } else {
                                JOptionPane.showMessageDialog(null, "Table too small for reservation party", "Table size check", JOptionPane.ERROR_MESSAGE);
                                //System.out.println("...date is invalid...");
                            }                                
                        } else {
                            JOptionPane.showMessageDialog(null, "Reservation Slot is already booked", "Slot check", JOptionPane.ERROR_MESSAGE);
                            //System.out.println("...date is invalid...");
                        }  
                    }
                    catch (ParseException e) {
                        JOptionPane.showMessageDialog(null, "You have an invalid date format", "Date field check", JOptionPane.ERROR_MESSAGE);
                        //System.out.println("...date is invalid...");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You have an invalid date format", "Date field check", JOptionPane.ERROR_MESSAGE);
                    //System.out.println("...date is invalid...");
                }
            }
        }
        
        if (event.getSource() == deleteButton){
            Reservation tempReservation = vr.accessReservationByreservationNumber(reservationNumberTxtFld.getText());
            int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " 
            + tempReservation.getDate()+ "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            
            if(reply == JOptionPane.YES_OPTION){
                //remove client
                vr.removeReservation(tempReservation);
                vr.saveToFile();
                JOptionPane.showMessageDialog(null, "Reservation has been removed successfully!", "Delete a reservation", JOptionPane.INFORMATION_MESSAGE);
                vcus = new VectorCustomer();
                vcus.readFromFile();
                if (vcus.searchCustomerBymobileNum(mobileTxtFld.getText()) == true){
                    Customer tempCustomer = vcus.accessCustomerBymobileNum(mobileTxtFld.getText());
                    if (tempCustomer.getLoyaltyFlag()) {
                        int points = tempCustomer.getPoints();
                        points -= 10;
                        tempCustomer.setPoints(points);
                        vcus.saveToFile();
                        JOptionPane.showMessageDialog(null, "Customer " + tempCustomer.getName() + " " + tempCustomer.getSurname() + " has 10 points deducted!", "Loyality Scheme", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                clearFields();
                reservationNumberTxtFld.setEnabled(true);
                disableButtons();
                enablePanelButtons();
            }
        }
    }
    
}