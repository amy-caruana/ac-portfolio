package Logic;
import java.util.*;
import java.io.*;
import javax.swing.*;

public class VectorReceptionist
{
    private Vector <Receptionist> receptionists;
    
    public VectorReceptionist(){
       receptionists = new Vector<Receptionist>();
    }

    //add a receptionist
    public void addReceptionist(Receptionist receptionist){
       receptionists.add(receptionist);
    }
    
    //remove a receptionist
    public void removeReceptionist(Receptionist receptionist){
       receptionists.remove(receptionist);
    }
    
    //view all receptionists
    public void viewAllReceptionists(){
        for (Receptionist tempReceptionist: receptionists){
            System.out.println(tempReceptionist.toString());
        }
    }
    
    //checks whether a receptionist exists
    public boolean searchReceptionistByName(String receptionistName){
        boolean flag = false;
        for (Receptionist tempReceptionist: receptionists){
            if(tempReceptionist.getName().equals(receptionistName)){
                return true;
            }
        }
        return flag;
    }
    
    public void saveToFile(){
        try {
            File f = new File("ReceptionistsFile.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(receptionists);
            oos.flush();
            oos.close();
        } catch(IOException ioe) {
            JOptionPane.showMessageDialog(null, "Cannot write to file", "Writing file check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void readFromFile() {
        try {
            File f = new File("ReceptionistsFile.obj");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            receptionists = (Vector<Receptionist>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "File not found!", "File not found check", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Cannot read from file", "Reading file check", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Receptionist class not found", "Customer class check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String[] returnReceptionistList(int num) {
        String[] ReceptionistsList;
        ReceptionistsList = new String[num];

        return(ReceptionistsList);
    }
    
    public int getSize(){
        return receptionists.size();
    }
    
    //access receptionist by name
    public Receptionist accessReceptionistByName(String name) {
        Receptionist foundReceptionist = new Receptionist();
        for(Receptionist tempReceptionist: receptionists){
            if(tempReceptionist.getName().equals(name)){
                return tempReceptionist;
            }
        }
        return foundReceptionist;
    }
    
    public Receptionist getReceptionistByIndex(int i) {
        Receptionist tempReceptionist = new Receptionist();
        if(i < getSize()){
            tempReceptionist = receptionists.get(i);
        }
        return tempReceptionist;
    }
    
    public String getReceptionistNameByIndex(int i) {
        Receptionist tempReceptionist = new Receptionist();
        if(i < getSize()){
            tempReceptionist = receptionists.get(i);
        }
        return (String)tempReceptionist.getName();
    }
    
    public void sortReceptionists(){ //bubble sort method to sort by surname
        Receptionist nextReceptionist = new Receptionist();
        Receptionist currentReceptionist = new Receptionist();
        Receptionist tempReceptionist = new Receptionist();
        for(int i = 0; i < getSize(); i++){
            for(int j = 0; j < getSize()-i-1; j++){
                currentReceptionist = receptionists.elementAt(j);
                nextReceptionist = receptionists.elementAt(j+1);
                if(currentReceptionist.getName().compareTo(nextReceptionist.getName()) > 0){
                    tempReceptionist = currentReceptionist;
                    receptionists.setElementAt(nextReceptionist, j);
                    receptionists.setElementAt(tempReceptionist, j+1);
                }
            }
        }
    }
}

    
    