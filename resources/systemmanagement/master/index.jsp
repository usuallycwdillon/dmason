<!DOCTYPE html>
<html>
	<head>
		<meta name="viewport" content="width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes">

		<title>DMASON - System Management</title>
		<link rel="shortcut icon" type="image/png" href="images/dmason-ico.png"/>

		<!-- Polyfill Web Components for older browsers -->
		<script src="bower_components/webcomponentsjs/webcomponents-lite.js"></script>

		<!-- Custom Polymer CSS -->
		<link rel="import" href="style/polymer/styles-polymer.html">

		<!-- Custom CSS -->
		<link href="style/custom-style.css" rel="stylesheet" type="text/css">

		<!-- jQuery -->
		<script src="js/jquery-1.12.4.min.js"></script>
		<!--<script src="bower_components/jquery-3.2.1.min/index.js"></script>-->

		<!-- Masonry lib -->
		<script src="js/masonry.pkgd.min.js"></script>

		<!-- Custom Scripts -->
		<script src="js/script.js"></script>

		<!-- Import simulation elements -->
		<link rel="import" href="custom_components/simulation/simulation-example-list.html">

		<!-- Import element -->
		<link rel="import" href="bower_components/app-layout/app-drawer-layout/app-drawer-layout.html">
		<link rel="import" href="bower_components/app-layout/app-drawer/app-drawer.html">
		<link rel="import" href="bower_components/app-layout/app-header-layout/app-header-layout.html">
		<link rel="import" href="bower_components/app-layout/app-header/app-header.html">
		<link rel="import" href="bower_components/app-layout/app-toolbar/app-toolbar.html">

		<link rel="import" href="bower_components/neon-animation/web-animations.html">
		<link rel="import" href="bower_components/neon-animation/animations/scale-up-animation.html">

		<link rel="import" href="bower_components/paper-dropdown-menu/paper-dropdown-menu.html">
		<link rel="import" href="bower_components/paper-styles/paper-styles.html">
		<link rel="import" href="bower_components/paper-icon-button/paper-icon-button.html">
		<link rel="import" href="bower_components/paper-menu/paper-menu.html">
		<link rel="import" href="bower_components/paper-item/paper-item.html">
		<link rel="import" href="bower_components/paper-fab/paper-fab.html">
		<link rel="import" href="bower_components/paper-toast/paper-toast.html">
		<link rel="import" href="bower_components/paper-dialog/paper-dialog.html">
		<link rel="import" href="bower_components/paper-dialog-scrollable/paper-dialog-scrollable.html">
		<link rel="import" href="bower_components/paper-button/paper-button.html">
		<link rel="import" href="bower_components/paper-radio-button/paper-radio-button.html">
		<link rel="import" href="bower_components/paper-radio-group/paper-radio-group.html">
		<link rel="import" href="bower_components/paper-input/paper-input.html">
		<link rel="import" href="bower_components/paper-progress/paper-progress.html">
		<link rel="import" href="bower_components/paper-spinner/paper-spinner.html">
		<link rel="import" href="bower_components/paper-input/paper-input-container.html">
		<link rel="import" href="bower_components/paper-input/paper-input-error.html">
		<link rel="import" href="bower_components/paper-checkbox/paper-checkbox.html">
		<link rel="import" href="bower_components/paper-listbox/paper-listbox.html">

		<link rel="import" href="bower_components/iron-icons/iron-icons.html">
		<link rel="import" href="bower_components/iron-icons/maps-icons.html">
		<link rel="import" href="bower_components/iron-flex-layout/iron-flex-layout-classes.html">
		<link rel="import" href="bower_components/iron-image/iron-image.html">
		<link rel="import" href="bower_components/iron-icons/image-icons.html">
		<link rel="import" href="bower_components/iron-form/iron-form.html">
	</head>
	<body unresolved>
		<!-- Testata pagina -->
		<app-header reveals fixed slot="header">
			<app-toolbar flex id="mainToolBar" class="horizontal">
				<paper-icon-button icon="menu" onclick="drawer.toggle()" drawer-toggle></paper-icon-button>
				<div class="flex" spacer main-title><span>DMASON Master</span></div>
				<div onclick="selectAllWorkers()" class="selectAllWorker">
					<paper-icon-button icon="select-all"></paper-icon-button><span>Select all workers</span>
				</div>
			</app-toolbar>
		</app-header>

		<!-- Import bean MasterServer -->
		<jsp:useBean id="masterServer" class="it.isislab.dmason.experimentals.systemmanagement.master.MasterServer" scope="application"/>

		<!-- Corpo pagina -->
		<div class="content content-main">
			<!-- Griglia nodi lavoratori -->
			<div class="grid-monitoring" id="workers">
				<div class=\"grid-sizer-monitoring\"></div>
			</div>

			<!-- Loader caricamento e spegnimento nodi lavoratori -->
			<paper-dialog opened id="load_workers_dialog"  entry-animation="scale-up-animation" exit-animation="fade-out-animation" modal>
				<div class="layout horizontal center">
					<paper-spinner class="multi" active alt="Loading workers list"></paper-spinner>
					<span style="margin-left:5px;">Loading workers list...</span>
				</div>
			</paper-dialog>

			<paper-dialog id="shutdown_workers_dialog"  entry-animation="scale-up-animation" exit-animation="fade-out-animation" modal>
				<div class="layout horizontal center">
					<paper-spinner class="multi" active alt="Shutdown selected workers"></paper-spinner>
					<span style="margin-left:5px;">Shutdown selected workers...</span>
				</div>
			</paper-dialog>
	
			<!-- Bottoni in fondo e messaggi di avviso -->
			<paper-fab id="add-simulation-to-worker-buttom" icon="add" onclick="open_dialog_setting_new_simulation()"></paper-fab>
			<paper-toast id="miss-worker-selection">You should select some workers before to assign them a partitioning.</paper-toast>

			<paper-fab id="shutdown-worker-button" icon="settings-power" onclick="shutdown()"></paper-fab>
			<paper-toast id="miss-worker-shutdown">You need select some workers before shutdown them.</paper-toast>

			<!-- Pannello aggiunta nuova simulazione -->
			<paper-dialog id="add-simulation-paper-dialog" entry-animation="scale-up-animation" exit-animation="fade-out-animation" modal with-backdrop>
				<div class="layout vertical center">
					<h1>Simulation Settings</h1>
					<h2>Worker(s) selected: <span id="head_sel_works"></span> Available slot(s): <span id="head_num_slots"></span></h2>
				</div>
	
            	<paper-dialog-scrollable>
                    <div class="horizontal-section">
                        <form is="iron-form" id="sendSimulationForm">
                            <table>
                                <tr>
                                    <td>
                                        <span>Select an external simulation</span><br />
                                        <paper-button raised class="custom" onclick='open_file_chooser()'>Upload<iron-icon icon="file-upload"></iron-icon></paper-button>
                                        <input type="file" class="hidden" id="simulation-jar-chooser" name="simExe">
	                                </td>
                                    <td></td>
                                    <td>
	                                    <span>Select an example simulation</span><br />
                                        <!--paper-dropdown-menu id="exampleSimulation" label="Select">
                                            <paper-listbox class="dropdown-content">
                                                <paper-item id="examplesJarlist">Examples</paper-item>
                                                <paper-item id="customsJarlist">Customs</paper-item>
                                           	</paper-listbox>
                                        </paper-dropdown-menu-->
                                      	<simulation-example-list id="loader_sims_list_example"></simulation-example-list>
                                        <!--paper-button raised class="custom">Select<iron-icon icon="receipt"></iron-icon></paper-button-->
                                    </td>
                                </tr>
                                <tr>
									<td colspan="3"><paper-progress class="red"></paper-progress></td>
								</tr>
                                <tr>
                                    <td colspan="2" style="text-align:center; text-transform: uppercase;"><span>Partitioning</span></td>
                                    <td style="text-align:center; text-transform: uppercase;"><span>extra</span></td>
                                </tr>
                                <tr>
                                    <td colspan="2" style="text-align:center">
                                        <paper-radio-group id="partitioning" selected="uniform">
                                            <paper-radio-button required name="uniform" onclick="change_partitioning_input_params(this)"><span>Uniform  <iron-icon icon="view-module"></iron-icon></span></paper-radio-button>
                                            <paper-radio-button required name="non-uniform" onclick="change_partitioning_input_params(this)"> <span>Non-Uniform <iron-icon icon="view-quilt"></iron-icon></span></paper-radio-button>
                                        </paper-radio-group>
                                    </td>
                                    <td><paper-checkbox id="connectionType" class="layout horizontal center">enable MPI boost</paper-checkbox></td>
                                </tr>
                                <tr>
                                    <td colspan="3" style="text-align:center; text-transform: uppercase;"><span>parameters</span></td>
                                </tr>
                                <tr>
                                    <td>
                                        <paper-input id="form_simName" class="submit_work_form" name="simName" label="Simulation name" allowed-pattern="[a-zA-Z0-9]"></paper-input>
                                    </td>
                                    <td>
                                        <paper-input id="form_steps" class="submit_work_form" name="step" label="Number of step" allowed-pattern="[0-9]" error-message="Illegal value" onInput="_validate_params(this)"></paper-input>
                                    </td>
                                    <td>
                                        <paper-input disabled id="form_cells" class="submit_work_form" name="cells" label="Cells" allowed-pattern="[0-9]" error-message="Cell value either it exceeds available slots or it is zero!" onInput="_validate_slots(this)"></paper-input>
                                        <!--paper-dropdown-menu id="connectionType" label="Select connection" class="submit_work_form">
                                            <paper-listbox class="dropdown-content" selected="0">
                                                <paper-item label="ActiveMQ">ActiveMQ</paper-item>
                                                <paper-item label="MPI">MPI</paper-item>
                                            </paper-listbox>
                                        </paper-dropdown-menu-->
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <paper-input id="form_row" class="submit_work_form" name="rows" label="Rows" allowed-pattern="[0-9]" error-message="R x C value either it exceeds available slots or it is zero!" onInput="_validate_slots(this)"></paper-input>
                                    </td>
                                    <td>
                                        <paper-input id="form_col" class="submit_work_form" name="cols" label="Columns" allowed-pattern="[0-9]" error-message="R x C value either it exceeds available slots or it is zero!" onInput="_validate_slots(this)"></paper-input>
                                    </td>
                                    <td>
										<paper-input id="form_aoi" class="submit_work_form" name="aoi" label="Area of interest" allowed-pattern="[0-9]" error-message="Illegal value" onInput="_validate_params(this)"></paper-input>
									</td>
                                </tr>
                                <tr>
                                    <td>
                                      	<paper-input id="form_width" class="submit_work_form" name="width" label="Width" allowed-pattern="[0-9]" error-message="Illegal value" onInput="_validate_params(this)"></paper-input>
									</td>
                                    <td>
                                       	<paper-input id="form_height" class="submit_work_form" name="height" label="Height" allowed-pattern="[0-9]" error-message="Illegal value" onInput="_validate_params(this)"></paper-input>
                                    </td>
                                    <td>
                                       	<paper-input id="form_numAgents" class="submit_work_form" name="numAgents" label="Number of Agents" allowed-pattern="[0-9]" error-message="Illegal value" onInput="_validate_params(this)"></paper-input>
	                                </td>
                                </tr>
                                <tr>
                                	<td></td>
                                    <td colspan="2" style="text-align:right; padding-top:50px;">
                                        <paper-button raised onclick="resetForm(event)">Reset</paper-button>
										<paper-button id="submit_btn" raised onclick="submitForm()" dialog-confirm>Submit</paper-button>
										<paper-button raised dialog-dismiss autofocus>Cancel</paper-button>
                                    </td>
	                            </tr>
                            </table>
                        </form>
                        <paper-toast id="missing_settings">You should fill the other field(s)</paper-toast>
                    </div>
                </paper-dialog-scrollable>
            </paper-dialog>
        </div>

		<!-- menu laterale a scorrimento -->
		<app-drawer id="drawer" slot="drawer" swipe-open>
			<app-header-layout id="side-header-panel" fixed fill>
				<!-- header del drawer -->
				<app-toolbar class="side-drawer">
					<div style="margin-right:5px;">Control Panel</div>
					<paper-icon-button icon="chevron-left" onclick="drawer.toggle()"></paper-icon-button>
				</app-toolbar>
				<!-- menu drawer -->
				<nav class="content content-side-bar">
					<paper-menu selected="0">
						<paper-item class="selected">
							<a href="index.jsp">
								<iron-icon icon="icons:flip-to-front" item-icon slot="item-icon"></iron-icon>
								<span class="span-icon">Monitoring</span>
							</a>
						</paper-item>
						<paper-item>
							<a href="simulations.jsp">
								<iron-icon icon="image:blur-on" item-icon slot="item-icon"></iron-icon>
								<span class="span-icon">Simulations</span>
							</a>
						</paper-item>
						<paper-item>
							<a href="history.jsp">
								<iron-icon icon="history" item-icon slot="item-icon"></iron-icon>
								<span class="span-icon">History</span>
							</a>
						</paper-item>
						<paper-item>
							<a href="settings.jsp">
								<iron-icon icon="settings" item-icon slot="item-icon"></iron-icon>
								<span class="span-icon">Settings</span>
							</a>
						</paper-item>
					</paper-menu>
				<nav>
			</app-header-layout>
		</app-drawer>
	</body>
</html>
