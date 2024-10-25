package Logic;
import java.io.*;
import java.text.SimpleDateFormat;  
import java.util.Date;

public class Reservation implements Serializable
{
   private String reservationNumber;
   private Customer customer;  //ask about why storing a whole object of customer,
   //cant we store only id
   //private Receptionist receptionist;
   private String receptionist;
   private Date date;
   private String tableNumber;
   private String reservationTime;
   private String surname;
   private String mobileNum;
   private String capacity;
   public Reservation() {
        //customer = new Customer();
       // receptionist = new Receptionist();  
        //table = new Table(); 
   }
    
   public Reservation(String reservationNumber, Customer customer, String receptionist, Date date,String tableNumber,String reservationTime,String surname,String mobileNum,String capacity) {
        this.reservationNumber = reservationNumber;
        this.customer = customer;
        this.receptionist = receptionist;
        //this.table = table;
        this.date = date;
        this.tableNumber = tableNumber;
        this.reservationTime = reservationTime;
        //this.name = name;
        this.surname = surname;
        this.mobileNum = mobileNum;
        this.capacity = capacity;
   } 
      
   public String getreservationNumber(){
        return reservationNumber;
   }
   
   public void setreservationNumber (String reservationNumber){
        this.reservationNumber = reservationNumber;
   }
    
   public void setcustomer(Customer customer){
       this.customer = customer;
   }
   
   public Customer getcustomer() {
       return customer;
   }
   
   public void setreceptionist(String receptionist){
       this.receptionist = receptionist;
   }
   
   public String getreceptionist() {
       return receptionist;
   }
   
   public void setDate(Date date){
       this.date = date;
   }

   public Date getDate() {
       return date;
   }
   
   public void settableNumber(String tableNumber){
       this.tableNumber = tableNumber;
   }

   public String gettableNumber() {
       return tableNumber;
   }
   
    public void setreservationTime(String reservationTime){
       this.reservationTime = reservationTime;
   }

   public String getreservationTime() {
       return reservationTime;
   }
   
   public String getSurname(){
        return surname;
   }
   
   public void setSurname(String surname){
        this.surname=surname;
   }
    
   public String getmobileNum(){
        return mobileNum;
   }
   
   public void setmobileNum(String mobileNum){
        this.mobileNum=mobileNum;
   }
   
   public String getcapacity(){
        return capacity;
   }
   
   public void setcapacity(String capacity){
        this.capacity=capacity;
   }
   
   public String toString(){
        return super.toString()+
        "\nReservation number: "+reservationNumber+
        "\nCustomer: "+customer.toString()+
        "\nReceptionist: "+receptionist.toString()+
        "\nTable: "+tableNumber.toString()+
        "\nDate: "+date+
        "\nTable number: "+tableNumber;
   }
}

