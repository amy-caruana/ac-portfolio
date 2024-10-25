package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Logic.*;

public class ViewReceptionists extends JFrame implements ActionListener{
    private JPanel south;
    private JTable table;
    private JButton exitButton;
    private VectorReceptionist vrec;
    
    public ViewReceptionists(){
        super("View all Receptionists");
        this.setLayout(new BorderLayout());
        vrec = new VectorReceptionist();
        vrec.readFromFile();
        vrec.sortReceptionists();
        int numOfReceptionists = vrec.getSize();
        Receptionist tempReceptionist = new Receptionist();
        String[] tableHeader = {"Name", "User Type"};
        Object[][] tableContent = new Object[numOfReceptionists][2];
        
        for (int i = 0; i < numOfReceptionists; i++){
            tempReceptionist = vrec.getReceptionistByIndex(i);
            tableContent[i][0] = tempReceptionist.getName();
            tableContent[i][1] = tempReceptionist.getRights();
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