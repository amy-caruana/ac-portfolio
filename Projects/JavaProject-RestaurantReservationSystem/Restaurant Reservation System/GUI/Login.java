package GUI;

import Logic.*;

public class Login {
 
    public static Integer authenticate(String username, String password) {
        // hardcoded username and password
        
        VectorReceptionist vrec;
        vrec = new VectorReceptionist();
        vrec.readFromFile();
        vrec.sortReceptionists();
        int numOfReceptionists = vrec.getSize();
        Receptionist tempReceptionist = new Receptionist();
        String[] tableHeader = {"Name", "User Type"};
        Object[][] tableContent = new Object[numOfReceptionists][2];
        
        
        for (int i = 0; i < numOfReceptionists; i++){
            tempReceptionist = vrec.getReceptionistByIndex(i);
            if (username.equals(tempReceptionist.getName()) && password.equals(tempReceptionist.getPassword())) {
                if (tempReceptionist.getRights().equals("Admin"))
                    return 1;           //User is an administrator
                else
                    return 0;           //User is a normal user
            }
        }
        return -1;                      //Failed to logon to system
    }
}

