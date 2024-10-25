package Logic;
import java.io.*;

public class Table implements Serializable{
    String tableId;
    int capacity;  
        
    public Table(){
    }
    
    public Table(String tableId, int capacity){
        this.tableId = tableId;
        this.capacity = capacity;
    }
    
    public String gettableId(){
        return tableId;
    }

    public String getTableNumber(){
        return tableId;
    }
	
    public void settableId (String tableId){
        this.tableId = tableId;
    }

    public void setTableNumber (String tableId){
        this.tableId = tableId;
    }
    
    public int getTableCapacity(){
        return capacity;
    }
   
    public void setTableCapacity (int capacity){
        this.capacity = capacity;
    }
       
    public boolean isAvailable(String reservationTime){
        //check if this table is available begin from reserve time, the default duration is 1 hour.
        return true;
    }
}
