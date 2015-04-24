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

class Row {

	public List<String> columns;
	public List<Integer> columnsWhere = new ArrayList<Integer>();
	public List<String> valuesWhere = new ArrayList<String>();
	public List<String> compWhere = new ArrayList<String>();
	public List<String> values = new ArrayList<String>();
	public List<String> newValues = new ArrayList<String>();
	public List<Integer> clmnNewValues = new ArrayList<Integer>();
	public boolean allSelected = false;


	

   // public String[] columns = new String[100];
	public boolean where = false;
    public Integer attributesNum;

	
	public boolean isSelected (){
		if (this.where == false){return true;}
		double v1 = 0;
		double v2 = 0;
		

		for (int i = 0 ; i < this.attributesNum ; i++){	
			int colToCompare = this.columnsWhere.get(i);
			String comparison = this.compWhere.get(i);
					
			if (comparison.equals("=")){
				if (this.values.get(colToCompare).equals(this.valuesWhere.get(i)) == false){return false;}	
			}
			else if (comparison.equals("<>")){
				if (this.values.get(colToCompare).equals(this.valuesWhere.get(i)) == true){return false;}	
			}
			if (comparison.equals(">") || comparison.equals("<") || comparison.equals(">=") || comparison.equals("<=")){
				try{
				v1 = Double.parseDouble(this.values.get(colToCompare));
				v2 = Double.parseDouble(this.valuesWhere.get(i));
				}
				catch (NumberFormatException e) {return false;}
			}
			if (comparison.equals(">") && v1 <= v2){return false;}
			else if (comparison.equals("<") && v1 >= v2){return false;}
			else if (comparison.equals(">=") && v1 < v2){return false;}
			else if (comparison.equals("<=") && v1 > v2){return false;}

			
	
		}	
		return true;	
	}

	
	public void putWhereData (Integer col, String comp, String value){
		this.columnsWhere.add(col);
		this.valuesWhere.add(value);
		this.compWhere.add(comp);
	}


	public void putNewValuesData (Integer col, String value){
		this.clmnNewValues.add(col);
		this.newValues.add(value);
	}
	
	public void renewLists (){
		this.values = new ArrayList<String>();
	}




    /*public void boolean isChosen(String count) {
	return false;
    }*/

}
