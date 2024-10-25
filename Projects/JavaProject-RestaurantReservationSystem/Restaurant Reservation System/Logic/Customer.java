package Logic;
import java.io.*;

public class Customer extends Person implements Serializable{
    private boolean loyalty; //loyalty 
    private int points; //if points > 100 = loyal customer
    
    public Customer(){
    }
    
    public Customer(String name,String surname,String mobileNum,String email){
        super(name, surname, mobileNum, email);
        loyalty = false; 
        points = 0;       
    } 
    
    public boolean getLoyaltyFlag() {
        return loyalty;
    }
    
    public void setLoyaltyFlag(boolean flg) {
        this.loyalty = flg;
    }
    
    public int getPoints(){
        return points;
    }
    
    public void setPoints(int points){
        this.points = points;
    }
 
    public String ToString(){
        return super.ToString()+
        "\nLoyalty: "+loyalty+
        "\nPoints: "+points;
    }
}
    


    



    
   