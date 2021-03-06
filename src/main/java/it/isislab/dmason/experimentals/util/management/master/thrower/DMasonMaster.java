/**
 * Copyright 2012 Universita' degli Studi di Salerno


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

package it.isislab.dmason.experimentals.util.management.master.thrower;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import it.isislab.dmason.annotation.ThinAnnotation;
import it.isislab.dmason.experimentals.tools.batch.BatchExecutor;
import it.isislab.dmason.experimentals.tools.batch.BatchWizard.DistributionType;
import it.isislab.dmason.experimentals.tools.batch.data.Batch;
import it.isislab.dmason.experimentals.tools.batch.data.EntryParam;
import it.isislab.dmason.experimentals.tools.batch.data.EntryParam.ParamType;
import it.isislab.dmason.experimentals.tools.batch.data.EntryWorkerScore;
import it.isislab.dmason.experimentals.tools.batch.data.GeneralParam;
import it.isislab.dmason.experimentals.tools.batch.data.Param;
import it.isislab.dmason.experimentals.tools.batch.data.ParamDistribution;
import it.isislab.dmason.experimentals.tools.batch.data.ParamDistributionExponential;
import it.isislab.dmason.experimentals.tools.batch.data.ParamDistributionNormal;
import it.isislab.dmason.experimentals.tools.batch.data.ParamDistributionUniform;
import it.isislab.dmason.experimentals.tools.batch.data.ParamFixed;
import it.isislab.dmason.experimentals.tools.batch.data.ParamList;
import it.isislab.dmason.experimentals.tools.batch.data.ParamRange;
import it.isislab.dmason.experimentals.util.management.DigestAlgorithm;
import it.isislab.dmason.experimentals.util.management.JarClassLoader;
import it.isislab.dmason.experimentals.util.management.Release;
import it.isislab.dmason.experimentals.util.management.garbagecollector.Start;
import it.isislab.dmason.experimentals.util.management.master.EntryVal;
import it.isislab.dmason.experimentals.util.management.master.MasterDaemonStarter;
import it.isislab.dmason.experimentals.util.management.master.ModelPanel;
import it.isislab.dmason.experimentals.util.management.worker.Digester;
import it.isislab.dmason.experimentals.util.management.worker.PeerStatusInfo;
import it.isislab.dmason.experimentals.util.management.worker.UpdateData;
import it.isislab.dmason.experimentals.util.management.worker.WorkerUpdater;
import it.isislab.dmason.experimentals.util.management.worker.thrower.DMasonWorkerWithGui;
import it.isislab.dmason.experimentals.util.trigger.Trigger;
import it.isislab.dmason.experimentals.util.trigger.TriggerListener;
import it.isislab.dmason.sim.field.DistributedField2D;
import it.isislab.dmason.util.StdRandom;
import it.isislab.dmason.util.Util;
import it.isislab.dmason.util.connection.Address;
import it.isislab.dmason.util.connection.ConnectionType;
import it.isislab.dmason.util.connection.MyHashMap;
import it.isislab.dmason.util.connection.jms.ActiveMQManager;
import it.isislab.dmason.util.connection.jms.activemq.ConnectionNFieldsWithActiveMQAPI;
import it.isislab.dmason.util.connection.jms.activemq.MyMessageListener;
import sim.display.Console;
/**

 * @author Michele Carillo
 * @author Fabio Fulgido
 * @author Ada Mancuso
 * @author Dario Mazzeo
 * @author Francesco Milone
 * @author Francesco Raia
 * @author Flavio Serrapica
 * @author Carmine Spagnuolo
 * @author Luca Vicidomini
 * @author Mario Fiore Vitale
 *
 */
public class DMasonMaster extends JFrame  implements Observer{

	private int rows,columns;
	private static final String FTP_HOME = "FTPHome";
	private static final String SIMULATION_DIR = "simulation";
	private static final String UPDATE_DIR = "update";
	private static final String FTP_PORT = "18786";

	private static final int RMI_PORT = 61617;
	private static String rmiIp;

	private static boolean autoconnect = false;

	private static boolean enableWorkersUpdate = true;

	private File updateFile;
	private File simulationFile;
	private File configFile;
	private String xsdFilename = "batchSchema.xsd";
	private static String dirSeparator;
	private boolean isHorizontal=true;
	private WorkerUpdater wu;
	List<EntryWorkerScore<Integer, String>> scoreList = new ArrayList<EntryWorkerScore<Integer,String>>();


	private JMenuItem menuNewSim;
	private JScrollPane scrollPaneTree;	//scrollPaneTree
	private JLabel advancedConfirmBut;	//advancedConfirmBut;
	private JCheckBox graphicONcheckBox;
	private JButton buttonSetConfigAdvanced;
	private JLabel jLabelPlayButton;	//labelstopbutton
	private JLabel jLabelPauseButton;	//labelpausebutton
	private JPanel jPanelNumStep;		//panel1
	private JLabel writeStepLabel;		//labelWriteStep;
	private JLabel jLabelStep;	//step
	private JLabel jLabelStopButton;	//labelStopButton2
	private String selectedSimulation;
	private JScrollPane scrollPane1;
	private JDesktopPane peerInfoStatus1;
	private MasterDaemonStarter master;
	private String ip;
	private String port;
	private DefaultMutableTreeNode root;
	private boolean connected = false;
	private HashMap<String,EntryVal<Integer,Boolean>> config;
	private int total=0;
	private JMenuItem jMenuItemUpdateWorker;
	private JMenu jMenuSystem;
	private JButton jButtonChoseSimJar;
	private JTextField jTextFieldPathSimJar;
	private JButton jButtonLoadJar;
	private JPanel jPanelDeploying;
	private JLabel jLabelResetButton;
	private JCheckBox jCheckBoxLoadBalancing;
	boolean radioItemFlag=false;	//radioItemFalg
	private Address address;
	int res;
	private ConnectionNFieldsWithActiveMQAPI connection;
	private ConnectionNFieldsWithActiveMQAPI connectionForSteps;
	private Start starter;
	private Console c;	
	private boolean isSubmitted = false;
	private BatchExecutor batchExec;
	private boolean isBatchTest = false;
	private long batchStartedTime;
	private AtomicInteger testCount = new AtomicInteger();
	private int totalTests;
	private boolean dont=true;
	private int MODE;
	private int WIDTH;
	private int HEIGHT;
	private int numRegions;
	private int numAgents;
	private int maxDistance;
	private DMasonMaster me = this;
	//private JFileChooser files;
	public boolean centralGui = true;
	private static final long serialVersionUID = 1L;
	private boolean withGui = false;
	private JTree tree1;
	private JTextArea architectureLabel;
	private JDesktopPane peerInfoStatus;
	private JInternalFrame internalFrame1;
	private JPanel panelConsole;
	private JMenuBar menuBar1;
	private JMenu jMenuFile;
	//private JMenuItem menuItemOpen;
	private JMenuItem menuItemExit;
	private JMenu jMenuAbout;
	private JMenuItem menuItemInfo;
	private JMenuItem menuItemHelp;
	private JPanel panelMain;
	private JPanel jPanelContainerConnection;
	private JPanel jPanelConnection;
	private JLabel jLabelAddress;
	private JTextField textFieldAddress;
	private JLabel jLabelPort;
	private JTextField textFieldPort;
	private JLabel refreshServerLabel;
	private JButton buttonRefreshServerLabel;
	private JPanel jPanelContainerSettings;
	private JPanel jPanelSetDistribution;
	private JPanel jPanelSettings;
	private JLabel jLabelHorizontal;
	private JLabel jLabelSquare;
	private JLabel jLabelMaxDistance;
	private JLabel jLabelWidth;
	private JTextField textFieldMaxDistance;
	private JComboBox jComboBoxNumRegionXPeer;
	private JTextField textFieldWidth;
	private JLabel jLabelHeight;
	private JTextField textFieldHeight;
	private JLabel jLabelAgents;
	private JTextField textFieldAgents;
	private JLabel jLabelChooseSimulation;
	private JComboBox jComboBoxChooseSimulation;
	private JPanel jPanelContainerTabbedPane;
	private JTabbedPane tabbedPane2;
	private JPanel jPanelDefault;
	private ModelPanel jPanelSimulation;
	private JLabel labelSimulationConfigSet;
	private JLabel labelRegionsResume;
	private JLabel labelNumOfPeerResume;
	private JLabel labelRegForPeerResume;
	private JLabel labelWriteReg;
	private JLabel labelWriteNumOfPeer;
	private JLabel labelWriteRegForPeer;
	private JLabel labelWidthRegion;
	private JLabel labelheightRegion;
	private JLabel labelDistrMode;
	private JLabel labelWriteRegWidth;
	private JLabel labelWriteRegHeight;
	private JLabel labelWriteDistrMode;
	private JCheckBox graphicONcheckBox2;
	private JPanel jPanelSetButton;
	private JButton buttonSetConfigDefault;
	private JPanel jPanelAdvanced;
	private JPanel jPanelAdvancedMain;
	private JPanel jPanelSetButton2;
	private JButton buttonSetConfigDefault2;
	private JScrollPane scrollPane3;
	private JScrollPane scrollPane4;
	private JTextArea notifyArea;
	// codice profiling
	private static Logger logger;
	FileAppender file;
	private int limitStep;
	private JLabel lblTotalSteps;
	private int workerUpdated = 0;
	private ArrayList<String> peers;
	private ArrayList<String> toUpdate = new ArrayList<String>();
	private int totPeers = 0;
	private String workerJarName;
	private String curWorkerDigest;
	private JPanel jPanelRunBatchTests;
	private boolean enableReset;
	private JTextField textFieldConfigFilePath;
	private JProgressBar progressBarBatchTest;
	private int fineshed = 0;
	private JCheckBox chckbxParallelBatch;
	protected Logger batchLogger;
	private JTextField textFieldRows;
	private JTextField textFieldColumns;
	protected boolean isThin;
	private JLabel lblStatusIcon;
	private JButton buttonActiveMQRestart;
	private JButton buttonActiveMQStart;
	private JButton buttonActiveMQStop;
	private ActiveMQManager csManager;
	private JButton btnCheckPeers;
	private JTextArea textAreaBatchInfo;
	// fine codice profiling
	private JCheckBox jCheckBoxMPI;
	private JTextField textFieldSteps;
	private JLabel jLabelInsertSteps;
	private int steps;


	/**
	 * @author Michele Carillo
	 * @author Ada Mancuso
	 * @author Dario Mazzeo
	 * @author Francesco Milone
	 * @author Francesco Raia
	 * @author Flavio Serrapica
	 * @author Carmine Spagnuolo
	 * @author Luca Vicidomini
	 */
	class SimComboEntry
	{
		/**
		 * A short name that will be shown to the user.
		 */
		String shortName;

		/**
		 * Qualified name of the class implementing the proper simulation.
		 */
		String fullSimName;

		/**
		 * Creates a new entry for the combobox.
		 * @param shortName A short name that will be shown to the user.
		 * @param fullSimName Qualified name of the class implementing the proper simulation.
		 */
		public SimComboEntry(String shortName, String fullSimName) { this.shortName = shortName; this.fullSimName = fullSimName; }
		@Override public String toString() { return shortName; }
	}



	public DMasonMaster()
	{	
		// Get the path from which MasterUI was started
		String path;
		try {
			path = URLDecoder.decode(DMasonWorkerWithGui.class.getProtectionDomain().getCodeSource().getLocation().getFile(),"UTF-8");
			logger.debug("Path: " + path);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			logger.error("Unable to retrieve current working directory");
			path = null;
		}


		enableReset = true;
		// disabled for debug
		/*if(path.contains(".jar")) //from jar
			enableReset = true;
		else
			enableReset = false;
		 */
		initComponents();
		setSystemSettingsEnabled(false);
		config = new HashMap<String, EntryVal<Integer,Boolean>>();
		setTitle("JMasterUI");
		initializeDefaultLabel();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);
		starter = new Start();



		curWorkerDigest = getCurWorkerDigest();

		// Don't check workers' version on autoconnect (intended for easymode)
		if(curWorkerDigest == null && !autoconnect)
			loadUpdateFile();

		if(rmiIp != null)
		{
			csManager = new ActiveMQManager(rmiIp, 61617); //"172.16.15.127"
			checkCommunicationServerStatus();
			textFieldAddress.setText(rmiIp);
			if(!csManager.isUnknow())
				setEnableActiveMQControl(true);
		}
		else
		{
			setEnableActiveMQControl(false);
		//	lblStatusIcon.setIcon(new ImageIcon("it.isislab.dmason/resources/image/status-unknow.png")));
			lblStatusIcon.setIcon(new ImageIcon("resources/image/status-unknow.png"));
		}
	}

	private void setEnableActiveMQControl(boolean flag)
	{
		buttonActiveMQStart.setEnabled(flag);
		buttonActiveMQStop.setEnabled(flag);
		buttonActiveMQRestart.setEnabled(flag);
	}
	private void checkCommunicationServerStatus() {

		if(csManager.isUnknow())
//			lblStatusIcon.setIcon(new ImageIcon("it.isislab.dmason/resources/image/status-unknow.png")));
			lblStatusIcon.setIcon(new ImageIcon("resources/image/status-unknow.png"));
		else
		{
			if(csManager.isStarted())
				lblStatusIcon.setIcon(new ImageIcon("resources/image/status-up.png"));
			else
				lblStatusIcon.setIcon(new ImageIcon("resources/image/status-down.png"));
		}

	}
	public static DMasonMaster newIstance()
	{
		System.setProperty("java.security.policy", "grant {permission java.security.AllPermission;};");

		// Set the name of logger in log4j.properties, then loads the logger
		System.setProperty("masterlogfile.name", "master-ui");
		logger = Logger.getLogger(DMasonMaster.class.getCanonicalName());

		dirSeparator = System.getProperty("file.separator");


		return new DMasonMaster();
	}
	public static void main(String[] args){	
		// Needed for Java RMI support
		System.setProperty("java.security.policy", "grant {permission java.security.AllPermission;};");
		// Set the name of logger in log4j.properties, then loads the logger
		System.setProperty("masterlogfile.name", "master-ui");
		logger = Logger.getLogger(DMasonMaster.class.getCanonicalName());

		// Set directory separator
		dirSeparator = System.getProperty("file.separator");

		// Check if FTP directories exist, or create them
		checkFtpDirectories();

		if(args.length >= 1)
			rmiIp = args[0];

		if(args.length == 0)
			rmiIp = null;

		if (args.length >= 2 && args[1].equals("autoconnect") )
		{
			autoconnect = true;
		}

		if(args.length > 1 && !autoconnect)
		{
			System.out.println("Usage JMasterUI IP [autoconnect]");
			System.exit(0);
		}

		JFrame a = new DMasonMaster();
		a.setVisible(true);
		if (autoconnect)
		{
			((DMasonMaster)a).connect();
		}

		startFtpServer();

	}


	private void initComponents() {
		menuBar1 = new JMenuBar();
		jMenuFile = new JMenu();
		//menuItemOpen = new JMenuItem();
		menuItemExit = new JMenuItem();
		jMenuAbout = new JMenu();
		menuItemInfo = new JMenuItem();
		menuItemHelp = new JMenuItem();
		panelMain = new JPanel();
		jPanelContainerConnection = new JPanel();
		jPanelConnection = new JPanel();
		jLabelAddress = new JLabel();
		textFieldAddress = new JTextField();
		jLabelPort = new JLabel();
		textFieldPort = new JTextField();
		refreshServerLabel = new JLabel();
		buttonRefreshServerLabel = new JButton();
		jPanelContainerSettings = new JPanel();
		jPanelSetDistribution = new JPanel();
		jPanelSettings = new JPanel();
		jLabelHorizontal = new JLabel();
		jLabelSquare = new JLabel();
		jLabelMaxDistance = new JLabel();
		jLabelWidth = new JLabel();
		jLabelInsertSteps = new JLabel();
		textFieldMaxDistance = new JTextField();
		textFieldWidth = new JTextField();
		jLabelHeight = new JLabel();
		textFieldHeight = new JTextField();
		jLabelAgents = new JLabel();
		textFieldAgents = new JTextField();
		textFieldColumns = new JTextField();
		textFieldRows = new JTextField();
		textFieldSteps = new JTextField();
		jLabelChooseSimulation = new JLabel();
		jComboBoxChooseSimulation = new JComboBox();
		jComboBoxNumRegionXPeer = new JComboBox();
		jPanelContainerTabbedPane = new JPanel();
		tabbedPane2 = new JTabbedPane();
		jPanelDefault = new JPanel();
		jPanelSimulation = new ModelPanel(tabbedPane2);
		labelSimulationConfigSet = new JLabel();
		labelRegionsResume = new JLabel();
		labelNumOfPeerResume = new JLabel();
		labelRegForPeerResume = new JLabel();
		labelWriteReg = new JLabel();
		labelWriteNumOfPeer = new JLabel();
		labelWriteRegForPeer = new JLabel();
		labelWidthRegion = new JLabel();
		labelheightRegion = new JLabel();
		labelDistrMode = new JLabel();
		labelWriteRegWidth = new JLabel();
		labelWriteRegHeight = new JLabel();
		labelWriteDistrMode = new JLabel();
		graphicONcheckBox2 = new JCheckBox();
		jPanelSetButton = new JPanel();
		buttonSetConfigDefault = new JButton();
		jPanelAdvanced = new JPanel();
		jPanelAdvancedMain = new JPanel();
		peerInfoStatus = new JDesktopPane();
		internalFrame1 = new JInternalFrame();
		architectureLabel = new JTextArea();
		architectureLabel.setBackground(Color.BLACK);
		architectureLabel.setForeground(Color.GREEN);
		architectureLabel.setEditable(false);
		advancedConfirmBut = new JLabel();
		graphicONcheckBox = new JCheckBox();
		jCheckBoxLoadBalancing = new JCheckBox("Load Balancing", false);
		jCheckBoxLoadBalancing.setEnabled(true);
		jCheckBoxLoadBalancing.setSelected(false);

		jCheckBoxMPI = new JCheckBox("Enable MPI", false);
		jCheckBoxMPI.setEnabled(true);
		jCheckBoxMPI.setSelected(false);

		scrollPaneTree = new JScrollPane();
		tree1 = new JTree();
		buttonSetConfigAdvanced = new JButton();
		jLabelPlayButton = new JLabel();
		jLabelPauseButton = new JLabel();
		jPanelNumStep = new JPanel();
		jLabelStep = new JLabel();
		jLabelStep.setHorizontalAlignment(SwingConstants.LEFT);
		jLabelStopButton = new JLabel();
		scrollPane1 = new JScrollPane();
		peerInfoStatus1 = new JDesktopPane();
		root = new DefaultMutableTreeNode("Simulation");
		ButtonGroup b = new ButtonGroup();
		ip = textFieldAddress.getText();
		port = textFieldPort.getText();
		menuBar1 = new JMenuBar();
		jMenuFile = new JMenu();
		menuItemExit = new JMenuItem();
		menuNewSim = new JMenuItem();
		jMenuAbout = new JMenu();
		menuItemInfo = new JMenuItem();
		menuItemHelp = new JMenuItem();
		scrollPane3 = new JScrollPane();
		scrollPane4 = new JScrollPane();
		notifyArea = new JTextArea();
		panelConsole = new JPanel();
		buttonSetConfigDefault2 = new JButton();
		jPanelSetButton2 = new JPanel();
		graphicONcheckBox = new JCheckBox();
		graphicONcheckBox.setEnabled(false);
		graphicONcheckBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				withGui = graphicONcheckBox.isSelected();
			}
		});

		jLabelChooseSimulation = new JLabel();
		jComboBoxChooseSimulation = new JComboBox();


		loadSimulation();

		selectedSimulation = ((SimComboEntry)jComboBoxChooseSimulation.getSelectedItem()).fullSimName;
		jPanelSimulation.updateHTML(selectedSimulation);
		jComboBoxChooseSimulation.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// Prevent executing listener's actions two times
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				selectedSimulation = ((SimComboEntry)jComboBoxChooseSimulation.getSelectedItem()).fullSimName;
				jPanelSimulation.updateHTML(selectedSimulation);
				isThin=isThinSimulation(selectedSimulation);
				jCheckBoxLoadBalancing.setSelected(false);
				jCheckBoxLoadBalancing.setEnabled(!isThin);
				initializeDefaultLabel();
			}
		});


		/*for(int i=2;i<100;i++)
			jComboRegions.addItem(i);*/

		buttonRefreshServerLabel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				connect();
			}
		});



		refreshServerLabel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (starter.isConnected())
					starter.execute("restart");
				else
					JOptionPane.showMessageDialog(null,"Not connected to the Server!");
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});

		jCheckBoxLoadBalancing.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!isHorizontal){
					if(jCheckBoxLoadBalancing.isSelected())
						labelWriteDistrMode.setText("SQUARE BALANCED MODE");
					else
						labelWriteDistrMode.setText("SQUARE MODE");
				}

				if(isHorizontal){
					if(jCheckBoxLoadBalancing.isSelected())
						labelWriteDistrMode.setText("HORIZONTAL BALANCED MODE");
					else
						labelWriteDistrMode.setText("HORIZONTAL MODE");
				}

			}
		});

		buttonSetConfigDefault2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(initializeDefaultLabel())
					submitCustomizeMode();
				else
					JOptionPane.showMessageDialog(null, "To start a simulation must fill in all fields...!");
			}
		});

		buttonSetConfigDefault.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(initializeDefaultLabel())
					submitDefaultMode();
				else
					JOptionPane.showMessageDialog(null, "To start a simulation must fill in all fields...!");
			}
		});

		advancedConfirmBut.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				confirm();
				res -= (Integer)jComboBoxNumRegionXPeer.getSelectedItem();

				withGui = graphicONcheckBox.isSelected();

				jComboBoxNumRegionXPeer.removeAllItems();
				graphicONcheckBox.setSelected(false);
				for(int i=1;i<=res;i++)
					jComboBoxNumRegionXPeer.addItem(i);

				JOptionPane.showMessageDialog(null,"Region assigned !");

			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseClicked(MouseEvent arg0) {}
		});


		//======== this ========

		Container contentPane = getContentPane();

		//======== menuBar1 ========
		{
			{
				jMenuFile.setText("    File    ");



				menuNewSim.setText("New    ");
				menuNewSim.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						me.dispose();
						me = null;
						DMasonMaster p = new DMasonMaster();
						p.setVisible(true);
					}
				});

				jMenuFile.add(menuNewSim);



				//---- menuItemExit ----
				menuItemExit.setText("Exit");
				menuItemExit.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						me.dispose();
					}
				});

				jMenuFile.add(menuItemExit);
			}
			menuBar1.add(jMenuFile);
			menuBar1.add(getJMenuSystem());

			//======== jMenuAbout ========
			{
				jMenuAbout.setText(" ?  ");

				//---- menuItemInfo ----
				menuItemInfo.setText("Info");
				menuItemInfo.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(null, Release.PRODUCT_RELEASE, "Info", 1);


					}
				});



				jMenuAbout.add(menuItemInfo);

				//---- menuItenHelp ----
				menuItemHelp.setText("Help");
				menuItemHelp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						try {
							java.net.URI uri = new java.net.URI("http://isis.dia.unisa.it/projects/it.isislab.dmason/");
							try {
								java.awt.Desktop.getDesktop().browse(uri);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}

					}
				});




				jMenuAbout.add(menuItemHelp);


			}
			menuBar1.add(jMenuAbout);
		}

		setJMenuBar(menuBar1);

		//======== panelMain ========
		{

			//======== jPanelContainerConnection ========
			{

				//======== jPanelConnection ========
				{
					jPanelConnection.setBorder(new TitledBorder("Connection"));
					jPanelConnection.setPreferredSize(new Dimension(215, 125));

					//---- jLabelAddress ----
					jLabelAddress.setText("IP Address :");

					//---- textFieldAddress ----
					textFieldAddress.setText("127.0.0.1");

					//---- jLabelPort ----
					jLabelPort.setText("Port :");

					//---- textFieldPort ----
					textFieldPort.setText("61616");

					//---- refreshServerLabel ----

					refreshServerLabel.setIcon(new ImageIcon("resources/image/refresh.png"));

					//---- buttonRefreshServerLabel ----
					buttonRefreshServerLabel.setText("OK");

					JLabel lblStatus = new JLabel("Communication Server status :");

					lblStatusIcon = new JLabel("");
					lblStatusIcon.setIcon(new ImageIcon("resources/image/status-down.png"));

					buttonActiveMQRestart = new JButton("");
					buttonActiveMQRestart.setEnabled(false);
					buttonActiveMQRestart.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {

							notifyArea.append("ActiveMQ restarting...\n");
							if(csManager.restartActiveMQ())
								notifyArea.append("ActiveMQ restarted!\n");

							checkCommunicationServerStatus();

						}
					});
					buttonActiveMQRestart.setMinimumSize(new Dimension(24, 24));
					buttonActiveMQRestart.setMaximumSize(new Dimension(24, 24));
					buttonActiveMQRestart.setPreferredSize(new Dimension(24, 24));
					buttonActiveMQRestart.setIcon(new ImageIcon("resources/image/LH2 - Restart.png"));

					buttonActiveMQStart = new JButton("");
					buttonActiveMQStart.setEnabled(false);
					buttonActiveMQStart.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							notifyArea.append("ActiveMQ starting...\n");
							if(csManager.startActiveMQ())
								notifyArea.append("ActiveMQ started!\n");

							checkCommunicationServerStatus();
						}
					});
					buttonActiveMQStart.setPreferredSize(new Dimension(24, 24));
					buttonActiveMQStart.setMinimumSize(new Dimension(24, 24));
					buttonActiveMQStart.setMaximumSize(new Dimension(24, 24));
					buttonActiveMQStart.setIcon(new ImageIcon("resources/image/LH2 - Shutdown.png"));

					buttonActiveMQStop = new JButton("");
					buttonActiveMQStop.setEnabled(false);
					buttonActiveMQStop.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							notifyArea.append("ActiveMQ stopping...\n");
							if(csManager.stopActiveMQ())
								notifyArea.append("ActiveMQ stopped!\n");

							checkCommunicationServerStatus();
						}
					});
					buttonActiveMQStop.setPreferredSize(new Dimension(24, 24));
					buttonActiveMQStop.setMinimumSize(new Dimension(24, 24));
					buttonActiveMQStop.setMaximumSize(new Dimension(24, 24));
					buttonActiveMQStop.setIcon(new ImageIcon("resources/image/LH2 - Stop.png"));

					btnCheckPeers = new JButton("Check Peers");
					btnCheckPeers.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								checkPeers();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});

					GroupLayout jPanelConnectionLayout = new GroupLayout(jPanelConnection);
					jPanelConnectionLayout.setHorizontalGroup(
							jPanelConnectionLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(jPanelConnectionLayout.createSequentialGroup()
									.addGroup(jPanelConnectionLayout.createParallelGroup(Alignment.LEADING)
											.addGroup(jPanelConnectionLayout.createSequentialGroup()
													.addGroup(jPanelConnectionLayout.createParallelGroup(Alignment.TRAILING)
															.addComponent(refreshServerLabel)
															.addGroup(jPanelConnectionLayout.createSequentialGroup()
																	.addComponent(buttonActiveMQStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(buttonActiveMQRestart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(buttonActiveMQStop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addGap(111)
																	.addComponent(jLabelAddress)
																	.addGap(18)
																	.addComponent(textFieldAddress, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
																	.addGap(18)
																	.addComponent(jLabelPort)
																	.addGap(18)
																	.addComponent(textFieldPort, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
																	.addGap(46)
																	.addComponent(buttonRefreshServerLabel)))
																	.addGap(51)
																	.addComponent(btnCheckPeers))
																	.addGroup(jPanelConnectionLayout.createSequentialGroup()
																			.addComponent(lblStatus)
																			.addPreferredGap(ComponentPlacement.RELATED)
																			.addComponent(lblStatusIcon)))
																			.addContainerGap(71, Short.MAX_VALUE))
							);
					jPanelConnectionLayout.setVerticalGroup(
							jPanelConnectionLayout.createParallelGroup(Alignment.TRAILING)
							.addGroup(jPanelConnectionLayout.createSequentialGroup()
									.addGroup(jPanelConnectionLayout.createParallelGroup(Alignment.LEADING)
											.addGroup(jPanelConnectionLayout.createSequentialGroup()
													.addGroup(jPanelConnectionLayout.createParallelGroup(Alignment.LEADING)
															.addComponent(lblStatus)
															.addComponent(lblStatusIcon))
															.addPreferredGap(ComponentPlacement.RELATED)
															.addGroup(jPanelConnectionLayout.createParallelGroup(Alignment.LEADING)
																	.addComponent(buttonActiveMQRestart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addComponent(buttonActiveMQStart, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addComponent(buttonActiveMQStop, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																	.addPreferredGap(ComponentPlacement.RELATED, 1, Short.MAX_VALUE))
																	.addGroup(jPanelConnectionLayout.createSequentialGroup()
																			.addContainerGap(18, Short.MAX_VALUE)
																			.addComponent(refreshServerLabel)
																			.addPreferredGap(ComponentPlacement.RELATED)
																			.addGroup(jPanelConnectionLayout.createParallelGroup(Alignment.BASELINE)
																					.addComponent(buttonRefreshServerLabel)
																					.addComponent(jLabelPort)
																					.addComponent(textFieldPort, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
																					.addComponent(jLabelAddress)
																					.addComponent(textFieldAddress, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
																					.addComponent(btnCheckPeers))))
																					.addGap(10))
							);
					jPanelConnectionLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {buttonActiveMQRestart, buttonActiveMQStart, buttonActiveMQStop});
					jPanelConnection.setLayout(jPanelConnectionLayout);
				}

				GroupLayout jPanelContainerConnectionLayout = new GroupLayout(jPanelContainerConnection);
				jPanelContainerConnection.setLayout(jPanelContainerConnectionLayout);
				jPanelContainerConnectionLayout.setHorizontalGroup(
						jPanelContainerConnectionLayout.createParallelGroup()
						.addGroup(jPanelContainerConnectionLayout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jPanelConnection, GroupLayout.PREFERRED_SIZE, 829, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(153, Short.MAX_VALUE))
						);
				jPanelContainerConnectionLayout.setVerticalGroup(
						jPanelContainerConnectionLayout.createParallelGroup()
						.addComponent(jPanelConnection, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
						);
			}

			//======== jPanelContainerSettings ========
			{

				GroupLayout jPanelContainerSettingsLayout = new GroupLayout(jPanelContainerSettings);
				jPanelContainerSettings.setLayout(jPanelContainerSettingsLayout);
				jPanelContainerSettingsLayout.setHorizontalGroup(
						jPanelContainerSettingsLayout.createParallelGroup()
						.addGap(0, 1, Short.MAX_VALUE)
						);
				jPanelContainerSettingsLayout.setVerticalGroup(
						jPanelContainerSettingsLayout.createParallelGroup()
						.addGap(0, 481, Short.MAX_VALUE)
						);
			}

			//======== jPanelSetDistribution ========
			{
				jPanelSetDistribution.setBorder(new TitledBorder("Settings"));

				//======== jPanelSettings ========
				{

					//jLabelHorizontal.setIcon(new ImageIcon("it.isislab.dmason/resources/image/hori.png")));

					//---- jLabelSquare ----
					//jLabelSquare.setIcon(new ImageIcon("it.isislab.dmason/resources/image/square.png")));

					//---- jLabelMaxDistance ----
					jLabelMaxDistance.setText("MAX_DISTANCE :");

					//---- jLabelWidth ----
					jLabelWidth.setText("WIDTH :");

					//---- jLabelInsertSteps ----
					jLabelInsertSteps.setText("STEPS :");

					//---- textFieldMaxDistance ----
					textFieldMaxDistance.setText("1");

					//---- textFieldWidth ----
					textFieldWidth.setText("200");

					//---- jLabelHeight ----
					jLabelHeight.setText("HEIGHT :");

					//---- textFieldHeight ----
					textFieldHeight.setText("200");

					//---- jLabelAgents ----
					jLabelAgents.setText("AGENTS :");

					//---- textFieldAgents ----
					textFieldAgents.setText("15");

					//---- textFieldSteps
					textFieldSteps.setText("100");



					MouseListener textFieldMouseListener = new MouseListener() {

						@Override
						public void mouseReleased(MouseEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void mousePressed(MouseEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void mouseExited(MouseEvent e) {
							initializeDefaultLabel();
						}

						@Override
						public void mouseEntered(MouseEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void mouseClicked(MouseEvent e) {
							// TODO Auto-generated method stub

						}
					};

					textFieldAgents.addMouseListener(textFieldMouseListener);
					textFieldAgents.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) ||
									v == KeyEvent.VK_BACK_SPACE
									)
							{
								e.consume();
							}
							if(textFieldAgents.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent e) {
							//initializeDefaultLabel();
						}

						@Override
						public void keyPressed(KeyEvent e) {}
					});


					textFieldColumns.addMouseListener(textFieldMouseListener);
					textFieldColumns.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							// TODO Auto-generated method stub
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) ||
									v == KeyEvent.VK_BACK_SPACE)
							{
								e.consume();
							}
							if(textFieldColumns.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void keyPressed(KeyEvent e) {
							// TODO Auto-generated method stub
						}
					});


					textFieldMaxDistance.addMouseListener(textFieldMouseListener);
					textFieldMaxDistance.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) ||
									v == KeyEvent.VK_BACK_SPACE)
							{
								e.consume();
							}
							if(textFieldMaxDistance.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent e) {
							//initializeDefaultLabel();
						}

						@Override
						public void keyPressed(KeyEvent e) {}
					});


					textFieldWidth.addMouseListener(textFieldMouseListener);
					textFieldWidth.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) ||
									v == KeyEvent.VK_BACK_SPACE)
							{
								e.consume();
							}
							if(textFieldWidth.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent e) {
							//initializeDefaultLabel();
						}

						@Override
						public void keyPressed(KeyEvent e) {}
					});

					textFieldRows.addMouseListener(textFieldMouseListener);
					textFieldRows.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {			
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) || v == KeyEvent.VK_BACK_SPACE)
							{
								e.consume();
							}
							if(textFieldRows.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent e) {

						}

						@Override
						public void keyPressed(KeyEvent e) {}
					});

					textFieldHeight.addMouseListener(textFieldMouseListener);
					textFieldHeight.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) ||
									v == KeyEvent.VK_BACK_SPACE)
							{
								e.consume();
							}
							if(textFieldHeight.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent arg0) {
							//initializeDefaultLabel();
						}

						@Override
						public void keyPressed(KeyEvent arg0) {}
					});

					textFieldSteps.addMouseListener(textFieldMouseListener);
					textFieldSteps.addKeyListener(new KeyListener() {

						@Override
						public void keyTyped(KeyEvent e) {
							char v = e.getKeyChar();
							if(!(Character.isDigit(v)) ||
									v == KeyEvent.VK_BACK_SPACE)
							{
								e.consume();
							}
							if(textFieldSteps.getText().length()>0)
								initializeDefaultLabel();
						}

						@Override
						public void keyReleased(KeyEvent e) {
							// TODO Auto-generated method stub

						}

						@Override
						public void keyPressed(KeyEvent e) {
							// TODO Auto-generated method stub

						}
					});

					//---- jLabelChooseSimulation ----
					jLabelChooseSimulation.setText("Choose your simulation:");

					//---- jComboBoxChooseSimulation ----
					jComboBoxChooseSimulation.setMaximumRowCount(10);

					JLabel lblRows = new JLabel("ROWS :");

					JLabel lblColumns = new JLabel("COLUMNS :");

					textFieldRows.setText("1");
					textFieldRows.setEnabled(false);
					textFieldRows.setColumns(10);

					rows= Integer.parseInt(textFieldRows.getText());


					textFieldColumns.setText("2");
					textFieldColumns.setEnabled(false);
					textFieldColumns.setColumns(10);
					columns= Integer.parseInt(textFieldColumns.getText());

					textFieldSteps.setText("100");
					textFieldSteps.setEnabled(false);
					textFieldSteps.setColumns(10);
					steps = 100;

					GroupLayout jPanelSettingsLayout = new GroupLayout(jPanelSettings);
					jPanelSettingsLayout.setHorizontalGroup(
							jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(jPanelSettingsLayout.createSequentialGroup()
									.addGap(17)
									.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
											.addComponent(jLabelChooseSimulation, GroupLayout.PREFERRED_SIZE, 245, GroupLayout.PREFERRED_SIZE)
											.addGroup(jPanelSettingsLayout.createSequentialGroup()
													.addGap(119)
													.addComponent(jLabelHorizontal))
													.addComponent(jComboBoxChooseSimulation, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
													.addGroup(jPanelSettingsLayout.createSequentialGroup()
															.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.TRAILING)
																	.addGroup(Alignment.LEADING, jPanelSettingsLayout.createSequentialGroup()
																			.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
																					.addComponent(lblRows, GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE)
																					.addComponent(lblColumns))
																					.addGap(60)
																					.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
																							.addComponent(jLabelSquare)
																							.addComponent(textFieldColumns, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
																							.addComponent(textFieldRows, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)))
																							.addGroup(Alignment.LEADING, jPanelSettingsLayout.createSequentialGroup()
																									.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
																											.addComponent(jLabelMaxDistance)
																											.addComponent(jLabelWidth)
																											.addComponent(jLabelHeight)
																											.addComponent(jLabelAgents)
																											.addComponent(jLabelInsertSteps)
																											.addGap(132)
																											)
																											.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
																													.addComponent(textFieldAgents, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
																													.addComponent(textFieldHeight, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
																													.addComponent(textFieldWidth, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
																													.addComponent(textFieldMaxDistance, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
																													.addComponent(textFieldSteps, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
																													.addGap(20)
																													)
																									)
																	)

																	.addGap(20))))
							);
					jPanelSettingsLayout.setVerticalGroup(
							jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(jPanelSettingsLayout.createSequentialGroup()
									.addContainerGap()
									.addComponent(jLabelHorizontal)
									.addGap(41)
									.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.LEADING)
											.addComponent(jLabelSquare)
											.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
													.addComponent(lblRows, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
													.addComponent(textFieldRows, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
													.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
													.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
															.addComponent(textFieldColumns, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
															.addComponent(lblColumns))
															.addGap(18)
															.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
																	.addComponent(jLabelMaxDistance)
																	.addComponent(textFieldMaxDistance, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
																			.addComponent(jLabelWidth, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
																			.addComponent(textFieldWidth, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
																			.addGap(10)
																			.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
																					.addComponent(jLabelHeight, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
																					.addComponent(textFieldHeight, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
																					.addPreferredGap(ComponentPlacement.RELATED)
																					.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
																							.addComponent(jLabelAgents)
																							.addComponent(textFieldAgents, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
																							.addPreferredGap(ComponentPlacement.RELATED)
																							//.addGap(10)
																							.addGroup(jPanelSettingsLayout.createParallelGroup(Alignment.BASELINE)
																									.addComponent(jLabelInsertSteps, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
																									.addComponent(textFieldSteps, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
																									)
																									.addGap(35)
																									.addComponent(jLabelChooseSimulation)
																									.addPreferredGap(ComponentPlacement.UNRELATED)
																									.addComponent(jComboBoxChooseSimulation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																									.addGap(52))
							);
					jPanelSettings.setLayout(jPanelSettingsLayout);
				}

				//======== jPanelContainerTabbedPane ========
				{

					//======== tabbedPane2 ========
					{

						//======== jPanelDefault ========
						{
							jPanelDefault.setBorder(new EtchedBorder());
							jPanelDefault.setPreferredSize(new Dimension(350, 331));

							//---- labelSimulationConfigSet ----
							labelSimulationConfigSet.setText("Simulation Configuration Settings");
							labelSimulationConfigSet.setFont(labelSimulationConfigSet.getFont().deriveFont(labelSimulationConfigSet.getFont().getStyle() | Font.BOLD, labelSimulationConfigSet.getFont().getSize() + 8f));

							//---- labelRegionsResume ----
							labelRegionsResume.setText("REGIONS :");

							//---- labelNumOfPeerResume ----
							labelNumOfPeerResume.setText("NUMBER OF PEERS :");

							//---- labelRegForPeerResume ----
							labelRegForPeerResume.setText("REGIONS FOR PEER :");

							//---- labelWriteReg ----
							labelWriteReg.setText("text");

							//---- labelWriteNumOfPeer ----
							labelWriteNumOfPeer.setText("text");

							//---- labelWriteRegForPeer ----
							labelWriteRegForPeer.setText("text");

							//---- labelWidthRegion ----
							labelWidthRegion.setText("REGION WIDTH :");

							//---- labelheightRegion ----
							labelheightRegion.setText("REGION HEIGHT :");

							//---- labelDistrMode ----
							labelDistrMode.setText("DISTRIBUTION MODE :");

							//---- labelWriteRegWidth ----
							labelWriteRegWidth.setText("text");

							//---- labelWriteRegHeight ----
							labelWriteRegHeight.setText("text");

							//---- labelWriteDistrMode ----
							labelWriteDistrMode.setText("text");

							//---- graphicONcheckBox2 ----
							graphicONcheckBox2.setText("Graphic ON");

							//======== jPanelSetButton ========
							{

								//---- buttonSetConfigDefault ----
								buttonSetConfigDefault.setText("Set");
								{
									jCheckBoxLoadBalancing.setText("Load Balancing");
								}

								GroupLayout jPanelSetButtonLayout = new GroupLayout(jPanelSetButton);
								jPanelSetButton.setLayout(jPanelSetButtonLayout);
								jPanelSetButtonLayout.setVerticalGroup(jPanelSetButtonLayout.createSequentialGroup()

										.addGroup(jPanelSetButtonLayout.createParallelGroup()
												.addGroup(GroupLayout.Alignment.TRAILING, jPanelSetButtonLayout.createSequentialGroup()
														.addComponent(jCheckBoxMPI, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
														)
												) 
												.addGroup(jPanelSetButtonLayout.createParallelGroup()
														.addGroup(GroupLayout.Alignment.LEADING, jPanelSetButtonLayout.createSequentialGroup()
																.addComponent(jCheckBoxLoadBalancing, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
																)
																.addGroup(GroupLayout.Alignment.LEADING, jPanelSetButtonLayout.createSequentialGroup()
																		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
																		.addComponent(buttonSetConfigDefault, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))

																		.addContainerGap());


								jPanelSetButtonLayout.setHorizontalGroup(jPanelSetButtonLayout.createSequentialGroup()
										.addComponent(jCheckBoxMPI, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(0, 0)
										.addComponent(jCheckBoxLoadBalancing, GroupLayout.PREFERRED_SIZE, 114, GroupLayout.PREFERRED_SIZE)
										.addGap(0, 150, Short.MAX_VALUE)
										.addComponent(buttonSetConfigDefault, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(0, 0));

								FlowLayout lsetbutt=new FlowLayout();
								lsetbutt.addLayoutComponent("", jCheckBoxMPI);
								lsetbutt.addLayoutComponent("", jCheckBoxLoadBalancing);
								lsetbutt.addLayoutComponent("", buttonSetConfigDefault);
								jPanelSetButton.setLayout(lsetbutt);

							}

							GroupLayout jPanelDefaultLayout = new GroupLayout(jPanelDefault);
							jPanelDefaultLayout.setHorizontalGroup(
									jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(jPanelDefaultLayout.createSequentialGroup()
											.addContainerGap()
											.addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(labelSimulationConfigSet, GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
													.addGroup(jPanelDefaultLayout.createSequentialGroup()
															.addGap(6)
															.addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
																	.addGroup(jPanelDefaultLayout.createSequentialGroup()
																			.addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
																					.addComponent(labelNumOfPeerResume)
																					.addComponent(labelRegForPeerResume)
																					.addComponent(labelWidthRegion)
																					.addComponent(labelheightRegion)
																					.addComponent(labelDistrMode)
																					.addComponent(labelRegionsResume))
																					.addPreferredGap(ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
																					.addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
																							.addComponent(labelWriteNumOfPeer, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
																							.addComponent(labelWriteReg)
																							.addComponent(labelWriteRegForPeer)
																							.addComponent(labelWriteRegWidth)
																							.addComponent(labelWriteRegHeight)
																							.addComponent(labelWriteDistrMode))
																							.addGap(118))
																							.addComponent(graphicONcheckBox2))))
																							.addGap(211))
																							.addGroup(jPanelDefaultLayout.createSequentialGroup()
																									.addComponent(jPanelSetButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																									.addContainerGap(23, Short.MAX_VALUE))
									);
							jPanelDefaultLayout.setVerticalGroup(
									jPanelDefaultLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(jPanelDefaultLayout.createSequentialGroup()
											.addContainerGap()
											.addComponent(labelSimulationConfigSet, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
											.addGap(28)
											.addGroup(jPanelDefaultLayout.createParallelGroup(Alignment.TRAILING)
													.addGroup(jPanelDefaultLayout.createSequentialGroup()
															.addComponent(labelRegionsResume)
															.addPreferredGap(ComponentPlacement.RELATED)
															.addComponent(labelNumOfPeerResume)
															.addPreferredGap(ComponentPlacement.RELATED)
															.addComponent(labelRegForPeerResume)
															.addGap(6)
															.addComponent(labelWidthRegion)
															.addGap(6)
															.addComponent(labelheightRegion)
															.addPreferredGap(ComponentPlacement.RELATED)
															.addComponent(labelDistrMode))
															.addGroup(jPanelDefaultLayout.createSequentialGroup()
																	.addComponent(labelWriteReg)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(labelWriteNumOfPeer)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(labelWriteRegForPeer)
																	.addGap(6)
																	.addComponent(labelWriteRegWidth)
																	.addGap(6)
																	.addComponent(labelWriteRegHeight)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(labelWriteDistrMode, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
																	.addGap(8)))
																	.addPreferredGap(ComponentPlacement.UNRELATED)
																	.addComponent(graphicONcheckBox2)
																	.addPreferredGap(ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
																	.addComponent(jPanelSetButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									);
							jPanelDefault.setLayout(jPanelDefaultLayout);
						}
						tabbedPane2.addTab("Default", jPanelDefault);


						//======== jPanelSimulation ========


						tabbedPane2.addTab("Simulation", jPanelSimulation);
						//======== End jPanelSimulation ========
						//======== jPanelAdvanced ========
						{
							jPanelAdvanced.setBorder(new EtchedBorder());

							//======== jPanelAdvancedMain ========
							{

								//======== scrollPaneTree ========
								{

									//---- tree1 ----
									tree1.setModel(new DefaultTreeModel(root));
									DefaultTreeCellRenderer render = new DefaultTreeCellRenderer();

									render.setOpenIcon(new ImageIcon("resources/image/network.png"));

									render.setLeafIcon(new ImageIcon("esource/image/computer.gif"));

									render.setClosedIcon(new ImageIcon("resources/image/network.png"));
									tree1.setCellRenderer(render);
									tree1.setRowHeight(25);
									scrollPaneTree.setViewportView(tree1);
									tree1.addTreeSelectionListener(new TreeSelectionListener() {

										@Override
										public void valueChanged(TreeSelectionEvent arg0) {
											if(arg0.getPath().getLastPathComponent().equals(root)){
												jComboBoxNumRegionXPeer.setEnabled(true);
												advancedConfirmBut.setEnabled(true);
												graphicONcheckBox.setEnabled(true);
												total = Integer.parseInt(textFieldRows.getText()) * Integer.parseInt(textFieldColumns.getText()); //(Integer)jComboRegions.getSelectedItem();
												res = total;
												jComboBoxNumRegionXPeer.removeAllItems();
												for(int i=1;i<res;i++)
													jComboBoxNumRegionXPeer.addItem(i);
											}
											else
												clickTreeListener();
										}
									});

								}

								//======== peerInfoStatus ========
								{
									peerInfoStatus.setBorder(new TitledBorder("Settings"));
									peerInfoStatus.setBackground(Color.lightGray);

									//======== peerInfoStatus1 ========
									{
										peerInfoStatus1.setBackground(Color.lightGray);
										peerInfoStatus1.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
										peerInfoStatus1.setBorder(null);

										//======== internalFrame1 ========
										{
											internalFrame1.setVisible(true);
											Container internalFrame1ContentPane = internalFrame1.getContentPane();

											//======== scrollPane1 ========
											{

												//---- label8 ----
												architectureLabel.setText("Architecture Information");
												scrollPane1.setViewportView(architectureLabel);
											}

											GroupLayout internalFrame1ContentPaneLayout = new GroupLayout(internalFrame1ContentPane);
											internalFrame1ContentPane.setLayout(internalFrame1ContentPaneLayout);
											internalFrame1ContentPaneLayout.setHorizontalGroup(
													internalFrame1ContentPaneLayout.createParallelGroup()
													.addGroup(internalFrame1ContentPaneLayout.createSequentialGroup()
															.addContainerGap()
															.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
															.addContainerGap())
													);
											internalFrame1ContentPaneLayout.setVerticalGroup(
													internalFrame1ContentPaneLayout.createParallelGroup()
													.addGroup(internalFrame1ContentPaneLayout.createSequentialGroup()
															.addContainerGap()
															.addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE)
															.addContainerGap())
													);
										}
										peerInfoStatus1.add(internalFrame1, JLayeredPane.DEFAULT_LAYER);
										internalFrame1.setBounds(15, 0, 365, 160);
									}

									//---- graphicONcheckBox ----
									graphicONcheckBox.setText("Graphic ON");

									//---- advancedConfirmBut ----
									advancedConfirmBut.setIcon(new ImageIcon("resources/image/ok.png"));

									GroupLayout peerInfoStatusLayout = new GroupLayout(peerInfoStatus);
									peerInfoStatus.setLayout(peerInfoStatusLayout);
									peerInfoStatusLayout.setHorizontalGroup(
											peerInfoStatusLayout.createParallelGroup()
											.addGroup(peerInfoStatusLayout.createSequentialGroup()
													.addGroup(peerInfoStatusLayout.createParallelGroup()
															.addGroup(peerInfoStatusLayout.createSequentialGroup()
																	.addGap(26, 26, 26)
																	.addComponent(graphicONcheckBox)
																	.addGap(73, 73, 73)
																	.addComponent(jComboBoxNumRegionXPeer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addGap(80, 80, 80)
																	.addComponent(advancedConfirmBut, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
																	.addGroup(peerInfoStatusLayout.createSequentialGroup()
																			.addContainerGap()
																			.addComponent(peerInfoStatus1, GroupLayout.DEFAULT_SIZE, 387, Short.MAX_VALUE)))
																			.addContainerGap())
											);
									peerInfoStatusLayout.setVerticalGroup(
											peerInfoStatusLayout.createParallelGroup()
											.addGroup(peerInfoStatusLayout.createSequentialGroup()
													.addComponent(peerInfoStatus1, GroupLayout.PREFERRED_SIZE, 175, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
													.addGroup(peerInfoStatusLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
															.addGroup(GroupLayout.Alignment.LEADING, peerInfoStatusLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																	.addComponent(graphicONcheckBox)
																	.addComponent(jComboBoxNumRegionXPeer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
																	.addComponent(advancedConfirmBut, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE))
																	.addContainerGap(16, Short.MAX_VALUE))
											);
								}

								GroupLayout jPanelAdvancedMainLayout = new GroupLayout(jPanelAdvancedMain);
								jPanelAdvancedMain.setLayout(jPanelAdvancedMainLayout);
								jPanelAdvancedMainLayout.setHorizontalGroup(
										jPanelAdvancedMainLayout.createParallelGroup()
										.addGroup(jPanelAdvancedMainLayout.createSequentialGroup()
												.addContainerGap()
												.addComponent(scrollPaneTree, GroupLayout.PREFERRED_SIZE, 207, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(peerInfoStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addContainerGap())
										);
								jPanelAdvancedMainLayout.setVerticalGroup(
										jPanelAdvancedMainLayout.createParallelGroup()
										.addGroup(GroupLayout.Alignment.TRAILING, jPanelAdvancedMainLayout.createSequentialGroup()
												.addContainerGap()
												.addGroup(jPanelAdvancedMainLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
														.addComponent(peerInfoStatus, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(scrollPaneTree, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
														.addContainerGap())
										);
							}

							//======== jPanelSetButton2 ========
							{

								//---- buttonSetConfigDefault2 ----
								buttonSetConfigDefault2.setText("Set");

								GroupLayout jPanelSetButton2Layout = new GroupLayout(jPanelSetButton2);
								jPanelSetButton2.setLayout(jPanelSetButton2Layout);
								jPanelSetButton2Layout.setHorizontalGroup(
										jPanelSetButton2Layout.createParallelGroup()
										.addGroup(GroupLayout.Alignment.TRAILING, jPanelSetButton2Layout.createSequentialGroup()
												.addContainerGap(522, Short.MAX_VALUE)
												.addComponent(buttonSetConfigDefault2, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
												.addGap(72, 72, 72))
										);
								jPanelSetButton2Layout.setVerticalGroup(
										jPanelSetButton2Layout.createParallelGroup()
										.addGroup(GroupLayout.Alignment.TRAILING, jPanelSetButton2Layout.createSequentialGroup()
												.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(buttonSetConfigDefault2)
												.addContainerGap())
										);
							}

							GroupLayout jPanelAdvancedLayout = new GroupLayout(jPanelAdvanced);
							jPanelAdvancedLayout.setHorizontalGroup(
									jPanelAdvancedLayout.createParallelGroup(Alignment.LEADING)
									.addGroup(jPanelAdvancedLayout.createSequentialGroup()
											.addGroup(jPanelAdvancedLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(jPanelAdvancedMain, GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
													.addComponent(jPanelSetButton2, GroupLayout.PREFERRED_SIZE, 681, GroupLayout.PREFERRED_SIZE))
													.addContainerGap())
									);
							jPanelAdvancedLayout.setVerticalGroup(
									jPanelAdvancedLayout.createParallelGroup(Alignment.TRAILING)
									.addGroup(jPanelAdvancedLayout.createSequentialGroup()
											.addContainerGap()
											.addComponent(jPanelAdvancedMain, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(18)
											.addComponent(jPanelSetButton2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									);
							jPanelAdvanced.setLayout(jPanelAdvancedLayout);
						}
						tabbedPane2.addTab("Advanced", jPanelAdvanced);

					}

					//======== panelConsole ========
					{

						//======== scrollPane3 ========
						{

							//---- textField1 ----
							notifyArea.setEditable(false);
							DefaultCaret caret = (DefaultCaret)notifyArea.getCaret();
							caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
							scrollPane3.setViewportView(notifyArea);
						}

						GroupLayout panelConsoleLayout = new GroupLayout(panelConsole);
						panelConsole.setLayout(panelConsoleLayout);
						panelConsoleLayout.setHorizontalGroup(
								panelConsoleLayout.createParallelGroup()
								.addGroup(panelConsoleLayout.createParallelGroup()
										.addGroup(panelConsoleLayout.createSequentialGroup()
												.addGap(0, 0, Short.MAX_VALUE)
												.addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 679, GroupLayout.PREFERRED_SIZE)
												.addGap(0, 0, Short.MAX_VALUE)))
												.addGap(0, 679, Short.MAX_VALUE)
								);
						panelConsoleLayout.setVerticalGroup(
								panelConsoleLayout.createParallelGroup()
								.addGroup(panelConsoleLayout.createParallelGroup()
										.addGroup(panelConsoleLayout.createSequentialGroup()
												.addGap(0, 0, Short.MAX_VALUE)
												.addComponent(scrollPane3, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
												.addGap(0, 0, Short.MAX_VALUE)))
												.addGap(0, 76, Short.MAX_VALUE)
								);
					}

					GroupLayout jPanelContainerTabbedPaneLayout = new GroupLayout(jPanelContainerTabbedPane);
					jPanelContainerTabbedPaneLayout.setHorizontalGroup(
							jPanelContainerTabbedPaneLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(jPanelContainerTabbedPaneLayout.createSequentialGroup()
									.addContainerGap()
									.addGroup(jPanelContainerTabbedPaneLayout.createParallelGroup(Alignment.LEADING)
											.addComponent(tabbedPane2, GroupLayout.PREFERRED_SIZE, 686, GroupLayout.PREFERRED_SIZE)
											.addComponent(panelConsole, GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE))
											.addContainerGap())
							);
					jPanelContainerTabbedPaneLayout.setVerticalGroup(
							jPanelContainerTabbedPaneLayout.createParallelGroup(Alignment.LEADING)
							.addGroup(jPanelContainerTabbedPaneLayout.createSequentialGroup()
									.addGap(3)
									.addComponent(tabbedPane2, GroupLayout.PREFERRED_SIZE, 384, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panelConsole, GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
							);
					jPanelContainerTabbedPane.setLayout(jPanelContainerTabbedPaneLayout);
				}
				{
					jPanelDeploying = new JPanel();
					tabbedPane2.addTab("Simulation Jar", null, jPanelDeploying, null);
					GroupLayout jPanelDeployingLayout = new GroupLayout(jPanelDeploying);
					jPanelDeploying.setLayout(jPanelDeployingLayout);
					{
						jButtonLoadJar = new JButton();
						jButtonLoadJar.setText("Load Jar");
						jButtonLoadJar.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent evt)
							{

								if( simulationFile != null)
								{

									File dest = new File(FTP_HOME+dirSeparator+SIMULATION_DIR+dirSeparator+simulationFile.getName());
									try {
										FileUtils.copyFile(simulationFile, dest);

										Digester dg = new Digester(DigestAlgorithm.MD5);

										InputStream in = new FileInputStream(dest);

										Properties prop = new Properties();

										try {

											prop.setProperty("MD5", dg.getDigest(in));

											String fileName = FilenameUtils.removeExtension(simulationFile.getName());
											//save properties to project root folder
											prop.store(new FileOutputStream(FTP_HOME+dirSeparator+SIMULATION_DIR+dirSeparator+fileName+".hash"), null);

										} catch (IOException ex) {
											ex.printStackTrace();
										}

										System.out.println("MD5: "+dg.getDigest(in));

										loadSimulation();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						});


					}

					jPanelDeployingLayout.setVerticalGroup(jPanelDeployingLayout.createSequentialGroup()
							.addGap(22, 22, 22)
							.addGroup(jPanelDeployingLayout.createParallelGroup()
									.addGroup(GroupLayout.Alignment.LEADING, jPanelDeployingLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
											.addComponent(jButtonLoadJar, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(getJTextFieldPathSimJar(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
											.addComponent(getJButtonChoseSimJar(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 286, GroupLayout.PREFERRED_SIZE));
					jPanelDeployingLayout.setHorizontalGroup(jPanelDeployingLayout.createSequentialGroup()
							.addGap(22, 22, 22)
							.addComponent(getJTextFieldPathSimJar(), GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(getJButtonChoseSimJar(), GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
							.addGap(24)
							.addComponent(jButtonLoadJar, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE));
				}

				jPanelRunBatchTests = new JPanel();
				tabbedPane2.addTab("Run batch tests", null, jPanelRunBatchTests, null);

				JLabel lblSelectConfigurationFile = new JLabel("Select configuration file:");

				textFieldConfigFilePath = new JTextField();
				textFieldConfigFilePath.setColumns(10);

				JButton buttonChooseConfigFile = new JButton();
				buttonChooseConfigFile.addActionListener(new ActionListener() {


					@Override
					public void actionPerformed(ActionEvent arg0) {

						configFile = showFileChooser();
						if(configFile != null)
							textFieldConfigFilePath.setText(configFile.getAbsolutePath());
					}
				});
				buttonChooseConfigFile.setIcon(new ImageIcon("it/isislab/dmason/resources/image/openFolder.png"));

				JButton buttonLoadConfig = new JButton("Start");
				buttonLoadConfig.addActionListener(new ActionListener() {





					@Override
					public void actionPerformed(ActionEvent e) {

						try {
							if(configFile.getName().contains(".xml"))
							{
								ClassLoader.getSystemClassLoader();
								//File xsd = new File(xsdFilename);
								InputStream xsd = new FileInputStream("resources/batch/batchSchema.xsd");

								if(xsd != null)
								{
									if(validateXML(configFile, xsd))
									{

										Batch batch = loadConfigFromXML(configFile);

										if(batch != null)
										{
											textAreaBatchInfo.append(configFile.getName()+" loaded.\n");
											if(batch.getNeededWorkers() > peers.size() || batch.getNeededWorkers() == 0)
												JOptionPane.showMessageDialog(DMasonMaster.this, "There are not enough workers to start the simulation");
											else
											{
												textAreaBatchInfo.append("Simulation: "+batch.getSimulationName()+"\n");
												textAreaBatchInfo.append("Needed Worker: "+batch.getNeededWorkers()+"\n");
												textAreaBatchInfo.append("Balance: "+batch.isBalanced()+"\n");

												Set<List<EntryParam<String, Object>>> testList = generateTestsFrom(batch);

												textAreaBatchInfo.append("--------------------------------------\n");
												textAreaBatchInfo.append("Number of experiments: "+testList.size()+"\n");
												ConcurrentLinkedQueue<List<EntryParam<String, Object>>> testQueue = new ConcurrentLinkedQueue<List<EntryParam<String,Object>>>();

												for (List<EntryParam<String, Object>> test : testList) 
													testQueue.offer(test);

												//System.out.println("Test queue: "+testQueue.size());

												try {

													List<List<EntryWorkerScore<Integer, String>>> workersPartition = Util.chopped(scoreList, batch.getNeededWorkers());


													//System.out.println(workersPartition.toString());

													testCount.set(0);
													totalTests =  testList.size();

													progressBarBatchTest.setValue(0);
													progressBarBatchTest.setString("0 %");
													progressBarBatchTest.setStringPainted(true);

													batchLogger = Logger.getLogger(BatchExecutor.class.getCanonicalName());
													batchStartedTime = System.currentTimeMillis();

													textAreaBatchInfo.append("Batch started at: "+Util.getCurrentDateTime(batchStartedTime)+"\n");
													textAreaBatchInfo.append("--------------------------------------\n");
													batchLogger.debug("Started at: "+batchStartedTime);
													int i = 1;
													BatchExecutor batchExec;
													for (List<EntryWorkerScore<Integer, String>> workers : workersPartition)
													{
														try {

															batchExec = new BatchExecutor(batch.getSimulationName(),batch.isBalanced(),testQueue,master,connection,root.getChildCount(),getFPTAddress(),workers,"Batch"+i,1,textAreaBatchInfo);

															batchExec.getObservable().addObserver(DMasonMaster.this);
															batchExec.start();

															//Thread.sleep(2000);
															//textAreaBatchInfo.append("Batch Executor "+i+" started\n");

															i++;

															//not paraller simulation, I use only one batch executor
															if(!chckbxParallelBatch.isSelected())
																break;
														} catch (Exception e1) {
															// TODO Auto-generated catch block
															e1.printStackTrace();
														}

													}

												} catch (Exception e3) {
													// TODO Auto-generated catch block
													e3.printStackTrace();
												}

											}

										}
										else
											JOptionPane.showMessageDialog(DMasonMaster.this, "Error when loading config file");
									}
									else
										JOptionPane.showMessageDialog(DMasonMaster.this, "The configuration file is not a valid file");
								}
								else
									JOptionPane.showMessageDialog(DMasonMaster.this, xsdFilename +" not exists, can't validate configuration file.");
							}
							else
								JOptionPane.showMessageDialog(DMasonMaster.this, "The file "+configFile.getName()+ "is not a configuration file.");
						} catch (HeadlessException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}


				});

				JPanel panel = new JPanel();
				panel.setBorder(new TitledBorder(null, "Batch Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));

				chckbxParallelBatch = new JCheckBox("Parallel Batch");


				GroupLayout gl_jPanelRunBatchTests = new GroupLayout(jPanelRunBatchTests);
				gl_jPanelRunBatchTests.setHorizontalGroup(
						gl_jPanelRunBatchTests.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_jPanelRunBatchTests.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_jPanelRunBatchTests.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_jPanelRunBatchTests.createSequentialGroup()
												.addComponent(lblSelectConfigurationFile, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(textFieldConfigFilePath, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
												.addGap(10)
												.addComponent(buttonChooseConfigFile, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
												.addGroup(gl_jPanelRunBatchTests.createSequentialGroup()
														.addComponent(chckbxParallelBatch)
														.addGap(18)
														.addComponent(buttonLoadConfig))
														.addComponent(panel, GroupLayout.PREFERRED_SIZE, 666, GroupLayout.PREFERRED_SIZE))
														.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						);
				gl_jPanelRunBatchTests.setVerticalGroup(
						gl_jPanelRunBatchTests.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_jPanelRunBatchTests.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_jPanelRunBatchTests.createParallelGroup(Alignment.TRAILING)
										.addComponent(buttonChooseConfigFile, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
										.addComponent(textFieldConfigFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(lblSelectConfigurationFile))
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addGroup(gl_jPanelRunBatchTests.createParallelGroup(Alignment.BASELINE)
												.addComponent(chckbxParallelBatch)
												.addComponent(buttonLoadConfig))
												.addPreferredGap(ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
												.addComponent(panel, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
												.addContainerGap())
						);

				progressBarBatchTest = new JProgressBar();

				JLabel lblProgress = new JLabel("Progress:");
				GroupLayout gl_panel = new GroupLayout(panel);
				gl_panel.setHorizontalGroup(
						gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(scrollPane4, GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
										.addGroup(gl_panel.createSequentialGroup()
												.addComponent(lblProgress)
												.addGap(18)
												.addComponent(progressBarBatchTest, GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE)))
												.addContainerGap())
						);
				gl_panel.setVerticalGroup(
						gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
										.addComponent(lblProgress)
										.addComponent(progressBarBatchTest, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(scrollPane4, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE)
										.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						);
				panel.setLayout(gl_panel);
				jPanelRunBatchTests.setLayout(gl_jPanelRunBatchTests);
				GroupLayout jPanelSetDistributionLayout = new GroupLayout(jPanelSetDistribution);
				jPanelSetDistributionLayout.setHorizontalGroup(
						jPanelSetDistributionLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanelSetDistributionLayout.createSequentialGroup()
								.addComponent(jPanelSettings, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(jPanelContainerTabbedPane, GroupLayout.PREFERRED_SIZE, 707, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(21, Short.MAX_VALUE))
						);
				jPanelSetDistributionLayout.setVerticalGroup(
						jPanelSetDistributionLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanelSetDistributionLayout.createSequentialGroup()
								.addGroup(jPanelSetDistributionLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(jPanelSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jPanelContainerTabbedPane, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addContainerGap())
						);
				jPanelSetDistribution.setLayout(jPanelSetDistributionLayout);
			}

			textAreaBatchInfo = new JTextArea();
			textAreaBatchInfo.setEditable(false);
			DefaultCaret caret = (DefaultCaret)textAreaBatchInfo.getCaret();
			caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
			scrollPane4.setViewportView(textAreaBatchInfo);

			//---- jLabelPlayButton ----
			jLabelPlayButton.setIcon(new ImageIcon("resources/image/NotStopped.png"));

			//---- jLabelPauseButton ----
			jLabelPauseButton.setIcon(new ImageIcon("resources/image/PauseOff.png"));


			//---- labelStopButton ----
			jLabelPlayButton.setIcon(new ImageIcon("image/NotPlaying.png"));

			jLabelPlayButton.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent arg0) {}

				@Override
				public void mousePressed(MouseEvent arg0) {}

				@Override
				public void mouseExited(MouseEvent arg0) {}

				@Override
				public void mouseEntered(MouseEvent arg0) {}

				@Override
				public void mouseClicked(MouseEvent arg0) {

					jLabelPlayButton.setIcon(new ImageIcon("resources/image/Playing.png"));
					jLabelPauseButton.setIcon(new ImageIcon("resources/image/PauseOff.png"));
					jLabelStopButton.setIcon(new ImageIcon("resources/image/NotStopped.png"));
					jLabelResetButton.setIcon(new ImageIcon("resources/image/NotReload.png"));
					try {

						master.play();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			//---- labelStopButton2 ----	
			jLabelStopButton.setIcon(new ImageIcon("resources/image/NotStopped.png"));
			jLabelStopButton.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseClicked(MouseEvent e) {
					jLabelStopButton.setIcon(new ImageIcon("resources/image/Stopped.png"));
					jLabelPlayButton.setIcon(new ImageIcon("resources/image/NotPlaying.png"));
					jLabelPauseButton.setIcon(new ImageIcon("resources/image/PauseOff.png"));

					try {

						Address FTPAddress = getFPTAddress();

						if(FTPAddress != null)
						{
							UpdateData ud = new UpdateData("", FTPAddress);
							master.stop(ud);
						}


					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			//---- labelPauseButton ----
			jLabelPauseButton.setIcon(new ImageIcon("resources/image/PauseOff.png"));
			jLabelPauseButton.addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {}

				@Override
				public void mouseClicked(MouseEvent e) {
					jLabelPauseButton.setIcon(new ImageIcon("resources/image/PauseOn.png"));
					jLabelStopButton.setIcon(new ImageIcon("resources/image/NotStopped.png"));
					jLabelPlayButton.setIcon(new ImageIcon("resources/image/NotPlaying.png"));
					try {
						master.pause();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});

			//======== jPanelNumStep ========
			{

				GroupLayout jPanelNumStepLayout = new GroupLayout(jPanelNumStep);
				jPanelNumStepLayout.setHorizontalGroup(
						jPanelNumStepLayout.createParallelGroup(Alignment.TRAILING)
						.addGap(0, 25, Short.MAX_VALUE)
						);
				jPanelNumStepLayout.setVerticalGroup(
						jPanelNumStepLayout.createParallelGroup(Alignment.LEADING)
						.addGap(0, 28, Short.MAX_VALUE)
						);
				jPanelNumStep.setLayout(jPanelNumStepLayout);
				jPanelNumStep.setPreferredSize(new java.awt.Dimension(89, 23));
			}
			{
				jLabelResetButton = new JLabel();
				jLabelResetButton.setIcon(new ImageIcon("resources/image/NotReload.png"));
				jLabelResetButton.setPreferredSize(new java.awt.Dimension(20, 20));

				jLabelResetButton.setVisible(enableReset);
			}

			// for resetting simulation
			jLabelResetButton.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent arg0) {

					if(connected)
					{
						jLabelStopButton.setIcon(new ImageIcon("resources/image/NotStopped.png"));
						jLabelPlayButton.setIcon(new ImageIcon("resources/image/NotPlaying.png"));
						jLabelPauseButton.setIcon(new ImageIcon("resources/image/PauseOff.png"));
						jLabelResetButton.setIcon(new ImageIcon("resources/image/Reload.png"));

						//send message to workers for resetting simulation
						try {
							master.reset();

						} catch (Exception e1) {
							e1.printStackTrace();
						}


						//clean up topic from AcitveMQ
						connection.resetTopic();

						setSystemSettingsEnabled(true);

						notifyArea.append("Simulation resetted\n");


					}

				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
			writeStepLabel = new JLabel();
			writeStepLabel.setText("0");

			lblTotalSteps = new JLabel("Steps:");

			GroupLayout panelMainLayout = new GroupLayout(panelMain);
			panelMainLayout.setHorizontalGroup(
					panelMainLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(panelMainLayout.createSequentialGroup()
							.addComponent(jPanelContainerSettings, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(panelMainLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(jPanelSetDistribution, 0, 986, Short.MAX_VALUE)
									.addGroup(panelMainLayout.createSequentialGroup()
											.addGap(636)
											.addGroup(panelMainLayout.createParallelGroup(Alignment.TRAILING)
													.addComponent(jLabelStep, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
													.addGroup(panelMainLayout.createSequentialGroup()
															.addComponent(lblTotalSteps)
															.addPreferredGap(ComponentPlacement.UNRELATED)
															.addComponent(writeStepLabel)))
															.addPreferredGap(ComponentPlacement.RELATED)
															.addComponent(jPanelNumStep, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
															.addGroup(panelMainLayout.createParallelGroup(Alignment.LEADING)
																	.addGroup(panelMainLayout.createSequentialGroup()
																			.addGap(24)
																			.addComponent(jLabelPlayButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(ComponentPlacement.UNRELATED)
																			.addComponent(jLabelPauseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(ComponentPlacement.UNRELATED)
																			.addComponent(jLabelStopButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
																			.addGroup(panelMainLayout.createSequentialGroup()
																					.addGap(116)
																					.addComponent(jLabelResetButton, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))))
																					.addGap(12))
																					.addComponent(jPanelContainerConnection, 0, 1004, Short.MAX_VALUE)
					);
			panelMainLayout.setVerticalGroup(
					panelMainLayout.createParallelGroup(Alignment.LEADING)
					.addGroup(panelMainLayout.createSequentialGroup()
							.addComponent(jPanelContainerConnection, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(panelMainLayout.createParallelGroup(Alignment.LEADING)
									.addComponent(jPanelSetDistribution, GroupLayout.PREFERRED_SIZE, 518, GroupLayout.PREFERRED_SIZE)
									.addGroup(panelMainLayout.createSequentialGroup()
											.addGap(29)
											.addComponent(jPanelContainerSettings, GroupLayout.PREFERRED_SIZE, 489, GroupLayout.PREFERRED_SIZE)))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(panelMainLayout.createParallelGroup(Alignment.LEADING)
													.addComponent(jLabelStopButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(jLabelPlayButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(jLabelPauseButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(jLabelResetButton, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
													.addGroup(panelMainLayout.createParallelGroup(Alignment.BASELINE)
															.addComponent(writeStepLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
															.addComponent(lblTotalSteps))
															.addGroup(panelMainLayout.createSequentialGroup()
																	.addComponent(jLabelStep, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
																	.addPreferredGap(ComponentPlacement.RELATED)
																	.addComponent(jPanelNumStep, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)))
																	.addContainerGap(13, Short.MAX_VALUE))
					);
			panelMain.setLayout(panelMainLayout);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(panelMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())
				);
		contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(panelMain, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
		contentPane.setLayout(contentPaneLayout);
		pack();
		setLocationRelativeTo(null);

		refreshServerLabel.setVisible(false);

		jButtonChoseSimJar.addActionListener(new ActionListener() {


			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulationFile = showFileChooser();
				if(simulationFile != null)
					jTextFieldPathSimJar.setText(simulationFile.getAbsolutePath());
			}
		});


	}



	public void setConnectionSettingsEnabled(boolean enabled)
	{
		textFieldAddress.setEnabled(enabled);
		textFieldPort.setEnabled(enabled);
		//buttonRefreshServerLabel.setEnabled(enabled);

	}

	public void setSystemSettingsEnabled(boolean enabled)
	{
		textFieldRows.setEnabled(enabled);
		textFieldColumns.setEnabled(enabled);
		textFieldAgents.setEnabled(enabled);
		textFieldHeight.setEnabled(enabled);
		textFieldWidth.setEnabled(enabled);
		textFieldMaxDistance.setEnabled(enabled);
		textFieldSteps.setEnabled(enabled);
		jComboBoxChooseSimulation.setEnabled(enabled);

		buttonSetConfigDefault.setEnabled(enabled);
		buttonSetConfigDefault2.setEnabled(enabled);
		buttonSetConfigAdvanced.setEnabled(enabled);
	}

	private void connect(){
		try
		{
			if(csManager == null)
			{
				rmiIp = textFieldAddress.getText(); 
				csManager = new ActiveMQManager(rmiIp, 61617); //"172.16.15.127"
				checkCommunicationServerStatus();
				setEnableActiveMQControl(true);
			}

			ip = textFieldAddress.getText();			    
			port = textFieldPort.getText();  
			address = new Address(textFieldAddress.getText(),textFieldPort.getText());
			connection = new ConnectionNFieldsWithActiveMQAPI();
			connection.setupConnection(address);
			master = new MasterDaemonStarter(connection, this);

			if (!master.connectToServer())
			{
				notifyArea.append("Connection refused to " + textFieldAddress.getText()+", please check IP address and port.\n");
			}

			else{

				notifyArea.append("Connection estabilished.\n");
				connected = true;
				checkPeers();
				dont = true;

				textFieldAddress.setEnabled(false);
				textFieldPort.setEnabled(false);
				buttonRefreshServerLabel.setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkPeers() throws Exception {
		peers = master.getTopicList();
		for(String p : peers)
		{
			master.info(p);
			root.add(new DefaultMutableTreeNode(p));
		}
		labelWriteNumOfPeer.setText(""+peers.size());
	}

	/*private ArrayList<String> checkSyntaxForm(int num,int width,int height,int numAgentsForPeer){

		ArrayList<String> errors = new ArrayList<String>();	



		// check total number of regions coincides with total number of assignments
		if((numRegions % root.getChildCount()) != 0 || total != numRegions)
			errors.add("Please check regions number! \n");
		//check field's misures if horizontal mode is selected
		if(radioButtonHorizontal.isSelected() && width%num != 0)
			errors.add("Cannot divide the field in "+num+" regions! \n");
		//check field's misures if square mode is selected
		if(radioButtonSquare.isSelected() && (width % Math.sqrt(num) != 0 || height % Math.sqrt(num) != 0))
			errors.add("Cannot divide field in Sqrt(REGIONS),please check field parameter \n");
		//check id agents number > 0
		if(numAgentsForPeer<=0)
			errors.add("Missing number of agents\n");
		//check field's measures if square mode balanced is selected
		//the width and the height must be equals, and both must be divisible by sqrt(peer)*3
		if(radioButtonSquare.isSelected() && jCheckBoxLoadBalancing.isSelected() && (width % (Math.sqrt(num)*3) != 0 || height % (Math.sqrt(num)*3) != 0))
			errors.add("Cannot divide field in Sqrt(REGIONS)*3 for the Load Balanced Mode,please check field parameter \n");

		return errors;
	}*/

	private void submitCustomizeMode()
	{
		//System.out.println(total +" JmasterUi submitCustomizeMode()");
		ArrayList<String> errors = null;
		WIDTH = Integer.parseInt(textFieldWidth.getText());   
		HEIGHT = Integer.parseInt(textFieldHeight.getText());
		//numRegions = Integer.parseInt(""+jComboRegions.getSelectedItem());
		numAgents = Integer.parseInt(textFieldAgents.getText());
		maxDistance = Integer.parseInt(textFieldMaxDistance.getText());
		rows= Integer.parseInt(textFieldRows.getText());
		columns= Integer.parseInt(textFieldColumns.getText());



		/*if(logger.getLevel()!=Level.OFF){
			file = new FileAppender();
			  file.setName("test_cells_"+numRegions+"_agents_"+numAgents+"_width_"+WIDTH+"_height_"+HEIGHT);
			  file.setFile("test_cells_"+numRegions+"_agents_"+numAgents+"_width_"+WIDTH+"_height_"+HEIGHT+".log");
			  file.setLayout(new SimpleLayout());
			  file.setThreshold(Level.DEBUG);
			  file.activateOptions();	
			logger.addAppender(file);
			}
		 */
		if(rows==0 || columns==0)
			errors.add("Rows or Columns must not be equals to 0");

		if(rows==1 || columns == 1)
			if(!jCheckBoxLoadBalancing.isSelected())
				MODE = DistributedField2D.UNIFORM_PARTITIONING_MODE;
			else
				MODE = DistributedField2D.HORIZONTAL_BALANCED_DISTRIBUTION_MODE;
		else
			if(!jCheckBoxLoadBalancing.isSelected())
				MODE = DistributedField2D.UNIFORM_PARTITIONING_MODE;
			else
				MODE = DistributedField2D.SQUARE_BALANCED_DISTRIBUTION_MODE;

		if(MODE == DistributedField2D.SQUARE_BALANCED_DISTRIBUTION_MODE && rows != columns && (Integer)WIDTH % 3*rows!=0)
			errors.add("Width and height are not divisible by 3 * sqrt(rows*columns) or rows is not equal to columns");


		if(!isSubmitted ) //for next simulations do not have to register again
		{	
			(new Trigger(connection)).asynchronousReceiveToTriggerTopic(new TriggerListener(notifyArea,logger));
			isSubmitted = true;
		}




		// Wrong data inserted
		if(errors.size() > 0){
			String x="ERRORS"+"\n";
			for(String e : errors)
				x=x+e+"\n";
			JOptionPane.showMessageDialog(null,x);
		}
		getSteps();
		//sim = files.getSelectedFile().getName();

		jCheckBoxMPI.setEnabled(false);
		jCheckBoxLoadBalancing.setEnabled(false);
		graphicONcheckBox.setEnabled(false);
		graphicONcheckBox2.setEnabled(false);
		limitStep = steps;

		GeneralParam params = new GeneralParam(
				(Integer)WIDTH, (Integer)HEIGHT, 
				maxDistance, rows, columns, numAgents, MODE,
				jPanelSimulation.getAllParameters(),
				steps,
				jCheckBoxMPI.isSelected()? ConnectionType.hybridActiveMQMPIParallel : ConnectionType.pureActiveMQ
				);
		//master.start(numRegions, (Integer)WIDTH, (Integer)HEIGHT, numAgents,maxDistance,MODE, config,selectedSimulation,this,getFPTAddress());
		master.start(params, config,selectedSimulation,this,getFPTAddress());
		JOptionPane.showMessageDialog(null,"Setting completed !");
	}

	private void submitDefaultMode(){

		ArrayList<String> errors = new ArrayList<String>();
		//checkSyntaxForm(NUM_REGIONS,(Integer)WIDTH,(Integer)HEIGHT,NUM_AGENTS);
		WIDTH = Integer.parseInt(textFieldWidth.getText());
		HEIGHT = Integer.parseInt(textFieldHeight.getText());
		//numRegions = Integer.parseInt(""+jComboRegions.getSelectedItem());
		numAgents = Integer.parseInt(textFieldAgents.getText());
		maxDistance = Integer.parseInt(textFieldMaxDistance.getText());
		withGui = graphicONcheckBox2.isSelected();
		rows= Integer.parseInt(textFieldRows.getText());
		columns= Integer.parseInt(textFieldColumns.getText());

		if(rows==0 || columns==0)
			errors.add("Rows or Columns must not be equals to 0");

		if(rows == 1 && columns == 1)
			errors.add("Both Rows and Columns must not be equals to 1");

		if(rows==1 || columns == 1)
			if(!jCheckBoxLoadBalancing.isSelected())
				MODE = DistributedField2D.UNIFORM_PARTITIONING_MODE;
			else
				MODE = DistributedField2D.HORIZONTAL_BALANCED_DISTRIBUTION_MODE;
		else
			if(!jCheckBoxLoadBalancing.isSelected())
				MODE = DistributedField2D.UNIFORM_PARTITIONING_MODE;
			else
				MODE = DistributedField2D.SQUARE_BALANCED_DISTRIBUTION_MODE;



		if(MODE == DistributedField2D.SQUARE_BALANCED_DISTRIBUTION_MODE && (rows != columns || (Integer)WIDTH % 3*columns!=0 || (Integer)HEIGHT % 3*rows != 0 || maxDistance >= ((Integer)WIDTH/columns) / 3 / 2))
			errors.add("Width and height are not divisible by 3 * sqrt(rows*columns) or rows is not equal to columns");

		if((Math.floor((Integer)WIDTH/columns)<2*maxDistance+1))
			errors.add("MAX_DISTANCE too large for width of regions");
		else if((Math.floor((Integer)HEIGHT/rows)<2*maxDistance+1))
			errors.add("MAX_DISTANCE too large for height of regions");
		/*if(logger.getLevel()!=Level.OFF){
		file = new FileAppender();
		  file.setName("test_cells_"+numRegions+"_agents_"+numAgents+"_width_"+WIDTH+"_height_"+HEIGHT);
		  file.setFile("test_cells_"+numRegions+"_agents_"+numAgents+"_width_"+WIDTH+"_height_"+HEIGHT+".log");
		  file.setLayout(new SimpleLayout());
		  file.setThreshold(Level.DEBUG);
		  file.activateOptions();	
		logger.addAppender(file);
		}*/



		if(!isSubmitted ) //for next simulations do not have to register again
		{	
			(new Trigger(connection)).asynchronousReceiveToTriggerTopic(new TriggerListener(notifyArea,logger));
			isSubmitted = true;
		}



		/*
		if(radioButtonHorizontal.isSelected())
			MODE = DSparseGrid2DFactory.HORIZONTAL_DISTRIBUTION_MODE;
		else if(radioButtonSquare.isSelected() && !jCheckBoxLoadBalancing.isSelected())
			MODE = DSparseGrid2DFactory.SQUARE_DISTRIBUTION_MODE;
		else if(radioButtonSquare.isSelected() && jCheckBoxLoadBalancing.isSelected())
			MODE = DSparseGrid2DFactory.SQUARE_BALANCED_DISTRIBUTION_MODE;

		if(radioButtonSquare.isSelected() && jCheckBoxLoadBalancing.isSelected() && (Integer)WIDTH % 3*Math.sqrt(numRegions)!=0)
			errors.add("Width and height are not divisible by 3 * sqrt(numRegion)");

		//if (numRegions % root.getChildCount() != 0)
		//	errors.add("NUM_REGIONS < > = NUM_PEERS\n,please set Advanced mode!");
		 */

		if(errors.size() == 0)
		{
			numRegions = rows * columns;
			int regionsToPeers = numRegions / root.getChildCount();
			int remainder = numRegions % root.getChildCount();

			EntryVal<Integer, Boolean> value;
			int last=0;
			try{
				for (int i = 0; i < numRegions; i++) {

					if(config.get(master.getTopicList().get(last))==null)
					{
						value = new EntryVal<Integer, Boolean>(1,withGui/*withGui*/);
						config.put(master.getTopicList().get(last), value);

					}else
						config.get(master.getTopicList().get(last)).setNum(config.get(master.getTopicList().get(last)).getNum()+1);

					last=(last+1)%(master.getTopicList().size());

				}
				getSteps();
			}
			catch(Exception e)
			{
				System.err.println("The Workers does not works! :P");
			}

			//			if(remainder == 0) // all the workers will have the same number of regions
			//			{
			//				
			//				EntryVal<Integer, Boolean> value; 
			//				try{
			//					for(String topic : master.getTopicList()){
			//						value = new EntryVal(regionsToPeers, withGui);
			//						//config.put(topic, div);
			//						config.put(topic, value);
			//					}
			//					getSteps();
			//
			//				}catch (Exception e) {
			//					e.printStackTrace();
			//				}
			//			}
			//			else
			//			{
			//				
			//				try { // there will be some workers(remainders) that will have one more regions
			//					int count = 0;
			//					EntryVal<Integer, Boolean> value;
			//					for(String topic : master.getTopicList()) {
			//						if(count < remainder)
			//							value = new EntryVal<Integer, Boolean>(regionsToPeers+1,withGui);
			//						else
			//							value = new EntryVal<Integer, Boolean>(regionsToPeers,withGui);
			//						
			//						config.put(topic, value);
			//						
			//						count++;
			//					}
			//					
			//					getSteps();
			//				} catch (Exception e) {
			//					// TODO Auto-generated catch block
			//					e.printStackTrace();
			//				}
			//			}
			/*int div = numRegions / root.getChildCount();
			EntryVal<Integer, Boolean> value; 
			try{
				for(String topic : master.getTopicList()){
					value = new EntryVal(div, withGui);
					//config.put(topic, div);
					config.put(topic, value);
				}
				getSteps();

			}catch (Exception e) {
				e.printStackTrace();
			}*/

			jCheckBoxMPI.setEnabled(false);
			jCheckBoxLoadBalancing.setEnabled(false);
			graphicONcheckBox.setEnabled(false);
			graphicONcheckBox2.setEnabled(false);
			limitStep = steps;
			//GeneralParam params = new GeneralParam((Integer)WIDTH, (Integer)HEIGHT, maxDistance, rows, columns, numAgents, MODE);
			GeneralParam params = new GeneralParam(
					(Integer)WIDTH, 
					(Integer)HEIGHT, 
					maxDistance, 
					rows, 
					columns, 
					numAgents, 
					MODE, 
					jPanelSimulation.getAllParameters(),
					steps,
					jCheckBoxMPI.isSelected()? ConnectionType.hybridActiveMQMPIParallel : ConnectionType.pureActiveMQ);

			//master.start(numRegions, (Integer)WIDTH, (Integer)HEIGHT, numAgents,maxDistance,MODE, config,selectedSimulation,this,getFPTAddress());
			master.start(params, config,selectedSimulation,this,getFPTAddress());
			JOptionPane.showMessageDialog(null,"Setting completed !");
		}
		else{
			String x="";
			for(String s : errors)
				x=x+s;
			JOptionPane.showMessageDialog(null,x);
		}
	}

	private void clickTreeListener(){
		String key ="";
		String[] address;
		PeerStatusInfo info=null;
		if(connected && dont){
			key = tree1.getLastSelectedPathComponent().toString();
			if(key.startsWith("SERVICE")){

				info = master.getLatestUpdate(key);
				if(info!=null)
					architectureLabel.setText("IP : "+info.getAddress()+"\n"+"OS : "+info.getOS()+"\n"+"Architecture : "+info.getArchitecture()+"\n"+"Number of Core : "+info.getNumCores()+"\n"+"Worker Version : "+info.getVersion());
			}
		}
	}

	private void getSteps(){
		connectionForSteps = new ConnectionNFieldsWithActiveMQAPI(new MyMessageListener() {
			long initial_time=-1;
			Long step;
			@Override
			public void onMessage(Message arg0) {
				Object o=null; 
				try {
					o = parseMessage(arg0);
				} catch (JMSException e) {
					e.printStackTrace();
				}
				MyHashMap mh = (MyHashMap)o;
				if(mh.get("step")!=null){
					step=(Long)mh.get("step");

					if(step==100)
					{
						initial_time=System.currentTimeMillis();
						logger.debug("Number regions:"+numRegions+" Number agents:"+numAgents+" Width:"+WIDTH+" Height:"+HEIGHT);
						logger.debug("Step :0 Time:"+initial_time);
					}
					else if(step == limitStep)
					{
						long fifty_time=System.currentTimeMillis();
						logger.debug("Step :"+limitStep+" Time: "+fifty_time);

						long time= (fifty_time - initial_time );

						logger.debug("Total Time : "+time);
						notifyArea.append("Total Simulation Time : "+time+"ms for "+limitStep+" steps.");
					}
					writeStepLabel.setText(""+mh.get("step"));
				}
			}
		});
		try{
			connectionForSteps.setupConnection(address);
			connectionForSteps.createTopic("step",1);
			connectionForSteps.subscribeToTopic("step");
			connectionForSteps.asynchronousReceive("step");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean initializeDefaultLabel(){

		int numOfPeer = root.getChildCount();
		//String regex="(\\d)+|((\\d)+\\.(\\d)+)";

		boolean allIsFine = true;

		//		if(textFieldRows.getText().equals("") || textFieldRows.getText().equals("0"))
		//			textFieldRows.setText("1");

		if(!textFieldRows.getText().isEmpty())
			rows = Integer.parseInt(textFieldRows.getText());
		else {
			textFieldRows.setText("1");
			rows = 1;
			allIsFine=false;
		}



		//		if(textFieldColumns.getText().equals("") || textFieldColumns.getText().equals("0"))
		//			textFieldColumns.setText("1");
		if(!textFieldColumns.getText().isEmpty())
			columns = Integer.parseInt(textFieldColumns.getText());
		else
		{
			textFieldColumns.setText("2");
			columns=2;
			allIsFine=false;
		}


		//		if(textFieldMaxDistance.getText().equals(""))
		//			textFieldMaxDistance.setText("0");


		if(!textFieldMaxDistance.getText().isEmpty())
			maxDistance = Integer.parseInt(textFieldMaxDistance.getText());
		else
		{
			textFieldMaxDistance.setText("1");
			maxDistance=1;
			allIsFine=false;
		}


		//width
		//		if(textFieldWidth.getText().equals(""))
		//			textFieldWidth.setText("0");
		if(!textFieldWidth.getText().isEmpty())
			WIDTH = Integer.parseInt(textFieldWidth.getText());
		else
		{
			textFieldWidth.setText("200");
			WIDTH=200;
			allIsFine=false;
		}

		//		if(textFieldHeight.getText().equals(""))
		//			textFieldHeight.setText("0");

		if(!textFieldHeight.getText().isEmpty())
			HEIGHT = Integer.parseInt(textFieldHeight.getText());
		else
		{
			textFieldHeight.setText("200");
			HEIGHT=200;
			allIsFine=false;
		}

		if(!textFieldAgents.getText().isEmpty())
			numAgents = Integer.parseInt(textFieldAgents.getText());
		else 
		{
			textFieldAgents.setText("15");
			numAgents=15;
			allIsFine=false;
		}

		if(!textFieldSteps.getText().isEmpty())
			steps = Integer.parseInt(textFieldSteps.getText());
		else
			steps = 0;

		labelWriteReg.setText(""+numRegions);
		labelWriteNumOfPeer.setText(""+numOfPeer);
		if(numOfPeer > 0 && (numRegions % numOfPeer) == 0)			
			labelWriteRegForPeer.setText(""+numRegions/numOfPeer);
		else 
			labelWriteRegForPeer.setText("");

		//		if(textFieldAgents.getText().equals(""))
		//			textFieldAgents.setText("0");
		
		if(Integer.parseInt(textFieldRows.getText()) == 1 || Integer.parseInt(textFieldColumns.getText()) == 1)
			isHorizontal = true;
		else
			isHorizontal = false;

		if(isHorizontal && !jCheckBoxLoadBalancing.isSelected()){
			MODE = DistributedField2D.UNIFORM_PARTITIONING_MODE;
			String w=(rows==1)?""+(int)(WIDTH/columns):""+WIDTH;
			String h=(columns==1)?""+(int)(HEIGHT/rows):""+HEIGHT;
			//if((Integer)WIDTH % columns == 0)
			labelWriteRegWidth.setText(""+w);
			labelWriteRegHeight.setText(""+h);
			labelWriteDistrMode.setText("HORIZONTAL MODE");
		}
		else if(!isHorizontal && !jCheckBoxLoadBalancing.isSelected()){
			MODE = DistributedField2D.UNIFORM_PARTITIONING_MODE;
			//int rad = (int) Math.sqrt(numRegions);
			String w="";
			String h="";
			//if((Integer)WIDTH % rad == 0){
			w = "" + (Integer)WIDTH/columns;
			h = "" + (Integer)HEIGHT/rows;
			//}
			labelWriteRegWidth.setText(""+w);
			labelWriteRegHeight.setText(""+h);
			labelWriteDistrMode.setText("SQUARE MODE");
		}
		else if (!isHorizontal && jCheckBoxLoadBalancing.isSelected()){
			MODE = DistributedField2D.SQUARE_BALANCED_DISTRIBUTION_MODE;
			int rad = (int) Math.sqrt(rows * columns);
			String w="";
			String h="";
			if((Integer)WIDTH % rad == 0){
				w = "" + (Integer)WIDTH/columns;
				h = "" + (Integer)HEIGHT/rows;
			}
			labelWriteRegWidth.setText(""+w);
			labelWriteRegHeight.setText(""+h);
			labelWriteDistrMode.setText("SQUARE BALANCED MODE");
		}
		else if(isHorizontal && jCheckBoxLoadBalancing.isSelected()){
			MODE = DistributedField2D.HORIZONTAL_BALANCED_DISTRIBUTION_MODE;
			String w="";
			String h=""+HEIGHT;
			//if((Integer)WIDTH % numRegions == 0)
			w = ""+(Integer)WIDTH/columns;
			labelWriteRegWidth.setText(""+w);
			labelWriteRegHeight.setText(""+h);
			labelWriteDistrMode.setText("HORIZONTAL BALANCED MODE");
		}

		labelWriteReg.setText(""+ rows*columns);
		if(root.getChildCount() != 0)
			labelWriteRegForPeer.setText(""+ (rows * columns)/root.getChildCount());

		if((rows < 3 && columns < 3 && rows == columns) || (rows != columns && rows > 1) || isThin)
			jCheckBoxLoadBalancing.setEnabled(false);
		else
			jCheckBoxLoadBalancing.setEnabled(true);

		return allIsFine;

	}

	private void confirm(){
		String key = tree1.getLastSelectedPathComponent().toString();
		int reg = (Integer)jComboBoxNumRegionXPeer.getSelectedItem();
		withGui = graphicONcheckBox.isSelected();
		EntryVal<Integer,Boolean> value = new EntryVal<Integer,Boolean>(reg,withGui);
		//config.put(key,reg);
		config.put(key, value);
	}


	/** System Management methods 
	 * @author marvit 
	 * */
	public File showFileChooser()
	{
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setCurrentDirectory(new File(FTP_HOME));
		int n = fileChooser.showOpenDialog(DMasonMaster.this);
		if (n == JFileChooser.APPROVE_OPTION) 
		{
			return  fileChooser.getSelectedFile();
		}
		else
			return null;
	}
	private void loadSimulation() 
	{

		jComboBoxChooseSimulation.removeAllItems();

		//These are hardcoded simulations
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Flockers", "it.isislab.dmason.sim.app.DFlockers.DFlockers"));
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Particles", "it.isislab.dmason.sim.app.DParticles.DParticles"));
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Ants Foraging", "it.isislab.dmason.sim.app.DAntsForage.DAntsForage"));
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Flockers Thin", "it.isislab.dmason.sim.app.DFlockersThin.DFlockers"));
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Particles Thin", "it.isislab.dmason.sim.app.DParticlesThin.DParticles"));
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Ants Foraging Thin", "it.isislab.dmason.sim.app.DAntsForageThin.DAntsForage"));
		//jComboBoxChooseSimulation.addItem(new SimComboEntry("Breadth First Search", "it.isislab.dmason.sim.app.DBreadthFirstSearch.Vertexes"));
		jComboBoxChooseSimulation.addItem(new SimComboEntry("Persons", "it.isislab.dmason.sim.app.DPersonsScom.DPersons"));
		
		// Then loads jar simulation from SIMULATION_DIR
		File folder = new File(FTP_HOME+dirSeparator+SIMULATION_DIR);
		File[] listOfFiles = folder.listFiles(); 
		if(folder!=null)
			for (File file : listOfFiles) {
				if (file.isFile() && FilenameUtils.isExtension(file.getName(), "jar")) 
					jComboBoxChooseSimulation.addItem(new SimComboEntry(file.getName(), file.getName()));
			}

	}

	// Manages the automatic update mechanism
	private void checkUpdate()
	{

		//if(autoconnect || toUpdate.isEmpty()) // All workers are updated
		if (!enableWorkersUpdate || toUpdate.isEmpty()) // All workers are update, or th euser choose not to update
		{	

			notifyArea.append("All workers are ready!\n");
			setConnectionSettingsEnabled(false);
			setSystemSettingsEnabled(true);
			jMenuItemUpdateWorker.setEnabled(true);

		}
		else
		{
			//if(curWorkerDigest != null)
			//{ 
			//System.out.println("ToUPDATE :"+toUpdate.size());
			JOptionPane.showMessageDialog(this,"Not all worker have the same version! Update needed");

			wu = new WorkerUpdater(getFPTAddress(),FTP_HOME,dirSeparator,master,toUpdate.size(),UPDATE_DIR,toUpdate);
			wu.setVisible(true);

			//Needs to know when the process is done
			wu.getObservable().addObserver(DMasonMaster.this);

			File updFile = new File(FTP_HOME+dirSeparator+UPDATE_DIR+dirSeparator+workerJarName);
			wu.setUpdateFile(updFile);
			wu.startAutoUpdate();

			toUpdate.clear();
		}
		//else
		//{ // There is no update file

		//loadUpdateFile();


	}

	private void loadUpdateFile() {
		String message =
				"There is no reference worker JAR. Do you want to specify a reference\n "
						+ "JAR for the worker?\n"
						+ "\n"
						+ "If you choose Yes, D-Mason will ensure that every worker is running an\n"
						+ "instance of the reference JAR and update them automatically.\n"
						+ "If you choose No, D-Mason won't perform any check (this is highly\n"
						+ "discouraged as it may lead to unreported errors during the simulation).\n"
						+ "If you choose Cancel, D-Mason will exit.\n\n";

		int res = JOptionPane.showConfirmDialog(this, message);

		if(res == JOptionPane.OK_OPTION)
		{
			updateFile = showFileChooser();

			if(updateFile != null)
			{
				File dest = new File(FTP_HOME+dirSeparator+UPDATE_DIR+dirSeparator+updateFile.getName());

				try {
					FileUtils.copyFile(updateFile, dest);

					Digester dg = new Digester(DigestAlgorithm.MD5);

					InputStream in = new FileInputStream(dest);
					curWorkerDigest = dg.getDigest(in);
					workerJarName = updateFile.getName();

					String fileName = FilenameUtils.removeExtension(updateFile.getName());
					dg.storeToPropFile(FTP_HOME+dirSeparator+UPDATE_DIR+dirSeparator+fileName+".hash");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//			else
			//			{
			//				JOptionPane.showMessageDialog(this, "User aborted, D-Mason will now exit.");
			//				this.dispose();
			//				System.exit(EXIT_ON_CLOSE);
			//			}

		}
		else if (res == JOptionPane.NO_OPTION)
		{
			enableWorkersUpdate = false;
			return;
		}
		if (updateFile == null || res == JOptionPane.CANCEL_OPTION || res == JOptionPane.CLOSED_OPTION)
		{
			JOptionPane.showMessageDialog(this, "User aborted, D-Mason will now exit.");
			this.dispose();
			System.exit(EXIT_ON_CLOSE);
		}
	}




	/*
	 * Why this? We can just use System.getProperty("file.separator");
	private static void setSeparator() 
	{
		OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

		if(os.getName().contains("Windows"))
			SEPARATOR = "\\";
		if(os.getName().contains("Linux") || os.getName().contains("OS X"))
			SEPARATOR = "/";
	}
	 */


	private Address getFPTAddress()
	{
		InetAddress thisIp;
		try {
			thisIp = InetAddress.getLocalHost(); //Note: It starts with Master
			return new Address(thisIp.getHostAddress(), FTP_PORT);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void startFtpServer()
	{
		FtpServerFactory serverFactory = new FtpServerFactory();

		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

		ConnectionConfigFactory connConfFactory = new ConnectionConfigFactory();
		connConfFactory.setAnonymousLoginEnabled(true);
		connConfFactory.setMaxAnonymousLogins(1000);
		connConfFactory.setMaxLogins(1000);

		serverFactory.setConnectionConfig(connConfFactory.createConnectionConfig());

		userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
		UserManager userManager = userManagerFactory.createUserManager();

		BaseUser user = new BaseUser();
		user.setName("anonymous");
		user.setPassword("");
		user.setHomeDirectory(FTP_HOME);
		List<Authority> auths = new ArrayList<Authority>();
		Authority auth = new WritePermission();
		auths.add(auth);
		user.setAuthorities(auths);
		try {
			userManager.save(user);
		} 
		catch (FtpException e1) {

			e1.printStackTrace();
		}

		ListenerFactory listenerFactory = new ListenerFactory();
		listenerFactory.setPort(Integer.parseInt(FTP_PORT));
		serverFactory.addListener("default",
				listenerFactory.createListener());

		serverFactory.setUserManager(userManager);

		FtpServer server = serverFactory.createServer();

		// start the server
		try {
			server.start();
			logger.debug("Server FTP started");
		} catch (FtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Creates the directories structures if not exists
	private static void checkFtpDirectories() 
	{
		boolean result;
		File homedir = new File(FTP_HOME);

		// if the directory does not exist, create it
		if (!homedir.exists())
		{
			result = homedir.mkdir();  
			if(result){    
				System.out.println("DIR "+ FTP_HOME+" created");  

				File simdir = new File(FTP_HOME+dirSeparator+SIMULATION_DIR);
				result = simdir.mkdir();
				if(result)   
					System.out.println("DIR "+ FTP_HOME+dirSeparator+SIMULATION_DIR+" created");
				File updatedir = new File(FTP_HOME+dirSeparator+UPDATE_DIR);
				result = updatedir.mkdir();
				if(result)   
					System.out.println("DIR "+ FTP_HOME+dirSeparator+UPDATE_DIR+" created");
			}

		}
		else
		{
			File simdir = new File(FTP_HOME+dirSeparator+SIMULATION_DIR);
			result = simdir.mkdir();
			if(result)   
				System.out.println("DIR "+ FTP_HOME+dirSeparator+SIMULATION_DIR+" created");
			File updatedir = new File(FTP_HOME+dirSeparator+UPDATE_DIR);
			result = updatedir.mkdir();
			if(result)   
				System.out.println("DIR "+ FTP_HOME+dirSeparator+UPDATE_DIR+" created");
		}
	}

	// receving notify from Worker Updater
	@Override
	public void update(Observable o, Object arg) 
	{
		int value;
		if(arg.equals("Update"))
		{
			wu.dispose();
			setConnectionSettingsEnabled(false);
			setSystemSettingsEnabled(true);
			jMenuItemUpdateWorker.setEnabled(true);
		}
		if(arg.equals("Test done"))
		{
			value = testCount.incrementAndGet();

			int progValue = (value*100)/totalTests;
			progressBarBatchTest.setValue(progValue);
			progressBarBatchTest.setString(progValue+" %");

			if(value ==  totalTests)
			{
				progressBarBatchTest.setString("Done!");
				long end = System.currentTimeMillis();
				long seconds = TimeUnit.SECONDS.convert((end - batchStartedTime), TimeUnit.MILLISECONDS) % 60;
				long minutes = TimeUnit.MINUTES.convert((end - batchStartedTime), TimeUnit.MILLISECONDS);
				long hours = TimeUnit.HOURS.convert((end - batchStartedTime), TimeUnit.MILLISECONDS);
				textAreaBatchInfo.append("Batch Ended at: "+Util.getCurrentDateTime(end)+"\n");
				textAreaBatchInfo.append("Batch execution time (h:m:s): "+hours+":"+minutes+":"+seconds+"\n");
				batchLogger.debug("Ended at: "+end);
			}
		}
	}

	// Used for manual update, called from MasterDeamonStarter
	public void incrementUpdatedWorker() 
	{
		wu.setProgress();
	}

	/**Used for setting the name of last worker version
	 * 
	 * @return 0 if no file
	 * 		   1 if more then 2 file
	 * 		   2 if number of file is correct
	 */
	private int getCurWorkerJarName()
	{
		File file = new File(FTP_HOME+dirSeparator+UPDATE_DIR);  
		File[] files = file.listFiles();

		if(files.length == 0)
			return 0;
		if(files.length > 2)
			return 1;
		if(files.length == 2)
		{
			for (File curfile : files) 
			{
				if (curfile.isFile() && FilenameUtils.isExtension(curfile.getName(), "jar"))
				{	
					workerJarName = curfile.getName();
					return 2;
				}
			}
			return 1;
		}
		return -1;
	}

	//Used for setting the digest last worker version
	private String getCurWorkerDigest()
	{
		int res = getCurWorkerJarName();
		if(res == 0)
			return null;
		if(res == 1){
			JOptionPane.showMessageDialog(this, "There is more one worker version in Updade dir, please leave only the last version");
			System.exit(1);
			return null;
		}
		if(res == 2)
		{
			File jarFile = new File(FTP_HOME+dirSeparator+UPDATE_DIR+dirSeparator+workerJarName);
			if(jarFile.exists())
			{
				String digestFile = FilenameUtils.removeExtension(workerJarName)+".hash";

				Digester dg = new Digester(DigestAlgorithm.MD5);

				return dg.loadFromPropFile(FTP_HOME+dirSeparator+UPDATE_DIR+dirSeparator+digestFile);
			}
			else
				return null;
		}
		else
		{
			return null;
		}

	}

	// Called by MasterDeamonStarter when receives WorkerInfo
	public void workerInfo(PeerStatusInfo workerInfo) 
	{
		totPeers++;
		if(workerInfo.getDigest() != null)
		{
			//System.out.println("Digest worker: "+workerInfo.getDigest()+" digest last: "+curWorkerDigest);
			if(!workerInfo.getDigest().equals(curWorkerDigest))
				toUpdate.add(workerInfo.getTopic());
		}
		else
		{
			// System.out.println("workerInfo.getDigest() is null");
		}

		scoreList.add(new EntryWorkerScore<Integer, String>(getPerformanceScore(workerInfo), workerInfo.getTopic()));
		if(totPeers == peers.size())
		{	
			checkUpdate();
			totPeers = 0;
			if(isBatchTest)
			{	
				nextTest();
			}
		}
	}

	private int getPerformanceScore(PeerStatusInfo workerInfo) {
		double cpuFactor = 1;
		double ramFactor = 1;
		return  (int) ((workerInfo.getNumCores()*cpuFactor) + ((workerInfo.getMemory()/(1024*1024))/1000)* ramFactor);
	}

	private static Set<List<EntryParam<String, Object>>> generateTestsFrom(Batch batch)
	{
		ArrayList<Set<EntryParam<String, Object>>> sets = new ArrayList<Set<EntryParam<String, Object>>>();
		int totalTests = 1;
		for (Param p : batch.getSimulationParams())
		{
			// System.out.println("Param: "+p.getName()+" mode "+p.getMode());

			Set<EntryParam<String, Object>> s = null;
			if(p.getMode().equals("fixed"))
			{
				ParamFixed pf = (ParamFixed) p;
				s = new HashSet<EntryParam<String, Object>>();
				if(pf.getType().equals("double"))
					s.add(new EntryParam<String, Object>(pf.getName(),Double.parseDouble(pf.getValue()),ParamType.SIMULATION));
				if(pf.getType().equals("int"))
					s.add(new EntryParam<String, Object>(pf.getName(),Integer.parseInt(pf.getValue()),ParamType.SIMULATION));
				if(pf.getType().equals("long"))
					s.add(new EntryParam<String, Object>(pf.getName(),Long.parseLong(pf.getValue()),ParamType.SIMULATION));

				totalTests *= s.size();
			}
			if(p.getMode().equals("range"))
			{
				ParamRange pr = (ParamRange) p;
				s = new HashSet<EntryParam<String, Object>>();
				if(pr.getType().equals("int"))
				{
					int end = Integer.parseInt(pr.getEnd());
					int inc = Integer.parseInt(pr.getIncrement());
					int start = Integer.parseInt(pr.getStart());
					int limit = (end-start)/inc;
					int last = start;
					s.add(new EntryParam<String, Object>(pr.getName(),start,ParamType.SIMULATION));
					for(int i=0;i<limit;i++)
					{		
						s.add(new EntryParam<String, Object>(pr.getName(),last+inc,ParamType.SIMULATION));
						last += inc;
					}
				}
				if(pr.getType().equals("double"))
				{
					double end = Double.parseDouble(pr.getEnd());
					double inc = Double.parseDouble(pr.getIncrement());
					double start = Double.parseDouble(pr.getStart());
					double limit = (end-start)/inc;
					double last = start;
					s.add(new EntryParam<String, Object>(pr.getName(),start,ParamType.SIMULATION));
					for(int i=0;i<limit;i++)
					{		
						s.add(new EntryParam<String, Object>(pr.getName(),last+inc,ParamType.SIMULATION));
						last += inc;
					}
				}


				totalTests *= s.size();
			}
			if(p.getMode().equals("list"))
			{
				ParamList pr = (ParamList) p;
				s = new HashSet<EntryParam<String, Object>>();

				for (String item : pr.getValues()) {
					if(pr.getType().equals("double"))
						s.add(new EntryParam<String, Object>(pr.getName(),Double.parseDouble(item),ParamType.SIMULATION));
					if(pr.getType().equals("int"))
						s.add(new EntryParam<String, Object>(pr.getName(),Integer.parseInt(item),ParamType.SIMULATION));
					if(pr.getType().equals("long"))
						s.add(new EntryParam<String, Object>(pr.getName(),Long.parseLong(item),ParamType.SIMULATION));
				}

				totalTests *= s.size();
			}
			if(p.getMode().equals("distribution"))
			{
				ParamDistribution pd = (ParamDistribution) p;
				s = new HashSet<EntryParam<String, Object>>();
				if(pd.getDistributionName().equals(DistributionType.uniform.name()))
				{
					ParamDistributionUniform pu = (ParamDistributionUniform) p;
					for (int i = 0; i < pu.getNumberOfValues(); i++) {
						if(pu.getType().equals("double"))
						{
							double value = StdRandom.uniform(Double.parseDouble(pu.getA()), Double.parseDouble(pu.getB()));
							s.add(new EntryParam<String, Object>(pu.getName(),value,ParamType.SIMULATION));
						}
						if(pu.getType().equals("int"))
						{
							int value = (int) Math.round(StdRandom.uniform(Double.parseDouble(pu.getA()), Double.parseDouble(pu.getB())));
							s.add(new EntryParam<String, Object>(pu.getName(),value,ParamType.SIMULATION));
						}
						if(pu.getType().equals("long"))
						{
							long value = Math.round(StdRandom.uniform(Double.parseDouble(pu.getA()), Double.parseDouble(pu.getB())));
							s.add(new EntryParam<String, Object>(pu.getName(),value,ParamType.SIMULATION));
						}


					}
				}
				if(pd.getDistributionName().equals(DistributionType.exponential.name()))
				{
					ParamDistributionExponential pe = (ParamDistributionExponential) p;
					for (int i = 0; i < pe.getNumberOfValues(); i++) {
						if(pe.getType().equals("double"))
						{
							double value = StdRandom.exponential(Double.parseDouble(pe.getLambda()));
							s.add(new EntryParam<String, Object>(pe.getName(),value,ParamType.SIMULATION));
						}
						if(pe.getType().equals("int"))
						{
							int value = (int) Math.round(StdRandom.exponential(Double.parseDouble(pe.getLambda())));
							s.add(new EntryParam<String, Object>(pe.getName(),value,ParamType.SIMULATION));
						}
						if(pe.getType().equals("long"))
						{
							long value = Math.round(StdRandom.exponential(Double.parseDouble(pe.getLambda())));
							s.add(new EntryParam<String, Object>(pe.getName(),value,ParamType.SIMULATION));
						}
					}
				}
				if(pd.getDistributionName().equals(DistributionType.normal.name()))
				{
					ParamDistributionNormal pn = (ParamDistributionNormal) p;
					System.out.println("Name: "+pn.getName()+" #Value: "+pn.getNumberOfValues());
					for (int i = 0; i < pn.getNumberOfValues(); i++) {
						if(pn.getType().equals("double"))
						{
							double value = StdRandom.gaussian(Double.parseDouble(pn.getMean()), Double.parseDouble(pn.getStdDev()));
							s.add(new EntryParam<String, Object>(pn.getName(),value,ParamType.SIMULATION));
						}
						if(pn.getType().equals("int"))
						{
							int value = (int) Math.round(StdRandom.gaussian(Double.parseDouble(pn.getMean()), Double.parseDouble(pn.getStdDev())));
							s.add(new EntryParam<String, Object>(pn.getName(),value,ParamType.SIMULATION));
						}
						if(pn.getType().equals("long"))
						{
							long value = Math.round(StdRandom.gaussian(Double.parseDouble(pn.getMean()), Double.parseDouble(pn.getStdDev())));
							s.add(new EntryParam<String, Object>(pn.getName(),value,ParamType.SIMULATION));
						}
					}
				}
				totalTests *= s.size();
			}
			if(s != null)
				sets.add(s);
		}
		for (Param p : batch.getGeneralParams())
		{
			System.out.println("Param: "+p.getName()+" mode "+p.getMode());

			Set<EntryParam<String, Object>> s = null;
			if(p.getMode().equals("fixed"))
			{
				ParamFixed pf = (ParamFixed) p;
				s = new HashSet<EntryParam<String, Object>>();
				// da aggiungere anche per range e per altri tipi
				if(pf.getType().equals("double"))
					s.add(new EntryParam<String, Object>(pf.getName(),Double.parseDouble(pf.getValue()),ParamType.GENERAL));
				if(pf.getType().equals("int"))
					s.add(new EntryParam<String, Object>(pf.getName(),Integer.parseInt(pf.getValue()),ParamType.GENERAL));
				if(pf.getType().equals("long"))
					s.add(new EntryParam<String, Object>(pf.getName(),Long.parseLong(pf.getValue()),ParamType.GENERAL));

				totalTests *= s.size();
			}
			if(p.getMode().equals("range"))
			{
				ParamRange pr = (ParamRange) p;
				s = new HashSet<EntryParam<String, Object>>();
				if(pr.getType().equals("int"))
				{
					int end = Integer.parseInt(pr.getEnd());
					int inc = Integer.parseInt(pr.getIncrement());
					int start = Integer.parseInt(pr.getStart());
					int limit = (end-start)/inc;
					int last = start;
					s.add(new EntryParam<String, Object>(pr.getName(),start,ParamType.GENERAL));
					for(int i=0;i<limit;i++)
					{		
						s.add(new EntryParam<String, Object>(pr.getName(),last+inc,ParamType.GENERAL));
						last += inc;
					}
				}
				if(pr.getType().equals("double"))
				{
					double end = Double.parseDouble(pr.getEnd());
					double inc = Double.parseDouble(pr.getIncrement());
					double start = Double.parseDouble(pr.getStart());
					double limit = (end-start)/inc;
					double last = start;
					s.add(new EntryParam<String, Object>(pr.getName(),start,ParamType.GENERAL));
					for(int i=0;i<limit;i++)
					{		
						s.add(new EntryParam<String, Object>(pr.getName(),last+inc,ParamType.GENERAL));
						last += inc;
					}
				}
				totalTests *= s.size();
			}
			if(p.getMode().equals("list"))
			{
				ParamList pr = (ParamList) p;
				s = new HashSet<EntryParam<String, Object>>();

				for (String item : pr.getValues()) {
					if(pr.getType().equals("double"))
						s.add(new EntryParam<String, Object>(pr.getName(),Double.parseDouble(item),ParamType.GENERAL));
					if(pr.getType().equals("int"))
						s.add(new EntryParam<String, Object>(pr.getName(),Integer.parseInt(item),ParamType.GENERAL));
					if(pr.getType().equals("long"))
						s.add(new EntryParam<String, Object>(pr.getName(),Long.parseLong(item),ParamType.GENERAL));
				}

				totalTests *= s.size();
			}
			if(p.getMode().equals("distribution"))
			{
				ParamDistribution pd = (ParamDistribution) p;
				s = new HashSet<EntryParam<String, Object>>();
				if(pd.getDistributionName().equals(DistributionType.uniform.name()))
				{
					ParamDistributionUniform pu = (ParamDistributionUniform) p;
					for (int i = 0; i < pu.getNumberOfValues(); i++) {
						if(pu.getType().equals("double"))
						{
							double value = StdRandom.uniform(Double.parseDouble(pu.getA()), Double.parseDouble(pu.getB()));
							s.add(new EntryParam<String, Object>(pu.getName(),value,ParamType.GENERAL));
						}
						if(pu.getType().equals("int"))
						{
							int value = (int) Math.round(StdRandom.uniform(Double.parseDouble(pu.getA()), Double.parseDouble(pu.getB())));
							s.add(new EntryParam<String, Object>(pu.getName(),value,ParamType.GENERAL));
						}
						if(pu.getType().equals("long"))
						{
							long value = Math.round(StdRandom.uniform(Double.parseDouble(pu.getA()), Double.parseDouble(pu.getB())));
							s.add(new EntryParam<String, Object>(pu.getName(),value,ParamType.GENERAL));
						}


					}
				}
				if(pd.getDistributionName().equals(DistributionType.exponential.name()))
				{
					ParamDistributionExponential pe = (ParamDistributionExponential) p;
					for (int i = 0; i < pe.getNumberOfValues(); i++) {
						if(pe.getType().equals("double"))
						{
							double value = StdRandom.exponential(Double.parseDouble(pe.getLambda()));
							s.add(new EntryParam<String, Object>(pe.getName(),value,ParamType.GENERAL));
						}
						if(pe.getType().equals("int"))
						{
							int value = (int) Math.round(StdRandom.exponential(Double.parseDouble(pe.getLambda())));
							s.add(new EntryParam<String, Object>(pe.getName(),value,ParamType.GENERAL));
						}
						if(pe.getType().equals("long"))
						{
							long value = Math.round(StdRandom.exponential(Double.parseDouble(pe.getLambda())));
							s.add(new EntryParam<String, Object>(pe.getName(),value,ParamType.GENERAL));
						}
					}
				}
				if(pd.getDistributionName().equals(DistributionType.normal.name()))
				{
					ParamDistributionNormal pn = (ParamDistributionNormal) p;
					for (int i = 0; i < pn.getNumberOfValues(); i++) {
						if(pn.getType().equals("double"))
						{
							double value = StdRandom.gaussian(Double.parseDouble(pn.getMean()), Double.parseDouble(pn.getStdDev()));
							s.add(new EntryParam<String, Object>(pn.getName(),value,ParamType.GENERAL));
						}
						if(pn.getType().equals("double"))
						{
							int value = (int) Math.round(StdRandom.gaussian(Double.parseDouble(pn.getMean()), Double.parseDouble(pn.getStdDev())));
							s.add(new EntryParam<String, Object>(pn.getName(),value,ParamType.GENERAL));
						}
						if(pn.getType().equals("double"))
						{
							long value = Math.round(StdRandom.gaussian(Double.parseDouble(pn.getMean()), Double.parseDouble(pn.getStdDev())));
							s.add(new EntryParam<String, Object>(pn.getName(),value,ParamType.GENERAL));
						}
					}
				}

				totalTests *= s.size();
			}

			if(s != null)
				sets.add(s);
		}

		/*System.out.println("Cartesian product size: "+totalTests);
		for (Set<EntryParam<String, Object>> set : sets) {
			System.out.println(set.toString());
		}*/
//		Set<List<EntryParam<String, Object>>> res = Sets.cartesianProduct(sets);
		/*System.out.println("Method 2 size: "+res.size());
	    System.out.println(res.toString()+"\n");*/
//return res;
		return null;
	}
	private static Batch loadConfigFromXML(File configFile)
	{

		XStream xstream = new XStream(new DomDriver("UTF-8"));
		xstream.processAnnotations(Batch.class);
		xstream.processAnnotations(Param.class);
		xstream.processAnnotations(ParamRange.class);
		xstream.processAnnotations(ParamFixed.class);
		xstream.processAnnotations(ParamDistributionExponential.class);
		xstream.processAnnotations(ParamDistributionNormal.class);
		xstream.processAnnotations(ParamDistributionUniform.class);
		xstream.processAnnotations(ParamList.class);

		try {
			return (Batch)xstream.fromXML(new FileReader(configFile.getAbsolutePath()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		}
	}
	private static boolean validateXML(File configFile,InputStream xsdFile) throws IOException
	{
		Source schemaFile = new StreamSource(xsdFile);
		Source xmlFile = new StreamSource(configFile);
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);


		try {

			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
			return true;
			//System.out.println(xmlFile.getSystemId() + " is valid");
		} catch (SAXException e) {
			//System.out.println(xmlFile.getSystemId() + " is NOT valid");
			//System.out.println("Reason: " + e.getLocalizedMessage());
			return false;
		}
	}

	public void countFinishedTest()
	{
		fineshed++;
		System.out.println("finsished: "+ fineshed +" tot: "+peers.size());
		if(fineshed == peers.size())
		{	
			fineshed = 0;

			nextTest();
		}
	}
	// Here I wakeup the BatchExecutor
	public void nextTest() 
	{
		// TODO Auto-generated method stub
		System.out.println("unlock");

		batchExec.setCanStartAnother(true);

		Lock batchLock = batchExec.getLock();
		batchLock.lock();
		{
			batchExec.getIsResetted().signalAll();
		}
		batchLock.unlock();
	}
	/* End System Management methods */



	/** UI Component */
	private JTextField getJTextFieldPathSimJar() {
		if(jTextFieldPathSimJar == null) {
			jTextFieldPathSimJar = new JTextField();
			jTextFieldPathSimJar.setText("PathSimulationJar");
		}
		return jTextFieldPathSimJar;
	}

	public void setCurWorkerDigest(String curWorkerDigest) {
		this.curWorkerDigest = curWorkerDigest;
	}

	private JButton getJButtonChoseSimJar() {
		if(jButtonChoseSimJar == null) {
			jButtonChoseSimJar = new JButton();
			jButtonChoseSimJar.setIcon(new ImageIcon("resources/image/openFolder.png"));
			jButtonChoseSimJar.setSize(16, 16);
		}
		return jButtonChoseSimJar;
	}

	private JMenu getJMenuSystem() {
		if(jMenuSystem == null) {
			jMenuSystem = new JMenu();
			jMenuSystem.setText("System");
			jMenuSystem.add(getJMenuItemUpdateWorker());
		}
		return jMenuSystem;
	}

	private JMenuItem getJMenuItemUpdateWorker() {
		if(jMenuItemUpdateWorker == null) {
			jMenuItemUpdateWorker = new JMenuItem();
			jMenuItemUpdateWorker.setText("Update Worker...");
			jMenuItemUpdateWorker.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {


					wu = new WorkerUpdater(getFPTAddress(),FTP_HOME,dirSeparator,master,peers.size(),UPDATE_DIR,DMasonMaster.this);
					wu.setVisible(true);

					wu.getObservable().addObserver(DMasonMaster.this);

				}
			});
		}

		jMenuItemUpdateWorker.setEnabled(false);
		return jMenuItemUpdateWorker;
	}



	private boolean isThinSimulation(String simulation) {

		URL url;
		Class c;
		Object instance;
		try {
			if(simulation.contains(".jar")){
				File simFile=new File(FTP_HOME+dirSeparator+SIMULATION_DIR+dirSeparator+simulation);
				url = new URL("file:" + simFile.getAbsolutePath());

				JarClassLoader cl = new JarClassLoader(url);

				cl.addToClassPath();

				String main = cl.getMainClassName();
				System.out.println("main: "+main);

				c = cl.loadClass(main);

			}
			else
			{	
				c = Class.forName(simulation);

			}
			return c.isAnnotationPresent(ThinAnnotation.class);



		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

}