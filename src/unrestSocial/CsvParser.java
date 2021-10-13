package unrestSocial;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * this class is to read the CSV files by default it reads
 *  the vector_2016to2019 file but we can load other paths too.
 *  
 *  It takes the filepath as the input and output a list of list variable.
*/
public class CsvParser {

	String fileName ; 
	
	public CsvParser() {
		this.fileName = "./data/tn/weighted.csv";
	}
	
	public CsvParser(String path) {
		this.fileName = path;
	}
	
    public List<List<Double>> csvReader(){
        File file= new File(this.fileName);

        // this gives you a 2-dimensional array of strings
        List<List<Double>> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                String[] values = line.split(",");
                // this adds the currently parsed line to the 2-dimensional string array
                List<Double> temp = new ArrayList<>();
                for(String i:values) {
                	Double v = Double.parseDouble(i);
                	temp.add(v);
                }
                lines.add(temp);
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return lines;
    }

}
