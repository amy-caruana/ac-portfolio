package Logic;
import java.util.*;
import java.io.*;
import javax.swing.*;

public class VectorReservation
{
   private Vector<Reservation> reservations;
    
   public VectorReservation(){
        reservations = new Vector<Reservation>();
   }

    //add a reservation
   public void addReservation(Reservation reservation){
       reservations.add(reservation);
    }
    //remove a reservation
   public void removeReservation(Reservation reservation){
       reservations.remove(reservation);
    }
    //view all reservations
   public void viewAllReservations(){
       for(Reservation tempReservation: reservations){
           System.out.println(tempReservation.toString());
        }
    }
    //checks whether a reservation exists via reservation number
   public boolean searchReservationByreservationNumber(String reservationNumber){
        boolean flag = false;
        for (Reservation tempReservation: reservations){
            if(tempReservation.getreservationNumber().equals(reservationNumber)){
                return true;
            }
        }
        return flag;
   }
    
    //access reservation by reservation number
   public Reservation accessReservationByreservationNumber(String reservationNumber){
        Reservation foundReservation = new Reservation();
        for(Reservation tempReservation: reservations){
            if(tempReservation.getreservationNumber().equals(reservationNumber)){
                return tempReservation;
            }
        }
        return foundReservation;
   }
    
   public void saveToFile() {
        try {
            File f = new File("reservationsFile.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(reservations);
            oos.flush();
            oos.close();
        } catch(IOException ioe) {
            JOptionPane.showMessageDialog(null, "Cannot write to file", "Writing file check", JOptionPane.ERROR_MESSAGE);
        }
   }
    
   public void readFromFile() {
        try {
            File f = new File("reservationsFile.obj");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            reservations = (Vector<Reservation>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "File not found!", "File not found check", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Cannot read from file", "Reading file check", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Reservation class not found", "Reservation class check", JOptionPane.ERROR_MESSAGE);
        }
   }
    
   public int getSize(){
        return reservations.size();
   }
   
   public int getNextReservationNumber() {
       int lastReservation = 1;
       Reservation tempReservation = new Reservation();
       for (int i = 0; i < getSize(); i++) {
           tempReservation = reservations.get(i);
           if (Integer.parseInt(tempReservation.getreservationNumber()) > lastReservation) {
               lastReservation = Integer.parseInt(tempReservation.getreservationNumber());
           }
           lastReservation++;
       }
       return lastReservation;
   }
    
   public Reservation getReservationByIndex(int i){
        Reservation tempReservation = new Reservation();
        if(i < getSize()){
            tempReservation = reservations.get(i);
        }
        return tempReservation;
   }
    
   public void sortReservations(){ //bubble sort method to sort by surname
        Reservation nextReservation = new Reservation();
        Reservation currentReservation = new Reservation();
        Reservation tempReservation = new Reservation();
        for(int i = 0; i < getSize(); i++){
            for(int j = 0; j < getSize()-i-1; j++){
                currentReservation = reservations.elementAt(j);
                nextReservation = reservations.elementAt(j+1);
                if (currentReservation.getSurname().compareTo(nextReservation.getSurname()) > 0) {
                //if(currentReservation.getcustomer().getSurname().compareTo(nextReservation.getcustomer().getSurname()) > 0){
                    tempReservation = currentReservation;
                    reservations.setElementAt(nextReservation, j);
                    reservations.setElementAt(tempReservation, j+1);
                }
            }
        }
   }

   public boolean checkSlot(Date Dcheck, String tcheck) {
       
      Reservation currentReservation = new Reservation();
      for(int j = 0; j < getSize(); j++){
            currentReservation = reservations.elementAt(j);
            if (currentReservation.getDate().compareTo(Dcheck) == 0)
                if (currentReservation.getreservationTime() == tcheck)
                    return false;
      }
      return true;
   }
}


