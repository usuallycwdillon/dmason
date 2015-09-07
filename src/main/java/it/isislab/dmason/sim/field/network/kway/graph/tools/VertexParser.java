package it.isislab.dmason.sim.field.network.kway.graph.tools;


import it.isislab.dmason.annotation.AuthorAnnotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

@AuthorAnnotation(
		author = {"Alessia Antelmi", "Carmine Spagnuolo"},
		date = "20/7/2015"
		)
public class VertexParser {
	
	/**
	 * Reads a graph described as edgelist 
	 * and returns all the vertices of the graph
	 * with their id
	 * @param filepath - file.edgelist describing a graph
	 * @return ids - TreeMap containing vertices id, inserted as keys
	 */
	
	public static TreeMap<Integer, String> getOriginalIdFromEdgelist(String filepath){
		TreeMap<Integer, String> ids = new TreeMap<Integer, String>();
		String edge, separator;
		String vertices[] = new String[2];
	
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filepath))));
			
			while((edge = in.readLine()) != null){
				
				separator = Utility.findSeparator(edge);
				vertices = edge.split(separator);
				
				int firstId = Integer.parseInt(vertices[0]);
				int secondId = Integer.parseInt(vertices[1]);
				
				if(!ids.containsKey(firstId))
					ids.put(firstId, null);
				
				if(!ids.containsKey(secondId))
					ids.put(secondId, null);
				
			}
	
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ids;
	}
	

}