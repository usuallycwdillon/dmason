/**
 * Copyright 2016 Universita' degli Studi di Salerno


   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package it.isislab.dmason.experimentals.systemmanagement.master.web.utils;

import it.isislab.dmason.experimentals.systemmanagement.master.MasterServer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 
 * @author Michele Carillo
 * @author Carmine Spagnuolo
 * @author Flavio Serrapica
 *
 */
public class GetInfoForLogServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	MasterServer masterServer=null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		if(req.getServletContext().getAttribute("masterServer")==null)
			return;
		JSONObject file;
		JSONArray list_file = new JSONArray();
		PrintWriter printer = resp.getWriter();
		masterServer = (MasterServer) req.getServletContext().getAttribute("masterServer");
		String idSimulation = (String)req.getParameter("id");
		String logsPathName = "";
		
		logsPathName = masterServer.logRequestForSimulationByID(Integer.parseInt(idSimulation),"logreq");

		
		File log_root = new File(logsPathName);
		String sCurrentLine = null;
		RandomAccessFile raf=null;
		String content = "";
		int MAX_LINE_FOR_PREVIEW = 50;		
		List<String> last_lines = null;
		File[] list = log_root.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.endsWith(".out");
			}
		});
		
		if(log_root.isDirectory()){
			int num_line;
			for(File f: list){
				//System.out.println("leggo "+f.getName());
				if(f.exists()){
					content="";
					file = new JSONObject();
					//lf[i]={fileName:'file'+i,modifiedDate:"22/01/2016"};
					file.put("fileName", f.getName());
					Date date=new Date(f.lastModified());
					SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy");
					String dateText = df2.format(date);
					file.put("modifiedDate", dateText);
					if(f.canRead()){
						raf = new RandomAccessFile(f, "r");
						long length = raf.length();
						last_lines = new LinkedList<String>();
						//it starts to read from 4th part of file
						raf.seek(length-(length/4));
			
						while ((sCurrentLine = raf.readLine()) != null) {
							//read last MAX_LINE_FOR_PREVIEW lines
							if(last_lines.add(sCurrentLine) && last_lines.size()>MAX_LINE_FOR_PREVIEW)
								last_lines.remove(0);
						}
						for (int i = 0; i < last_lines.size(); i++) {
							content+=last_lines.get(i)+'\n';
						}
						file.put("content", content);
					}
					list_file.add(file);
				}
			}
		}
		if(raf!=null)
			raf.close();
		JSONObject json_files = new JSONObject();
		json_files.put("files", list_file);
		
		StringWriter out = new StringWriter();
		json_files.writeJSONString(out);

		String jsonText = out.toString();
		//System.out.println(jsonText);
		printer.print(jsonText);
		printer.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		doGet(req, resp);
	}
}
