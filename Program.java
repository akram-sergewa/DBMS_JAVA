        import java.nio.file.Files;

    import java.util.Scanner; 
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.OutputStreamWriter;
    import java.io.Writer;
	import java.io.FilenameFilter;
	import javax.swing.*;
	import java.util.Vector;


class Program {
	
	private Vector<Vector> rowData = new Vector<Vector>();
	private Vector<String> columnNames = new Vector<String>();

    Table table;
    Record record;
    String database = "";
	Graphics graphics = new Graphics();
    public  void run() {
        File dir = new File("files");
        if (dir.exists() == false){dir.mkdir();}
		System.out.println("\n\n\n\n\n\n\n\n\nTYPE HELP; to get a list of commands");
			
		
        while (true){
			 System.out.println("\n\n------------------------\nDatabase Management System\n------------------------");
			 System.out.print("<"+database+">:->");
			 Scanner input = new Scanner(System.in);
			 String s = input.nextLine();
			 String[] theInput = inputMaker(s);
			 inputAnalazye(theInput);

        }

         
    }

    private void inputAnalazye(String[] input){
        if (input[0].toUpperCase().equals("CREATE") && input[1].toUpperCase().equals("TABLE")){ createAnalyzer(input); } 
        else if (input[0].toUpperCase().equals("INSERT") && input[1].toUpperCase().equals("INTO")){ insertAnalyzer(input); } 
        else if (input[0].toUpperCase().equals("SELECT")){ selectAnalyzer(input); }
        else if (input[0].toUpperCase().equals("CREATE") && input[1].toUpperCase().equals("DATABASE")){ createDBAnalyzer(input); }
        else if (input[0].toUpperCase().equals("USE")){ useAnalyzer(input); }
        else if (input[0].toUpperCase().equals("LIST") && input[1].toUpperCase().equals("DATABASES")){ listDBAnalyzer(input); }
        else if (input[0].toUpperCase().equals("LIST") && input[1].toUpperCase().equals("TABLES")){ listTBAnalyzer(input); }
        else if (input[0].toUpperCase().equals("UPDATE")){ updateAnalyzer(input); }
        else if (input[0].toUpperCase().equals("DELETE") && input[1].toUpperCase().equals("FROM")){ deleteAnalyzer(input); } 
		else if (input[0].toUpperCase().equals("EXIT") && input[1].toUpperCase().equals(";")){ System.exit(1); } 
		else if (input[0].toUpperCase().equals("HELP") && input[1].toUpperCase().equals(";")){ printHeader(); }
        else if (input[0].toUpperCase().equals("DROP") && input[1].toUpperCase().equals("TABLE")){ dropTbAnalyzer(input); } 
        else if (input[0].toUpperCase().equals("DROP") && input[1].toUpperCase().equals("DATABASE")){ dropDbAnalyzer(input); } 

    }

    private void createAnalyzer (String[] input){
        table = new Table();
        if (this.database == null){System.out.println("You have to make or access a database first");return;}
        table.connectDatabase(this.database);
        table.putName(input[2]);
        table.createCheck(input);
    }

    private void insertAnalyzer (String[] input){
        table = new Table();
        record = new Record();
        table.connectDatabase(this.database);
        table.putName(input[2]);
        if (table.checkTableExist() == false){System.out.println("Table doesn't exist");return;}
        table.initTable();
        record.connectTable(table);
        record.putName(input[2]);
        record.insertCheck(input);
    }

    private void selectAnalyzer (String[] input){
        String tableName; int i = 1;
		if (input.length < 3){System.out.println("Error with the query");return;}
        while (input[i++].toUpperCase().equals("FROM") == false){
			if (input.length == i + 1){System.out.println("Error with the query");return;}
		}
        table = new Table();
        record = new Record();
        table.connectDatabase(this.database);
        table.putName(input[i]);
        if (table.checkTableExist() == false){System.out.println("Table doesn't exist");return;}
        table.initTable();
        record.connectTable(table); //this must be before putName
        record.putName(input[i]);
        if (record.selectCheck(input) == true){
			//update graphics
			graphics.close();
			graphics.updateTable(record.getRow(), record.getColumn());
			SwingUtilities.invokeLater(graphics);
			}
    }

    private void createDBAnalyzer (String[] input){
        File dir = new File("files/" + input[2]);
        if (dir.exists()){System.out.println("DATABASE" + this.database + "already exist");return;}
        dir.mkdir();
        this.database = input[2];
        System.out.println("DATABASE " + this.database + " is created");
    }

    private void useAnalyzer (String[] input){
        File dir = new File("files/" + input[1]);
        if (dir.exists()){
            this.database = input[1];
            System.out.println("DATABASE " + this.database + " is connected");
            return;
        }
        System.out.println("DATABASE " + input[1] + " doesn't exist");

    }

    private void listDBAnalyzer (String[] input){
		rowData.clear();
		 columnNames.clear();
        File fname = new File("files");
      if (fname.isDirectory()) {
         File[] fileNames;
         fileNames = fname.listFiles();
		 columnNames.addElement("DATABASES LIST");
		 
		 
		 if (fileNames.length == 0){
			Vector<String> dbNameV = new Vector<String>();
			 dbNameV.addElement("NO DATABASES WERE FOUND");
			 rowData.addElement(dbNameV); 
		 }
		 
         for (int i = 0; i < fileNames.length; i++) {
			 String dbName = fileNames[i].getName().replace("files/", "");
            System.out.println(dbName);
			Vector<String> dbNameV = new Vector<String>();
			 dbNameV.addElement(dbName);
			 rowData.addElement(dbNameV);			
			}
		graphics.close();
		graphics.updateTable(rowData, columnNames);
			SwingUtilities.invokeLater(graphics);
		}
	}
	
	
	private void listTBAnalyzer (String[] input){
		rowData.clear();
		 columnNames.clear();
        File dir = new File("files/" + this.database);
         File[] tables = dir.listFiles(new FilenameFilter(){
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".table");
			}
		 });

		 columnNames.addElement("TABLE LIST");
		 	Vector<String> tbNameV = new Vector<String>();
		 if (tables.length == 0){
			 tbNameV.addElement("NO TABLES WERE FOUND");
			 rowData.addElement(tbNameV); 
		 }
		 
         for (int i = 0; i < tables.length; i++) {
			 String tbName = tables[i].getName().replace("files/"+this.database, "");
			 System.out.println(tbName);
			 Vector<String> dbNameV = new Vector<String>();
			 tbNameV.addElement(tbName);
			 rowData.addElement(tbNameV);
			}
			graphics.close();
			graphics.updateTable(rowData, columnNames);
			SwingUtilities.invokeLater(graphics);
			
	}

    private void updateAnalyzer (String[] input){
        String tableName;
        int i = 1;
        table = new Table();
        record = new Record();
        table.connectDatabase(this.database);
        table.putName(input[i]);
        if (table.checkTableExist() == false){System.out.println("Table doesn't exist");return;}
        table.initTable();
        record.connectTable(table); //this must be before putName
        record.putName(input[i]);
        record.updateCheck(input); 
    }
	
	private void deleteAnalyzer (String[] input){
        String tableName;
        int i = 2;
        table = new Table();
        record = new Record();
        table.connectDatabase(this.database);
        table.putName(input[i]);
        if (table.checkTableExist() == false){System.out.println("Table doesn't exist");return;}
        table.initTable();
        record.connectTable(table); //this must be before putName
        record.putName(input[i]);
        record.removeCheck(input); 
    }

    private void dropTbAnalyzer (String[] input){
        if (input.length <3){return;}
        String tableFile1 = "files/" +this.database+"/"+ input[2] + ".table";
        String tableFile2 = "files/" +this.database+"/"+ input[2] + ".record";

        try{
 
            File file = new File(tableFile1);
 
            if(file.delete()){System.out.println(input[2] + " table is deleted!");}
        }
        catch(Exception e){}
        try{
             File file = new File(tableFile2);
             if(file.delete()){System.out.println(input[2] + " record is deleted!");}
         }
        catch(Exception e){}
        
    }

    private void dropDbAnalyzer (String[] input){
        if (input.length <3){return;}
        String databaseFile = "files/" + input[2];

        try{
            File file = new File(databaseFile);
            String[]entries = file.list();
            for(String s: entries){
                File currentFile = new File(file.getPath(),s);
                currentFile.delete();
            }
 
            if(file.delete()){System.out.println(input[2] + " database is deleted!");}
            else{}
        }
        catch(Exception e){}
        
        
    }


    private String[] inputMaker (String s){
		s = s.replace("<=", "TEMPLessOrEqual");
        s = s.replace(">=", "TEMPGreaterOrEqual");
        s = s.replace("<>", "TEMPNotEqual");
        s = s.replace(",", " , ");
        s = s.replace("= ", " = ");
        s = s.replace(">", " > ");
        s = s.replace("< ", " < ");
        s = s.replace("TEMPLessOrEqual", "<=");
        s = s.replace("TEMPGreaterOrEqual", ">=");
        s = s.replace("TEMPNotEqual", "<>");
        s = s.replace(";", " ; ");
        s = s.replace("*", " * ");
        s = s.replace("}", " } ");
        s = s.replace("{", " { ");
		s = s.replace(")", " ) ");
        s = s.replace("(", " ( ");
		s = s + " ;";
        while (s.contains("  ")){
            s = s.replace("  ", " ");
        }
        String[] input = s.split(" ");
        return input;
    }

	private void printHeader(){
		System.out.println("-------------------------------------------------------------------");
		System.out.println("###################  Database Management System  ##################");
		System.out.println("-------------------------------------------------------------------");
		System.out.println("---------------------------BY AKRAM SERGEWA------------------------\n\n\n");
		System.out.println("The available commands");
		System.out.println("create database <name>;");
		System.out.println("use database <name>;");
		System.out.println("list databases;");
		System.out.println("list tables;");
		System.out.println("create table <name> {<columnName> <type> , <columnName> <type> , <columnName> <type> , PRIMARY KEY <columnName>};");
        System.out.println("create table <name> {<columnName> <type> , <columnName> <type> , <columnName> <type> , FOREIGN KEY <columnNameThisTable> <ForeignTableName.ForeignTableColumn>};");
		System.out.println("insert into <tableName> VALUES ( value1 , value2 , value3 etc..)");
		System.out.println("select * from <tableName>;");
		System.out.println("select * from <tableName> where column = value;");
		System.out.println("select <columnName>, <columnName> from <tableName> where <columnName> = <value>;");
		System.out.println("update <tableName> set <columnName> = <value> where <columnName> = <value>;");
		System.out.println("delete from <tableName> where <columnName> = <value>;");
        System.out.println("DATA TYPES (int , string , char , double)");
		System.out.println("exit; to exit");
		System.out.println("-------------------------------------------------------------------------------");
	}




}
