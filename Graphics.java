
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;
import java.util.Collections;

import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;

	



class Graphics implements Runnable {


	private Vector<Vector> rowData = new Vector<Vector>();
    private Vector<String> columnNames = new Vector<String>();
	private JTable table;
	private DefaultTableModel model;
	private JFrame w = new JFrame();
	
  public void run() {
	     //w.setDefaultCloseOperation();
    w.setTitle("DBS");
    w.add(displayTable());
    w.pack();
    w.setLocationByPlatform(true);
    w.setVisible(true);
  }

	public void close(){
		w.dispose();
		 w = new JFrame();
	}
  

  JScrollPane displayTable(){
	model=new DefaultTableModel(rowData, columnNames);
    table = new JTable(model);
    JScrollPane scrollPane = new JScrollPane(table);
	return scrollPane;
  }
  
 	
    public void updateTable(Vector<Vector> rowData0, Vector<String> columnNames0){
		rowData = rowData0;
		columnNames = columnNames0;	
	}
	
}