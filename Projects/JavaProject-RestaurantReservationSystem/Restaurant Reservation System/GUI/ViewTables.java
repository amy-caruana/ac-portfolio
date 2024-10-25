package GUI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Logic.*;

public class ViewTables extends JFrame implements ActionListener{
    private JPanel south;
    private JTable table;
    private JButton exitButton;
    private VectorTables vt;
    
    public ViewTables(){
        super("View all Tables");
        this.setLayout(new BorderLayout());
        vt = new VectorTables();
        vt.readFromFile();
        vt.sortTables();
        int numOfTables = vt.getSize();
        Table tempTable = new Table();
        String[] tableHeader = {"Table Number", "Table Capacity"};
        Object[][] tableContent = new Object[numOfTables][2];
        
        for (int i = 0; i < numOfTables; i++){
            tempTable = vt.getTableByIndex(i);
            
            tableContent[i][0] = tempTable.getTableNumber();
            tableContent[i][1] = tempTable.getTableCapacity();          

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