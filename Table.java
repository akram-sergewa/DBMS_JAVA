    import java.util.Scanner;
    import java.io.BufferedWriter;
    import java.io.BufferedReader;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStreamWriter;
    import java.io.Writer;
    import java.io.FileReader;
	import java.util.List;
	import java.util.ArrayList;

class Table {

    public String tableName;
    public String fileName;
	public List<String> attributes = new ArrayList<String>();
	public List<String> columns = new ArrayList<String>();
    public Integer attributesNum;
    public Integer primaryKey;
	public List<Integer> foreignKeyColumnSrc = new ArrayList<Integer>();
	public List<Integer> foreignKeyColumnDst = new ArrayList<Integer>();
	public List<String> foreignKeyTable = new ArrayList<String>();
	
    public boolean hasPrimaryKey = false;
    public boolean hasForeignKey = false;
    public String database;


    public void putName (String n){
        this.tableName = n;
        this.fileName = tableName.concat(".table");
        this.fileName = "files/" + this.database + "/" + this.fileName;
    }

    public void connectDatabase (String database){
        this.database = database;
    }



    //CHECK THE COMMAN CREATE TABLE
    public void createCheck(String[] input){
        String tableName = input[2].concat(".table");
        if (checkTableExist() == false){
            Writer writer = createTable();

             try {
                if (addTableAtrib(writer, input) == false){return;}
                writer.close();
                System.out.println("The Table "+tableName+" is created");
             }
             catch (IOException e) {System.err.println("Problem writing to the file");}

        }else{
            System.out.println("The Table  already exist");
        }


    }




    public boolean checkTableExist(){
        File f = new File(fileName);
        if(f.exists() && !f.isDirectory()) { return true; }
        return false;
    }




     private Writer createTable (){
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



    //ADDS THE ATTRIBUTIONS TO THE TABLE AFTER CHECKING THEM
     private boolean addTableAtrib (Writer writer, String[] input){
        int i = 3;
        if ("{".equals(input[i++]) == false){System.err.println("no curly bracket");return false;}
        while ("}".equals(input[i]) == false){

        try {
            if (input[i].equals(",")){writer.write ("\n");i++;}
            else if (isPrimaryKey(input[i], input[i+1])){addPrimaryKey(writer, input, i); i = i + 3;}
            else if (isForeignKey(input[i], input[i+1])){addForeginKey(writer, input[i+2],input[i+3], i); i = i + 4;}
            else if (input[i].equals("}") == false && input[i].equals(",") == false) {
                writer.write (input[i]+" ");
                this.columns.add(input[i]);
                i++;
                if (checkAtrib(input[i])){
                    writer.write (input[i]+" ");
                    this.attributes.add(input[i]);
                    i++;
                }
                else {System.err.println("wrong attrib");return false;}

            }


          }
        catch (IOException e) {System.err.println("Problem writing to the file");}

        }//end while
	return true;
    }


    private void addPrimaryKey(Writer writer, String[] input, Integer i){
        boolean found = false;
        try {
            for (int p = 0; p < this.columns.size(); p++){
                if (input[i+2].equals(this.columns.get(p))){writer.write ("PRIMARY_KEY " + input[i+2] + "\n"); found = true;}
            }
        }
        catch (IOException e) {System.err.println("Problem writing to the file");}
        if (found == false){System.err.println("Wrong primary key");System.exit(1);}       
    }



    private boolean isPrimaryKey(String s1, String s2){
        if (s1.toUpperCase().equals("PRIMARY")){
            if (s2.toUpperCase().equals("KEY")){
                return true;
            }
        }
        return false;
    }
	
	private void addForeginKey(Writer writer, String foreignKeyNameSrc, String foreignKeyNameDst, Integer i){
        boolean found = false;
        boolean found2 = false;

		String[] fk = foreignKeyNameDst.split("\\.");
		String fkTable = fk[0];
		String fkColumn = fk[1];
		
		for (int p = 0; p < this.columns.size(); p++){
                if (foreignKeyNameSrc.equals(this.columns.get(p))){found2 = true;this.foreignKeyColumnSrc.add(p);}
            }
			if (found2==false){System.err.println("Can't find foreign key column "+foreignKeyNameSrc+" in this table");System.exit(1);}
		
        try {
			Table tableF = new Table();
			Record recordF = new Record();
			tableF.connectDatabase(this.database);
			tableF.putName(fkTable);
			if (tableF.checkTableExist() == false){System.out.println("Table "+fkTable+" doesn't exist");return;}
			tableF.initTable();
			recordF.connectTable(tableF); //this must be before putName
			recordF.putName(fkTable);
            for (int p = 0; p < tableF.columns.size(); p++){
                if (fkColumn.equals(tableF.columns.get(p))){writer.write ("FOREIGN_KEY " + foreignKeyNameSrc + " " + foreignKeyNameDst + "\n"); found = true;}
            }
        }
        catch (IOException e) {System.err.println("Problem writing to the file");}
        if (found == false){System.err.println("Can't find foreign key in the table" + fkTable);System.exit(1);}       
    }



    private boolean isForeignKey(String s1, String s2){
        if (s1.toUpperCase().equals("FOREIGN")){
            if (s2.toUpperCase().equals("KEY")){
                return true;
            }
        }
        return false;
    }
	

    private boolean checkAtrib(String attrib){
        if (attrib.toUpperCase().equals("INT")){return true;}
        if (attrib.toUpperCase().equals("CHAR")){return true;}
        if (attrib.toUpperCase().equals("STRING")){return true;}
		if (attrib.toUpperCase().equals("DOUBLE")){return true;}
        return false;
    }

    private void putPrimaryKey(String s){
        for (int p = 0; p < this.columns.size(); p++){
            if (s.equals(this.columns.get(p))){primaryKey = p;}
        }
    }

	private boolean putForeignKey(String foreignSrc, String foreigndst){
		String[] fk = foreigndst.split("\\.");
		String fkTable = fk[0];
		String fkColumn = fk[1];
		boolean found = false;
		boolean found2 = false;

		
		for (int p = 0; p < this.columns.size(); p++){
            if (foreignSrc.equals(this.columns.get(p))){foreignKeyColumnSrc.add(p); found2 = true;}
        }
		if (found2 == false){return false;}
		
			Table tableF = new Table();
			Record recordF = new Record();
			tableF.connectDatabase(this.database);
			tableF.putName(fkTable);
			if (tableF.checkTableExist() == false){System.out.println("Table "+fkTable+" doesn't exist");return false;}
			tableF.initTable();
			recordF.connectTable(tableF); //this must be before putName
			recordF.putName(fkTable);
            for (int p = 0; p < tableF.columns.size(); p++){
                if (fkColumn.equals(tableF.columns.get(p))){
					this.foreignKeyColumnDst.add(p);
					this.foreignKeyTable.add(fkTable);
					this.hasForeignKey = true;
					found = true;
					}
            }
        if (found == false){System.err.println("Wrong foreign key" + fkColumn);return false;}
		return true;

    }
	
	public boolean isColumn(String s, Integer count){
        if (this.columns.get(count).equals(s)){return true;}
		return false;
    }
	
	public String getColumn(Integer counter){
		String col = this.columns.get(counter);
		return col;
	}
	
	public String getAttribute(Integer counter){
		return this.attributes.get(counter);
	}



    //WHEN A TABLE IS LOADED FROM FILE THIS FUNCTION LOADS THE ATTRIBS TO THE OBJECT'S VARIABLES
    public boolean initTable (){
            Integer atCounter = 0;
            this.hasPrimaryKey = false;
			this.hasForeignKey = false;
    try {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            String[] lineSplit = line.split(" ");
            if (lineSplit[0].equals("PRIMARY_KEY")){
				this.hasPrimaryKey = true;
				putPrimaryKey(lineSplit[1]);
				}
            else if (lineSplit[0].equals("FOREIGN_KEY")){
				this.hasForeignKey = true;
				if (putForeignKey(lineSplit[1], lineSplit[2])==false){return false;};
				}
            else{
                this.columns.add(lineSplit[0]);
                this.attributes.add(lineSplit[1]);
                atCounter++;
            }

            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();

        }
        attributesNum = atCounter;
        String everything = sb.toString();
        br.close();
    }
        catch (IOException e) {System.err.println("Problem writing to the file");}
	return true;
    }

}
