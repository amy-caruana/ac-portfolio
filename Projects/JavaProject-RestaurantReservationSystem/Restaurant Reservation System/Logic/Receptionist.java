package Logic;
import java.io.*;

public class Receptionist extends Person implements Serializable{    

    private String receptionistName;
    private String userPassword;
    private String userRights;
    
    public Receptionist() {
        
    }

    public Receptionist(String receptionistName, String userPassword, String userRights) {
        this.receptionistName = receptionistName;
        this.userPassword = userPassword;
        this.userRights = userRights;

    }
    
    public String getName(){
        return receptionistName;
    }
    
    public void setName(String receptionist){
       this.receptionistName = receptionist;
    }

    public String getRights(){
        return userRights;
    }
    
    public void setRights(String receptionist){
       this.userRights = userRights;
    }

    public String getPassword(){
        return userPassword;
    }
    
    public void setPassword(String userPassword){
       this.userPassword = userPassword;
    }    
    public String toString(){
        return super.toString() + "\nReceptionist Name: " + receptionistName;
    }
}
    

