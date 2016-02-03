package it.isislab.dmason.experimentals.systemmanagement.worker;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;

public class WorkerResourceInfo {

	private OperatingSystemMXBean  os=null;
	private MemoryMXBean memory=null;
	private MemoryUsage available=null;
	private MemoryUsage busy=null;
	private double cpuLoad=0.0;
	private static final double byte_giga=1073741824;
	private static final double byte_mega=1048576;
	

	
	public WorkerResourceInfo() {
		os = ManagementFactory.getOperatingSystemMXBean();
		memory = ManagementFactory.getMemoryMXBean();
	}


	public double getAvailableHeapGb(){	this.setAvailableHeapMemory();
	return available.getMax()/byte_giga;
	}

	public double getAvailableHeapMb(){this.setAvailableHeapMemory();
	return available.getMax()/byte_mega;
	}

	public double getBusyHeapGb(){this.setUsedHeapMemory();
	return busy.getUsed()/byte_giga;
	}
	public double getBusyHeapMb(){this.setUsedHeapMemory();
	return busy.getUsed()/byte_mega;
	}

	
	
	private void setCpuLoad(){cpuLoad=os.getSystemLoadAverage() ;}
	private void setAvailableHeapMemory(){available=memory.getNonHeapMemoryUsage();}
	private void setUsedHeapMemory(){busy= memory.getHeapMemoryUsage();}
	public double getCPULoad(){this.setCpuLoad();return cpuLoad;}

	
	//se vogliamo inviare l'oggetto
	class WorkerInfo implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String cpuLoad;
		private String availableheapmemory;
		private String busyheapmemory;
		
		public WorkerInfo() {}
		
		
		public WorkerInfo(String cpuLoad, String availableheapmemory,
				String busyheapmemory){
			this.cpuLoad = cpuLoad;
			this.availableheapmemory = availableheapmemory;
			this.busyheapmemory = busyheapmemory;
		}


		public void setCpuLoad(double x){this.cpuLoad=""+x;}
		public void setAvailableHeap(double x){this.availableheapmemory=""+x;}
		public void setBusyHeap(double x){this.busyheapmemory=""+x;}
		public String getCpuLoad(){return cpuLoad;}
		public String availableHeapMemory(){return availableheapmemory;}
		public String busyHeapMemory(){return busyheapmemory;}
	}
	
	/*
	public static void main(String[] args) throws MalformedObjectNameException, InstanceNotFoundException, NullPointerException, ReflectionException {
		
		double byte_giga=1073741824;
		double byte_mega=1048576;
		
		Runtime runtime=Runtime.getRuntime();
		NumberFormat format= NumberFormat.getInstance();
		
		
		long maxMemory = runtime.maxMemory(); //il massimoo della memoria che la vm tenta di usare
		long allocatedMemory = runtime.totalMemory();//la memoria disponibile per i processi attuali e futuri per la vm
		long freeMemory = runtime.freeMemory();//la memoria dipsonibili per i futuri oggetti allocati 
		long used =allocatedMemory-freeMemory;		
	
		
		MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
		MemoryUsage m= memory.getHeapMemoryUsage();
		//System.out.println(   Double.parseDouble(""+used)/byte_giga );
		//System.out.println(   Double.parseDouble(""+m.getUsed())/byte_giga );
		
		
		//MemoryUsage m1=memory.getNonHeapMemoryUsage();
		
		
		System.out.println(Double.parseDouble(""+m.getMax())/byte_giga);
		
		
		//System.out.println(Double.parseDouble(""+freeMemory)/byte_giga);
		
	   System.out.println(Double.parseDouble(""+allocatedMemory)/byte_giga);
		//System.out.println(Double.parseDouble(""+maxMemory)/byte_giga);//m.getmax
		//System.out.println( Double.parseDouble(""+   ((freeMemory + (maxMemory - allocatedMemory)) )/ byte_giga)   );
		

		
		
		//cpuLOAD
		//OperatingSystemMXBean  os = ManagementFactory.getOperatingSystemMXBean();
		//System.out.println("load cpu "+os.getSystemLoadAverage() );
		
		//memory available on vm 
		
		//memory busy on vm
		
		
	}
	*/
}
