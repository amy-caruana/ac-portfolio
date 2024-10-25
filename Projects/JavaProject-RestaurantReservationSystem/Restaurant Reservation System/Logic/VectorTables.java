package Logic;
import java.util.*;
import java.io.*;
import javax.swing.*;

public class VectorTables{
    private Vector <Table> tables;
   
    public VectorTables(){
       tables = new Vector<Table>();
    }
    
    //add a table
    public void addTable(Table table){
       tables.add(table);
    }
    
    //remove a table
    public void removeTable(Table table){
       tables.remove(table);
    }
    
    //view all tables
    public void viewAllTables(){
       for(Table tempTable: tables){
           System.out.println(tempTable.toString());
        }
    }
    
    //checks whether a table exists
    public boolean searchTableByNumber(String tableNum){
        boolean flag = false;
        for (Table tempTable: tables){
            if(tempTable.getTableNumber().equals(tableNum)){
                return true;
            }
        }
        return flag;
    }
    
    //access table by number
    public Table accessTableByNumber(String tableNum){
        Table foundTable = new Table();
        for(Table tempTable: tables){
            if(tempTable.getTableNumber().equals(tableNum)){
                return tempTable;
            }
        }
        return foundTable;
    }
    
    public void saveToFile() {
        try {
            File f = new File("tableFile.obj");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tables);
            oos.flush();
            oos.close();
        } catch(IOException ioe) {
            System.out.println("Exception : " + ioe.toString());
            JOptionPane.showMessageDialog(null, "Cannot write to file", "Writing file check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void readFromFile() {
        try {
            File f = new File("tableFile.obj");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tables = (Vector<Table>)ois.readObject();
            ois.close();
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "File not found!", "File not found check", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioe) {
            System.out.println("Exception : " + ioe.toString());
            JOptionPane.showMessageDialog(null, "Cannot read from file", "Reading file check", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Table class not found", "Table class check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public int getSize(){
        return tables.size();
    }
    
    //check if reservation party can be all seated at the table
    public boolean checkTableSeats(int ncheck) {
      Table currentTable = new Table();
      for (int j = 0; j < getSize(); j++) {
          currentTable = tables.elementAt(j);
          if (currentTable.getTableCapacity() < ncheck)
            return false;
      }
      return true;
    }
    
    public Table getTableByIndex(int i){
        Table tempTable = new Table();
        if(i < getSize()){
            tempTable = tables.get(i);
        }
        return tempTable;
    }
    
    public void sortTables(){ //bubble sort method to sort by table number
        Table nextTable = new Table();
        Table currentTable = new Table();
        Table tempTable = new Table();
        for(int i = 0; i < getSize(); i++){
            for(int j = 0; j < getSize()-i-1; j++){
                currentTable = tables.elementAt(j);
                nextTable = tables.elementAt(j+1);
                if(currentTable.getTableNumber().compareTo(nextTable.getTableNumber()) > 0){
                    tempTable = currentTable;
                    tables.setElementAt(nextTable, j);
                    tables.setElementAt(tempTable, j+1);
                }
            }
        }
    }
}
