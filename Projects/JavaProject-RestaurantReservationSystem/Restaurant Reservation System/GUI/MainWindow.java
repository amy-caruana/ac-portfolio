package GUI;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class MainWindow extends JFrame implements ActionListener
{
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private JPanel north,south,east,west,center;
    private JLabel titleLabel,westLabel,eastLabel,centerLabel;
    private ImageIcon image;
    private JButton exitButton, logonButton, logoffButton;
    private String userName;
    private Integer userRights; //0 = not logged in, 1 = normal user, 2 = superuser
    private Integer UserIndex;
    private JMenuItem jmiAddCustomer, jmiAddReservation, jmiEditCustomer, jmiEditReservation, jmiViewCustomer, jmiViewReservation;
    private JMenuItem jmiAddReceptionist, jmiDelReceptionist, jmiAddTable, jmiEditTable, jmiChgPasswd;
    private JMenuItem jmiCheckPoints, jmiRedeemPoints;
    
    final JFrame frame = new JFrame("JDialog Demo");
    final JButton btnLogin = new JButton("Click to login");     
    
    public MainWindow()
    {
        super("Restaurant Reservation System");
        this.setLayout(new BorderLayout());       

        userRights = 0;
        
        //Building the menu system
        menuBar = new JMenuBar();

        //File menu
        menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem jmiExit = new JMenuItem("Exit");
        menu.add(jmiExit);
        jmiExit.addActionListener(this);
        
        //Add menu
        menu = new JMenu("Add");
        menuBar.add(menu);
        //JMenuItem jmiAddCustomer = new JMenuItem("Add Customer");
        jmiAddCustomer = new JMenuItem("Add Customer");
        menu.add(jmiAddCustomer);
        jmiAddCustomer.addActionListener(this);
        jmiAddCustomer.setEnabled(false);
        
        jmiAddReservation = new JMenuItem("Add Reservation");
        menu.add(jmiAddReservation);
        jmiAddReservation.addActionListener(this);
        jmiAddReservation.setEnabled(false);
            
        //Edit menu
        menu = new JMenu("Edit");
        menuBar.add(menu);
        
        jmiEditCustomer = new JMenuItem("Edit Customer");
        menu.add(jmiEditCustomer);
        jmiEditCustomer.addActionListener(this);
        jmiEditCustomer.setEnabled(false);
        
        jmiEditReservation = new JMenuItem("Edit Reservation");
        menu.add(jmiEditReservation);
        jmiEditReservation.addActionListener(this);
        jmiEditReservation.setEnabled(false);
        
        //View menu
        menu = new JMenu("View");
        menuBar.add(menu);
        
        jmiViewCustomer = new JMenuItem("View Customers");
        menu.add(jmiViewCustomer);
        jmiViewCustomer.addActionListener(this);
        jmiViewCustomer.setEnabled(false);
        
        jmiViewReservation = new JMenuItem("View Reservations");
        menu.add(jmiViewReservation);
        jmiViewReservation.addActionListener(this);
        jmiViewReservation.setEnabled(false);

        //Loyalty points menu
        menu = new JMenu("Loyality Points");
        menuBar.add(menu);
        
        jmiCheckPoints= new JMenuItem("Check Points");
        menu.add(jmiCheckPoints);
        jmiCheckPoints.addActionListener(this);
        jmiCheckPoints.setEnabled(false);
        
        jmiRedeemPoints = new JMenuItem("Redeem Points");
        menu.add(jmiRedeemPoints);
        jmiRedeemPoints.addActionListener(this);
        jmiRedeemPoints.setEnabled(false);
        
        //Admin menu
        menu = new JMenu("System");
        menuBar.add(menu);
        
        jmiAddReceptionist= new JMenuItem("Add Receptionist");
        menu.add(jmiAddReceptionist);
        jmiAddReceptionist.addActionListener(this);
        jmiAddReceptionist.setEnabled(false);
        
        jmiDelReceptionist = new JMenuItem("Delete Receptionist");
        menu.add(jmiDelReceptionist);
        jmiDelReceptionist.addActionListener(this);
        jmiDelReceptionist.setEnabled(false);
        
        jmiAddTable = new JMenuItem("Add Table");
        menu.add(jmiAddTable);
        jmiAddTable.addActionListener(this);
        jmiAddTable.setEnabled(false);
        
        jmiEditTable = new JMenuItem("Edit Table");
        menu.add(jmiEditTable);
        jmiEditTable.addActionListener(this);
        jmiEditTable.setEnabled(false);

        jmiChgPasswd = new JMenuItem("Change Password");
        menu.add(jmiChgPasswd);
        jmiChgPasswd.addActionListener(this);
        jmiChgPasswd.setEnabled(false);
              
        //North Panel
        north = new JPanel();
        north.setLayout(new FlowLayout());
        this.add(north,BorderLayout.NORTH);
        titleLabel = new JLabel("Restaurant Reservation System");
        titleLabel.setFont(new Font ("Century Gotchic", Font.BOLD, 40));
        titleLabel.setForeground(Color.blue);
        north.add(titleLabel);
        
        //West and East Panels
        west = new JPanel();
        east = new JPanel();
        this.add(west, BorderLayout.WEST);
        this.add(east, BorderLayout.EAST);
        westLabel = new JLabel("          ");
        eastLabel = new JLabel("          ");
        west.add(westLabel);
        east.add(eastLabel);
        
        //Center Panel
        center = new JPanel();
        this.add(center, BorderLayout.CENTER);
        image = new ImageIcon(getClass().getResource("restaurant.jpg")); //add the picture in GUI folder (small pic)
        centerLabel = new JLabel(image);
        center.add(centerLabel);
        
        //South Panel
        south = new JPanel();
        this.add(south,BorderLayout.SOUTH);
        logonButton = new JButton("Logon");
        south.add(logonButton);
        logonButton.addActionListener(this);
        logoffButton = new JButton("Logoff");
        south.add(logoffButton);
        logoffButton.addActionListener(this);
        exitButton = new JButton("Exit");
        south.add(exitButton);
        exitButton.addActionListener(this);
        
        this.setJMenuBar(menuBar);
        this.setSize(800,600);
        this.setLocation(50,10);
        this.setVisible(true); 
    }
    
    public void actionPerformed(ActionEvent event){
        if (event.getSource() instanceof JButton){
            if (event.getSource() == exitButton) {
                this.dispose();
            }
            
            if (event.getSource() == logonButton) {
                JOptionPane.showMessageDialog(null,"Logon to the System","Logging onto system", JOptionPane.INFORMATION_MESSAGE);
                GUI.LoginDialog loginDlg = new GUI.LoginDialog(frame);
                loginDlg.setVisible(true);
                // if logon successfully
                if (loginDlg.isSucceeded()) {

                    jmiAddCustomer.setEnabled(true);
                    jmiAddReservation.setEnabled(true);
                    jmiEditCustomer.setEnabled(true);
                    jmiEditReservation.setEnabled(true);
                    jmiViewCustomer.setEnabled(true);
                    jmiViewReservation.setEnabled(true);
                    jmiChgPasswd.setEnabled(true);
                    jmiCheckPoints.setEnabled(true);
                    jmiRedeemPoints.setEnabled(true);
                    
                    if (loginDlg.userRights() == 1) {
                        jmiAddReceptionist.setEnabled(true);
                        jmiDelReceptionist.setEnabled(true);
                        jmiAddTable.setEnabled(true);
                        jmiEditTable.setEnabled(true);
                    } else {
                       jmiAddReceptionist.setEnabled(false);
                        jmiDelReceptionist.setEnabled(false);
                        jmiAddTable.setEnabled(false);
                        jmiEditTable.setEnabled(false);                        
                    }
                    
                    btnLogin.setText("Hi " + loginDlg.getUsername() + "!");
                }
            }
            
            if (event.getSource() == logoffButton) {
                JOptionPane.showMessageDialog(null,"Logoff the System","Logging off system", JOptionPane.INFORMATION_MESSAGE);

                jmiAddCustomer.setEnabled(false);
                jmiAddReservation.setEnabled(false);
                jmiEditCustomer.setEnabled(false);
                jmiEditReservation.setEnabled(false);
                jmiViewCustomer.setEnabled(false);
                jmiViewReservation.setEnabled(false);
                jmiChgPasswd.setEnabled(false);
                
                jmiAddReceptionist.setEnabled(false);
                jmiDelReceptionist.setEnabled(false);
                jmiAddTable.setEnabled(false);
                jmiEditTable.setEnabled(false);

            }
        } else {
            if (event.getSource() instanceof JMenuItem) {
                JMenuItem menuChoice = (JMenuItem) event.getSource();
            
                if (menuChoice.getText().equals("Exit")){
                    this.dispose();
                }
                
                if (menuChoice.getText().equals("Add Customer")){                
                    JOptionPane.showMessageDialog(null,"Add a customer","Adding a customer", JOptionPane.INFORMATION_MESSAGE);
                    GUI.AddCustomer addCus = new GUI.AddCustomer();
                    addCus.setVisible(true);
                }
                
                if (menuChoice.getText().equals("Add Reservation")){                
                    JOptionPane.showMessageDialog(null,"Add a reservation","Adding a reservation", JOptionPane.INFORMATION_MESSAGE);
                    GUI.AddReservation addRes = new GUI.AddReservation();
                    addRes.setVisible(true);
                }
                
                if (menuChoice.getText().equals("Edit Reservation")){                
                    JOptionPane.showMessageDialog(null,"Edit a reservation","Editing a reservation", JOptionPane.INFORMATION_MESSAGE);
                    GUI.EditReservation editRes = new GUI.EditReservation();
                    editRes.setVisible(true);
                }
                
                if (menuChoice.getText().equals("Edit Customer")){                
                    JOptionPane.showMessageDialog(null,"Edit a Customer","Editing a customer", JOptionPane.INFORMATION_MESSAGE);
                    GUI.EditCustomer editCus = new GUI.EditCustomer();
                    editCus.setVisible(true);
                }
                
                if (menuChoice.getText().equals("View Reservations")){                
                    JOptionPane.showMessageDialog(null,"View a reservation","Viewing a reservation", JOptionPane.INFORMATION_MESSAGE);
                    GUI.ViewReservations viewRes = new GUI.ViewReservations();
                    viewRes.setVisible(true);
                }
                
                if (menuChoice.getText().equals("View Customers")){                
                    JOptionPane.showMessageDialog(null,"View a customer","Viewing customers", JOptionPane.INFORMATION_MESSAGE);
                    GUI.ViewCustomers viewCus = new GUI.ViewCustomers();
                    viewCus.setVisible(true);
                }
                
                if (menuChoice.getText().equals("Add Receptionist")){                
                    JOptionPane.showMessageDialog(null,"Add Receptionist","Add receptionist", JOptionPane.INFORMATION_MESSAGE);
                    GUI.AddReceptionist addReceptionist = new GUI.AddReceptionist();
                    addReceptionist.setVisible(true);
                }
               
                if (menuChoice.getText().equals("Delete Receptionist")){                
                    JOptionPane.showMessageDialog(null,"Delete Receptionist","Delete receptionist", JOptionPane.INFORMATION_MESSAGE);
                    GUI.DelReceptionist delReceptionist = new GUI.DelReceptionist();
                    delReceptionist.setVisible(true);
                }
    
                if (menuChoice.getText().equals("Add Table")){                
                    JOptionPane.showMessageDialog(null,"Add Table","Add table", JOptionPane.INFORMATION_MESSAGE);
                    GUI.AddTable addTable = new GUI.AddTable();
                    addTable.setVisible(true);
                }
               
                if (menuChoice.getText().equals("Edit Table")){                
                    JOptionPane.showMessageDialog(null,"Edit Table","Edit table", JOptionPane.INFORMATION_MESSAGE);
                    GUI.EditTable editTable = new GUI.EditTable();
                    editTable.setVisible(true);
                }
                
                if (menuChoice.getText().equals("Change Password")){                
                    JOptionPane.showMessageDialog(null,"Change Password","Change Password", JOptionPane.INFORMATION_MESSAGE);
                    GUI.ChangePassword ChangePassword = new GUI.ChangePassword();
                    ChangePassword.setVisible(true);
                }
                
                if (menuChoice.getText().equals("Check Points")){                
                    JOptionPane.showMessageDialog(null,"Check Loyalty Points","Check Points", JOptionPane.INFORMATION_MESSAGE);
                    GUI.ViewPoints ViewPoints = new GUI.ViewPoints();
                    ViewPoints.setVisible(true);
                }
            }
        }
    }
}
