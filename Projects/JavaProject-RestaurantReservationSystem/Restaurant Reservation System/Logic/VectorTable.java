package Logic;
import java.util.*;
import java.io.*;
import javax.swing.*;

public class VectorTable{
   private Vector<Table> tables;
   
   public VectorTable(){
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
    //view all tables
   public void viewAllTables(){
       for(Table tempTable: tables){
           System.out.println(tempTable.toString());
        }
   }
}