package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Logic.*;

public class ViewCustomers extends JFrame implements ActionListener{
    private JPanel south;
    private JTable table;
    private JButton exitButton;
    private VectorCustomer vc;
    
    public ViewCustomers(){
        super("View all Customers");
        this.setLayout(new BorderLayout());
        vc = new VectorCustomer();
        vc.readFromFile();
        vc.sortCustomers();
        int numOfCustomers = vc.getSize();
        Customer tempCustomer = new Customer();
        String[] tableHeader = {"Name", "Surname","Mobile Number","Email"};
        Object[][] tableContent = new Object[numOfCustomers][4];
        
        for (int i = 0; i < numOfCustomers; i++){
            tempCustomer = vc.getCustomerByIndex(i);
            
            tableContent[i][0] = tempCustomer.getName();
            tableContent[i][1] = tempCustomer.getSurname();
            tableContent[i][2] = tempCustomer.getmobileNum();
            tableContent[i][3] = tempCustomer.geteMail();          

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