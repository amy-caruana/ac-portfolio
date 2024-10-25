package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Logic.*;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar; 

public class checkAvailability extends JFrame implements ActionListener{
    private JPanel south;
    private JTable table;
    private JButton exitButton;
    private VectorTables vt;
    private VectorReservation vr;
    private Reservation rd;
    private String[] resTimes = {"", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"};
    private String[] tableList;
    
    public checkAvailability(String reservationDate){
        super("Reservations for " + reservationDate);
        this.setLayout(new BorderLayout());
        
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        
        //read the restaurant tables data
        vt = new VectorTables();
        vt.readFromFile();
        vt.sortTables();
        int numOfTables = vt.getSize();
        
        Object[][] tableContent = new Object[numOfTables][resTimes.length];
        
        tableList = new String[numOfTables];
        for (int i = 0; i < numOfTables; i++) {
            tableList[i] = vt.getTableByIndex(i).getTableNumber();
            tableContent[i][0] = tableList[i] + " (Seats " + Integer.toString(vt.getTableByIndex(i).getTableCapacity()) + ")";
        }
        
        //read reservations for the date selected
        vr = new VectorReservation();
        vr.readFromFile();
        int numOfReservations = vr.getSize();
        for (int i = 0; i < numOfReservations; i++) {
            rd = vr.getReservationByIndex(i); 
            String strDate = dateFormat.format(rd.getDate()); 
            if (strDate.equals(reservationDate)) {
                for (int j = 0; j < resTimes.length; j++) {
                    if (rd.getreservationTime().equals(resTimes[j])) {
                        for (int t = 0; t < numOfTables; t++) {
                            if (rd.gettableNumber().equals(tableList[t])) {
                                tableContent[t][j] = rd.getSurname() + " (" + rd.getcapacity() + ")";
                            }
                        }
                    }
                }
            }
        }
        
        
        table = new JTable(tableContent, resTimes);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new Dimension(500,400));
        this.add(table.getTableHeader(), BorderLayout.NORTH);
        this.add(table, BorderLayout.CENTER);
        
        south = new JPanel();
        south.setLayout(new FlowLayout());
        this.add(south, BorderLayout.SOUTH);
        exitButton = new JButton("Exit");
        south.add(exitButton);
        exitButton.addActionListener(this);
        
        this.setSize(1200,200);
        this.setLocation(50,100);
        this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
    }
}