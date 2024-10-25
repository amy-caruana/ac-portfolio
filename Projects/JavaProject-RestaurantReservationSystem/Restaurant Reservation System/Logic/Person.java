package Logic;
import java.io.*;

public class Person implements Serializable
{
    private String name;
    private String surname;
    private String mobileNum;
    private String eMail;
    
    public Person(){
    }
    
    public Person(String name,String surname,String mobileNum,String eMail){
        this.name= name;
        this.surname= surname;
        this.mobileNum= mobileNum;
        this.eMail= eMail;
    } 
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name=name;
    }
    
    public String getSurname(){
        return surname;
    }
    
    public void setSurname(String surname){
        this.surname=surname;
    }
    
    public String geteMail() {
        return eMail;
    }
    
    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
    
    public String getmobileNum() {
        return mobileNum;
    }
    
    public void setmobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }
    
    public String ToString(){
        return("\nName:"+name+ "\nSurname:" +surname+"\nEmail:" +eMail+ "\nMobileNum"+mobileNum);
    }
}
   
    
    