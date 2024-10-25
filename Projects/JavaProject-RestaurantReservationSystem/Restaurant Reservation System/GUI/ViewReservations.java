package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Logic.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class ViewReservations extends JFrame implements ActionListener{
    private JPanel south;
    private JTable table;
    private JButton exitButton;
    private VectorReservation vr;
    
    public ViewReservations(){
        super("View all Reservations");
        this.setLayout(new BorderLayout());
        vr= new VectorReservation();
        vr.readFromFile();
        vr.sortReservations();
        int numOfReservations = vr.getSize();
        Reservation tempReservation = new Reservation();
        
        String[] tableHeader = {"Reservation Number", "Receptionist Id", "Date","Table Id","Reservation Time","Customer Surname","Customer Mobile Number","Capacity"};
        
        //String[] tableHeader = {"Customer Surname", "Customer Mobile Number", 
        //"Date", "Reservation Time", "Reservation Number","Table Number", "Party"};
          
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Object[][] tableContent = new Object[numOfReservations][8];
        
        for (int i = 0; i < numOfReservations; i++){
            tempReservation = vr.getReservationByIndex(i);
            
            tableContent[i][0] = tempReservation.getreservationNumber();
            tableContent[i][1] = tempReservation.getreceptionist();
            tableContent[i][2] = dateFormat.format(tempReservation.getDate());
            tableContent[i][3] = tempReservation.gettableNumber();
            tableContent[i][4] = tempReservation.getreservationTime();
            tableContent[i][5] = tempReservation.getSurname();
            tableContent[i][6] = tempReservation.getmobileNum();
            tableContent[i][7] = tempReservation.getcapacity();
        }
        
        table = new JTable(tableContent, tableHeader);
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
        
        this.setSize(800,600);
        this.setLocation(50,100);
        this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() == exitButton){
            this.dispose();
        }
    }
}