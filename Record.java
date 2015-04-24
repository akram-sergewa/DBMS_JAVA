    import java.util.Scanner;
    import java.io.BufferedWriter;
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


class Record {

    public String recordName;
    public String fileName;
    private Table recordTable;
    private Row row = new Row();
    private Integer tCount = 0;

	private Vector<Vector> rowData = new Vector<Vector>();
    private Vector<String> columnNames = new Vector<String>();


    public void putName (String n){
        recordName = n;
        this.fileName = recordName.concat(".record");
        this.fileName = "files/" + this.recordTable.database + "/" + this.fileName;

    }


    public void connectTable (Table t){
        recordTable = t;
        row.columns = this.recordTable.columns;
        row.attributesNum = this.recordTable.attributesNum;

    }


    public void insertCheck(String[] input){
        if (input[3].toUpperCase().equals("VALUES") == false){System.err.println("Check the query insert"); return;}
        Writer writer = null;
        if (checkRecordExist() == false){writer = createRecord();}
        else {writer = openRecord();}
             try {
                if (addRecordValues(writer, input) == false){System.out.println("Error with the query");return;}
                writer.close();
                System.out.println("The Record "+recordName+" is updated");
             }
             catch (IOException e) {System.err.println("Problem writing to the file");}


    }
    

    public boolean selectCheck(String[] input){
        Integer[] selectedColumns= new Integer[100]; //TO HOLD THE SELECTED COLUMNS
        this.tCount = 1; //COUNTER FOR THE INPUT
		boolean where = true;
        Integer countWhere = 0;

        
        Integer count = fromCheck(input, selectedColumns);
        if (count == -1){return false;}
        while ("WHERE".equals(input[tCount].toUpperCase()) == false){
			if (input.length == tCount+1){where = false; break;}
			if (";".equals(input[tCount])){where = false; break;}
			tCount++;
		}
		tCount++;
		
		if (where == true){
            countWhere = whereCheck(input);			
		}	
        if (countWhere == -1){return false;}

		row.attributesNum = countWhere;
		Printer printer = new Printer(this.rowData, this.columnNames, this.recordTable, this.row, this.fileName);
        printer.printResult(selectedColumns, count);
		return true;

    }


    private Integer fromCheck(String[] input, Integer[] selectedColumns){
        boolean found = false; //FOR INDICATING WHEN AN ATTRIB IS FOUND
        Integer count = 0; //FOR COUNTING THE NUMBER OF SELECTED COLUMNS

        while (input[tCount].toUpperCase().equals("FROM") == false){
			if (input.length == tCount+1){return -1;}
            if (input[tCount].equals("*")){tCount++;row.allSelected = true;}
            else {
                for (int j = 0; j<recordTable.attributesNum; j++){
                    if (recordTable.isColumn(input[tCount], j)){
                        selectedColumns[count++] = j;
                        found = true;
                        tCount++;
                        if (",".equals(input[tCount])){tCount++;}
                    }
                }
                if (found == false){System.err.println("Error, not found column, check FROM statement"); return -1;}
                found = false;
            }
        }

        return count;
    }


    private Integer whereCheck(String[] input){
        boolean found = false; //FOR INDICATING WHEN AN ATTRIB IS FOUND
        Integer countWhere = 0; //FOR COUNTING THE NUMBER OF SELECTED COLUMNS

            row.where = true;
            while (input[tCount].equals(";") == false){
                for (int j = 0; j<recordTable.attributesNum; j++){
                    if (recordTable.isColumn(input[tCount], j)){
                        tCount++;
                        row.putWhereData(j, input[tCount++], input[tCount++]);
                        found = true;
                        countWhere++;
                    }
                }
                if (found == false){System.err.println("Error, not found column"); return -1;}
                found = false;
            }

        return countWhere;
    }



    private boolean checkRecordExist(){
        File f = new File(fileName);
        if(f.exists() && !f.isDirectory()) { return true; }
        return false;
    }

     private Writer createRecord (){
        Writer w = null;
        try {
                File statText = new File(fileName);
                FileOutputStream is = new FileOutputStream(statText);
                OutputStreamWriter osw = new OutputStreamWriter(is);
                w = new BufferedWriter(osw);
                //w.close();
            }catch (IOException e) {
            System.err.println("Problem writing to the file");
        }
        return w;
    }


    private Writer openRecord (){
        Writer w = null;
        try {
                FileWriter statText = new FileWriter(fileName, true);
                w = new BufferedWriter(statText);
                //w.close();
            }catch (IOException e) {
            System.err.println("Problem writing to the file");
        }
        return w;
    }


     private boolean addRecordValues (Writer writer, String[] input){
        this.tCount = 4;
        int counter = 0;
        if ("(".equals(input[tCount++]) == false){System.err.println("no bracket");return false;}
        while (")".equals(input[tCount]) == false){

        try {
            while (",".equals(input[tCount]) == false && ")".equals(input[tCount]) == false){
                if (recordTable.hasPrimaryKey && counter == recordTable.primaryKey){
                    if (isPrimaryValid(input[tCount], counter) == false){return false;}
                }
				if (recordTable.hasForeignKey){
					for (int j = 0;j<recordTable.foreignKeyColumnSrc.size();j++){
						if (counter == recordTable.foreignKeyColumnSrc.get(j)){
							if (isForeginValid(input[tCount], j) == false){System.out.println("notValidForeign");return false;}
						}
					}
                }
                    if (isValidInput(input[tCount], counter)){writer.write (input[tCount++]);}
                    else {System.err.println("wrong data type, the column (" + recordTable.getColumn(counter) + ") accepts " + recordTable.getAttribute(counter));return false;}
                
            }

            if (recordTable.attributesNum == (counter+1) && ")".equals(input[tCount]) == false){writer.write ("\n");counter = 0;tCount++;}
            else if (",".equals(input[tCount])){writer.write ("/@/");tCount++;counter++;}
            else if (")".equals(input[tCount])){writer.write ("\n");}



          }
        catch (IOException e) {System.err.println("Problem writing to the file");}

        }//end while

	return true;
    }


    private boolean isValidInput (String value, Integer counter){
        double d;

        if (recordTable.attributes.get(counter).toUpperCase().equals ("INT")){
            try  
              {  
                d = Integer.parseInt(value); 
              }  
              catch(NumberFormatException nfe)  
              {  
                return false;  
              } 
        }
		else if (recordTable.attributes.get(counter).toUpperCase().equals ("DOUBLE")){
            try  
              {  
                d = Double.parseDouble(value);  
              }  
              catch(NumberFormatException nfe)  
              {  
                return false;  
              } 
		}
		else if (recordTable.attributes.get(counter).toUpperCase().equals ("CHAR")){
            if (value.length() > 1){return false;}
		}
        return true;
    }


    private boolean isPrimaryValid(String value, Integer column){
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                String[] values = line.split("/@/");
                for (String v : values){
                    row.values.add(v);
                }
                if (row.values.get(column).equals(value) == true){
                    System.err.println("repeated primary key in the column " + row.columns.get(column));
                    return false;
                }

                row.renewLists();
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();

            }
            br.close();
        }
      catch (IOException e) {System.err.println("Problem reading from the file");}
      return true;
    }


	
	private boolean isForeginValid(String value, Integer foreignColumn){
		boolean found = false;

		Integer fkColumnDst = recordTable.foreignKeyColumnDst.get(foreignColumn);
		String fkTable = recordTable.foreignKeyTable.get(foreignColumn);
		String ftableFileName = "files/"+recordTable.database+"/"+fkTable+".record";

		
		try {
            BufferedReader br = new BufferedReader(new FileReader(ftableFileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                String[] values = line.split("/@/");
                for (String v : values){
                    row.values.add(v);
                }
                if (row.values.get(fkColumnDst).equals(value) == true){
                   found = true;
                }

                row.renewLists();
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();

            }
            br.close();
        }
      catch (IOException e) {System.err.println("Problem reading from the file");}
	  if (found == false){return false;}
      return true;
		     
    }
	
	

    private boolean checkAtrib(String atrib){
        if (atrib.toUpperCase().equals("INT")){return true;}
        if (atrib.toUpperCase().equals("CHAR")){return true;}
        if (atrib.toUpperCase().equals("STRING")){return true;}
        return false;
    }




	public void updateCheck(String[] input){
        Integer countWhere = 0; //FOR COUNTING THE NUMBER OF SELECTED COLUMNS
        boolean found = false; //FOR INDICATING WHEN AN ATTRIB IS FOUND
        this.tCount = 2; //COUNTER FOR THE INPUT
        boolean where = true;
        if (input[tCount++].toUpperCase().equals("SET") == false){System.err.println("Error with the query, no SET"); return;}


        while (input[tCount].toUpperCase().equals("WHERE") == false){
                for (int j = 0; j<recordTable.attributesNum; j++){
                    if (recordTable.isColumn(input[tCount], j)){
                        if ("=".toUpperCase().equals(input[tCount+1]) == false){System.err.println("Error, check the query"); return;}
                        if (isValidInput(input[tCount+2], j) == false){return;}
                        if (recordTable.hasPrimaryKey && j == recordTable.primaryKey){if (isPrimaryValid(input[tCount+2], j) == false)return;}
                        row.putNewValuesData(j, input[tCount+2]);
                        tCount+=3;
                        found = true;
                    }
                }
                if (found == false){System.err.println("Error, not found column"); return;}
                found = false;
        }
        tCount++;

            while (input[tCount].equals(";") == false){
                for (int j = 0; j<recordTable.attributesNum; j++){
                    if (recordTable.isColumn(input[tCount], j)){
                        tCount++;
                        row.putWhereData(j, input[tCount++], input[tCount++]);
                        found = true;
                        countWhere++;

                    }
                }
                if (found == false){System.err.println("Error, not found column for WHERE"); return;}
                found = false;
            }           

        row.where = true;
        row.attributesNum = countWhere;
        setNewValue();
    }




    private void setNewValue (){
            List<String> newLines = new ArrayList<String>();
            boolean updateValue = false;


         try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            
            while (line != null) {    
                   

                String[] values = line.split("/@/");
                for (String v : values){
                    row.values.add(v);
                }

                    if (row.isSelected() == true){
                        String newLine = "";
                        for (int j = 0; j < row.values.size(); j++){
                            for (int k = 0; k < row.clmnNewValues.size(); k++){
                                if (row.clmnNewValues.get(k) == j){newLine = newLine + row.newValues.get(k);updateValue=true;}
                            }
                            if (updateValue == false){newLine = newLine + row.values.get(j);}
                            if (j < row.values.size()-1){newLine = newLine + "/@/";}
                            updateValue = false;
                        }
                        newLines.add(newLine);
                    }
                    else {newLines.add(line);}
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




        try {
           FileWriter fw = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fw);
            for(String s : newLines){
                 out.write(s);
             out.write("\n");
         }
            out.flush();
            out.close(); 
        }    
        catch (IOException e) {System.err.println("Problem writing to the file");}



        }
		
		
	public void removeCheck(String[] input){
        Integer countWhere = 0; //FOR COUNTING THE NUMBER OF SELECTED COLUMNS
        boolean found = false; //FOR INDICATING WHEN AN ATTRIB IS FOUND
        this.tCount = 3; //COUNTER FOR THE INPUT
        boolean where = true;
        if (input[tCount++].toUpperCase().equals("WHERE") == false){System.err.println("Error with the query, no SET"); return;}

            while (input[tCount].equals(";") == false){
                for (int j = 0; j<recordTable.attributesNum; j++){
                    if (recordTable.isColumn(input[tCount], j)){
                        tCount++;
                        row.putWhereData(j, input[tCount++], input[tCount++]);
                        found = true;
                        countWhere++;
                    }
                }
                if (found == false){System.err.println("Error, not found column for WHERE"); return;}
                found = false;
            }           
   
        row.where = true;
        row.attributesNum = countWhere;
        removeValue();
    }
	
	private void removeValue (){
            List<String> newLines = new ArrayList<String>();
            boolean updateValue = false;


         try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            
            while (line != null) {                    

                String[] values = line.split("/@/");
                for (String v : values){
                    row.values.add(v);
                }

                    if (row.isSelected() == false){
                        newLines.add(line);
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




        try {
           FileWriter fw = new FileWriter(fileName);
            BufferedWriter out = new BufferedWriter(fw);
            for(String s : newLines){
                 out.write(s);
             out.write("\n");
         }
            out.flush();
            out.close(); 
        }    
        catch (IOException e) {System.err.println("Problem writing to the file");}



        }
		


	public Vector<String> getColumn(){
		return this.columnNames;
	}
	
	public Vector<Vector> getRow(){
		return this.rowData;
	}




}
