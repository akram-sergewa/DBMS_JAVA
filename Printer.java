	import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStreamWriter;
    import java.io.Writer;
    import java.io.FileReader;
    import java.io.FileWriter;
    import java.util.List;
    import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Vector;
	
class Printer {
	
	private Vector<Vector> rowData = new Vector<Vector>();
    private Vector<String> columnNames = new Vector<String>();
	private Table recordTable;
	private Row row;
    private Integer cellSize = 15;
    public String fileName;

	
	
	Printer (Vector<Vector> rowData0, Vector<String> columnNames0, Table recordTable0, Row row0, String fileName0){
		this.rowData = rowData0;
		this.columnNames = columnNames0;
		this.row = row0;
		this.recordTable= recordTable0;
		this.fileName = fileName0;
		
	}

 private void printHeaderTable (Integer[] selectedColumns, Integer count){
        System.out.println(recordTable.tableName);
        if (row.allSelected == false){
            for (int j = 0; j<count; j++){
                cellPrinter(recordTable.columns.get(selectedColumns[j]));
				columnNames.addElement(recordTable.columns.get(selectedColumns[j]));
            }   
        }
        else {
            for (int j = 0; j<recordTable.columns.size(); j++){
                cellPrinter(recordTable.columns.get(j));
				columnNames.addElement(recordTable.columns.get(j));
            }
        }
        
        System.out.println("\n-------------------------------------------");
    }
	
	
	 private void cellPrinter (String content){
        int extraSize = cellSize;
        if (content.length() < (cellSize/4)){extraSize = cellSize/4;}

        System.out.print(content);
            for (int i = content.length(); i < extraSize; i++){
                 System.out.print(" ");
            }
        System.out.print("|");

    }
	
	public void printResult (Integer[] selectedColumns, Integer count){
        printHeaderTable(selectedColumns, count); 

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

			
			while (line != null) {
                String[] values = line.split("/@/");

                //store the raw inside the row object
                for (String v : values){
                    row.values.add(v);
                }

					if (row.isSelected() == true){
						Vector<String> singleRow = new Vector<String>();
						if (row.allSelected == false){
							for (int j = 0; j<count; j++){
								cellPrinter(row.values.get(selectedColumns[j]));
								singleRow.addElement(row.values.get(selectedColumns[j]));
							}	
						}
						else {
							for (int j = 0; j<row.values.size(); j++){
								cellPrinter(row.values.get(j));
								singleRow.addElement(row.values.get(j));
							}	
						}
						
						System.out.println("");
						rowData.addElement(singleRow);
						}
				row.renewLists();
		              

                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
		
            //String everything = sb.toString();
            br.close();
            System.out.println("");
        }
      catch (IOException e) {System.err.println("Problem reading from the file");}

    }
	
	
            
 }
