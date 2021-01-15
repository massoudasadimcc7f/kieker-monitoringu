/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.gui.view;

import java.util.List;

import kieker.gui.model.AggregatedExecutionEntry;
import kieker.gui.model.DataSource;
import kieker.gui.model.ExecutionEntry;
import kieker.gui.model.Properties;
import kieker.gui.model.RecordEntry;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * The main window of this application. This file should probably be maintained with the Eclipse GUI builder.
 *
 * @author Nils Christian Ehmke
 */
public class MainWindow {

	protected Shell shell;
	private Composite mainComposite;
	private Table recordsTable;
	private SashForm outerForm;
	private TreeViewer explorerTreeViewer;
	private TreeItem explorerTreeItem;
	TreeItem recordsTreeItem;
	TreeItem executionTracesTreeItem;
	private TableViewer recordsTableViewer;
	private TableColumn recordsTableTimestampColumn;
	private TableColumn recordsTableRecordColumn;
	private Menu menuBar;
	private MenuItem fileMenuItem;
	private Menu fileMenu;
	private MenuItem openMonitoringLogMenuItem;
	private MenuItem exitMenuItem;
	private MenuItem helpMenuItem;
	private Menu helpMenu;
	private MenuItem aboutMenuItem;
	private Tree explorerTree;
	private Tree executionTracesTree;
	private TreeColumn tracesTreeContainerColumn;
	private TreeColumn treeColumn_8;
	private TreeColumn treeColumn_10;
	private TreeColumn treeColumn_11;
	private TreeColumn treeColumn_16;
	private MenuItem mntmView_1;
	private Menu menu;
	private MenuItem mntmShortComponentNames;
	private MenuItem mntmLongComponentNames;
	private MenuItem mntmShortOperationParameters;
	private MenuItem mntmLongOperationParameters;
	private TableColumn recordsTableTypeColumn;
	private SashForm explorerForm;
	private final DataSource model = new DataSource();
	private TreeColumn trclmnPercent;
	Label lblNa;
	private SashForm executionTracesForm;
	private Composite executionTracesDetailComposite;
	private Label lblTraceId;
	Label lblNa_1;
	Label lblFailed;
	Label lblNa_2;
	private Label lblDuration;
	Label lblNa_3;
	private Label lblExecutionContainer;
	private Label lblComponent;
	private Label lblOperation;
	Label lblNa_4;
	Label lblNa_5;
	Label lblNa_6;
	private Label lblStackDepth;
	Label lblNa_7;
	private SashForm sashForm;
	private Tree tree_1;
	private TreeColumn treeColumn;
	private TreeColumn treeColumn_1;
	private TreeColumn treeColumn_2;
	private TreeColumn trclmnCalls;
	private Composite composite;
	private Label label;
	private Label label_1;
	private Label label_2;
	private Label label_3;
	private Label label_4;
	private Label label_5;
	private Label label_10;
	private Label label_11;
	private Label label_12;
	private Label label_13;
	TreeItem trtmAggregatedExecutionTraces;

	public static void main(final String[] args) {
		final MainWindow window = new MainWindow();
		window.open();
	}

	public void open() {
		final Display display = Display.getDefault();
		this.createContents();
		this.model.loadMonitoringLogFromFS("testdata");
		this.shell.open();
		this.shell.layout();
		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents() {
		this.shell = new Shell();
		this.shell.setImage(null);
		this.shell.setMaximized(true);
		this.shell.setText("Kieker's GUI");
		this.shell.setLayout(new GridLayout(1, false));

		this.outerForm = new SashForm(this.shell, SWT.NONE);
		this.outerForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		this.explorerForm = new SashForm(this.outerForm, SWT.VERTICAL);

		this.explorerTreeViewer = new TreeViewer(this.explorerForm, SWT.BORDER);
		this.explorerTree = this.explorerTreeViewer.getTree();

		this.explorerTreeItem = new TreeItem(this.explorerTree, SWT.NONE);
		this.explorerTreeItem.setText("Explorer");

		this.recordsTreeItem = new TreeItem(this.explorerTreeItem, SWT.NONE);
		this.recordsTreeItem.setText("Records");

		this.executionTracesTreeItem = new TreeItem(this.explorerTreeItem, SWT.NONE);
		this.executionTracesTreeItem.setText("Execution Traces");

		this.trtmAggregatedExecutionTraces = new TreeItem(this.executionTracesTreeItem, SWT.NONE);
		this.trtmAggregatedExecutionTraces.setText("Aggregated Execution Traces");

		this.executionTracesTreeItem.setExpanded(true);
		this.explorerTreeItem.setExpanded(true);

		this.explorerTree.addSelectionListener(new ExplorerTreeSelectionAdapter(this));
		this.explorerForm.setWeights(new int[] { 3 });

		this.mainComposite = new Composite(this.outerForm, SWT.NONE);
		this.mainComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.mainComposite.setLayout(new StackLayout());

		this.recordsTableViewer = new TableViewer(this.mainComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.recordsTable = this.recordsTableViewer.getTable();
		this.recordsTable.setHeaderVisible(true);
		this.recordsTable.addListener(SWT.SetData, new RecordsTableSetDataListener());

		this.recordsTableTimestampColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableTimestampColumn.setWidth(100);
		this.recordsTableTimestampColumn.setText("Timestamp");
		this.recordsTableTimestampColumn.addListener(SWT.Selection, new RecordsTableTimestampSortListener());

		this.recordsTableTypeColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableTypeColumn.setWidth(100);
		this.recordsTableTypeColumn.setText("Type");
		this.recordsTableTypeColumn.addListener(SWT.Selection, new RecordsTableTypeSortListener());

		this.recordsTableRecordColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableRecordColumn.setWidth(100);
		this.recordsTableRecordColumn.setText("Record");
		this.recordsTableRecordColumn.addListener(SWT.Selection, new RecordsTableTimestampSortListener());

		this.executionTracesForm = new SashForm(this.mainComposite, SWT.VERTICAL);

		this.executionTracesTree = new Tree(this.executionTracesForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.executionTracesTree.setHeaderVisible(true);
		this.executionTracesTree.addListener(SWT.SetData, new ExecutionTracesTreeSetDataListener());
		this.executionTracesTree.addSelectionListener(new ExecutionTraceTreeSelectionListener(this));
		Properties.getInstance().addObserver(new TreeUpdateObserver(this.executionTracesTree));

		this.tracesTreeContainerColumn = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.tracesTreeContainerColumn.setWidth(100);
		this.tracesTreeContainerColumn.setText("Execution Container");

		this.treeColumn_8 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_8.setWidth(100);
		this.treeColumn_8.setText("Component");

		this.treeColumn_10 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_10.setWidth(100);
		this.treeColumn_10.setText("Operation");

		this.treeColumn_11 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_11.setWidth(100);
		this.treeColumn_11.setText("Duration");

		this.trclmnPercent = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.trclmnPercent.setWidth(100);
		this.trclmnPercent.setText("Percent");

		this.treeColumn_16 = new TreeColumn(this.executionTracesTree, SWT.NONE);
		this.treeColumn_16.setWidth(100);
		this.treeColumn_16.setText("Trace ID");

		this.executionTracesDetailComposite = new Composite(this.executionTracesForm, SWT.BORDER);
		this.executionTracesDetailComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.executionTracesDetailComposite.setLayout(new GridLayout(2, false));

		this.lblExecutionContainer = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblExecutionContainer.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblExecutionContainer.setText("Execution Container:");

		this.lblNa_4 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.lblNa_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_4.setText("N/A");

		this.lblComponent = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblComponent.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblComponent.setText("Component:");

		this.lblNa_5 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_5.setText("N/A");

		this.lblOperation = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblOperation.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblOperation.setText("Operation:");

		this.lblNa_6 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_6.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_6.setText("N/A");

		this.lblTraceId = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblTraceId.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblTraceId.setText("Trace ID:");

		this.lblNa_1 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_1.setText("N/A");

		this.lblDuration = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblDuration.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblDuration.setText("Duration:");

		this.lblNa_3 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_3.setText("N/A");

		this.lblStackDepth = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblStackDepth.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblStackDepth.setText("Stack Depth:");

		this.lblNa_7 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_7.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_7.setText("N/A");

		this.lblFailed = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblFailed.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblFailed.setText("Failed:");

		this.lblNa_2 = new Label(this.executionTracesDetailComposite, SWT.NONE);
		this.lblNa_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.lblNa_2.setText("N/A");
		this.executionTracesForm.setWeights(new int[] { 2, 1 });

		this.sashForm = new SashForm(this.mainComposite, SWT.VERTICAL);

		this.tree_1 = new Tree(this.sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.tree_1.setHeaderVisible(true);
		this.tree_1.addListener(SWT.SetData, new AggregatedExecutionTracesTreeSetDataListener());
		Properties.getInstance().addObserver(new TreeUpdateObserver(this.tree_1));

		this.treeColumn = new TreeColumn(this.tree_1, SWT.NONE);
		this.treeColumn.setWidth(100);
		this.treeColumn.setText("Execution Container");

		this.treeColumn_1 = new TreeColumn(this.tree_1, SWT.NONE);
		this.treeColumn_1.setWidth(100);
		this.treeColumn_1.setText("Component");

		this.treeColumn_2 = new TreeColumn(this.tree_1, SWT.NONE);
		this.treeColumn_2.setWidth(100);
		this.treeColumn_2.setText("Operation");

		this.trclmnCalls = new TreeColumn(this.tree_1, SWT.NONE);
		this.trclmnCalls.setWidth(100);
		this.trclmnCalls.setText("# Calls");

		this.composite = new Composite(this.sashForm, SWT.BORDER);
		this.composite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.composite.setLayout(new GridLayout(2, false));

		this.label = new Label(this.composite, SWT.NONE);
		this.label.setText("Execution Container:");
		this.label.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_1 = new Label(this.composite, SWT.NONE);
		this.label_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		this.label_1.setText("N/A");
		this.label_1.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_2 = new Label(this.composite, SWT.NONE);
		this.label_2.setText("Component:");
		this.label_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_3 = new Label(this.composite, SWT.NONE);
		this.label_3.setText("N/A");
		this.label_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_4 = new Label(this.composite, SWT.NONE);
		this.label_4.setText("Operation:");
		this.label_4.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_5 = new Label(this.composite, SWT.NONE);
		this.label_5.setText("N/A");
		this.label_5.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_10 = new Label(this.composite, SWT.NONE);
		this.label_10.setText("Stack Depth:");
		this.label_10.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_11 = new Label(this.composite, SWT.NONE);
		this.label_11.setText("N/A");
		this.label_11.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_12 = new Label(this.composite, SWT.NONE);
		this.label_12.setText("Failed:");
		this.label_12.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));

		this.label_13 = new Label(this.composite, SWT.NONE);
		this.label_13.setText("N/A");
		this.label_13.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.sashForm.setWeights(new int[] { 2, 1 });

		this.outerForm.setWeights(new int[] { 2, 4 });

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		this.fileMenuItem = new MenuItem(this.menuBar, SWT.CASCADE);
		this.fileMenuItem.setText("File");

		this.fileMenu = new Menu(this.fileMenuItem);
		this.fileMenuItem.setMenu(this.fileMenu);

		this.openMonitoringLogMenuItem = new MenuItem(this.fileMenu, SWT.NONE);
		this.openMonitoringLogMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				MainWindow.this.showOpenMonitoringLogDialog();
			}
		});
		this.openMonitoringLogMenuItem.setText("Open Monitoring Log");

		new MenuItem(this.fileMenu, SWT.SEPARATOR);

		this.exitMenuItem = new MenuItem(this.fileMenu, SWT.NONE);
		this.exitMenuItem.setText("Exit");

		this.mntmView_1 = new MenuItem(this.menuBar, SWT.CASCADE);
		this.mntmView_1.setText("View");

		this.menu = new Menu(this.mntmView_1);
		this.mntmView_1.setMenu(this.menu);

		this.mntmShortOperationParameters = new MenuItem(this.menu, SWT.RADIO);
		this.mntmShortOperationParameters.setSelection(true);
		this.mntmShortOperationParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortOperationParameters(true);
			}
		});
		this.mntmShortOperationParameters.setText("Short Operation Parameters");

		this.mntmLongOperationParameters = new MenuItem(this.menu, SWT.RADIO);
		this.mntmLongOperationParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortOperationParameters(false);
			}
		});
		this.mntmLongOperationParameters.setText("Long Operation Parameters");

		new MenuItem(this.menu, SWT.SEPARATOR);

		this.mntmShortComponentNames = new MenuItem(this.menu, SWT.RADIO);
		this.mntmShortComponentNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortComponentNames(true);
			}
		});
		this.mntmShortComponentNames.setText("Short Component Names");

		this.mntmLongComponentNames = new MenuItem(this.menu, SWT.RADIO);
		this.mntmLongComponentNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortComponentNames(false);
			}
		});
		this.mntmLongComponentNames.setSelection(true);
		this.mntmLongComponentNames.setText("Long Component Names");

		this.helpMenuItem = new MenuItem(this.menuBar, SWT.CASCADE);
		this.helpMenuItem.setText("Help");

		this.helpMenu = new Menu(this.helpMenuItem);
		this.helpMenuItem.setMenu(this.helpMenu);

		this.aboutMenuItem = new MenuItem(this.helpMenu, SWT.NONE);
		this.aboutMenuItem.setText("About...");

		this.lblNa = new Label(this.shell, SWT.NONE);
		this.lblNa.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	}

	protected void showSystemModel() {
		// Show the tree

	}

	protected void showAggregatedExecutionTraces() {
		// Show the tree
		this.setVisibleMainComponent(this.sashForm);

		// Reload the data from the model...
		final List<AggregatedExecutionEntry> traces = this.model.getAggregatedTrace();

		// ...and write it into the tree
		this.tree_1.setItemCount(traces.size());
		this.tree_1.setData(traces);
		this.lblNa.setText(Integer.toString(traces.size()) + " Aggregated Traces");
		this.lblNa.pack();

		// Resize the columns
		for (final TreeColumn column : this.tree_1.getColumns()) {
			column.pack();
		}
	}

	protected void showAggregatedExecutionTracesByContainer() {

	}

	protected void showAggregatedExecutionTracesByComponent() {

	}

	protected void showExecutionTraces() {
		// Show the tree
		this.setVisibleMainComponent(this.executionTracesForm);

		// Reload the data from the model...
		final List<ExecutionEntry> traces = this.model.getTraces();

		// ...and write it into the tree
		this.executionTracesTree.setItemCount(traces.size());
		this.executionTracesTree.setData(traces);
		this.lblNa.setText(Integer.toString(traces.size()) + " Traces");
		this.lblNa.pack();

		// Resize the columns
		for (final TreeColumn column : this.executionTracesTree.getColumns()) {
			column.pack();
		}
	}

	protected void showRecords() {
		// Show the table
		this.setVisibleMainComponent(this.recordsTable);

		// Reload the data from the model...
		final List<RecordEntry> records = this.model.getRecords();

		// ...and write it into the table
		this.recordsTable.setItemCount(records.size());
		this.recordsTable.setData(records);
		this.lblNa.setText(Integer.toString(records.size()) + " Records");
		this.lblNa.pack();

		// Resize the columns
		for (final TableColumn column : this.recordsTable.getColumns()) {
			column.pack();
		}
	}

	protected void showOpenMonitoringLogDialog() {
		final DirectoryDialog dialog = new DirectoryDialog(this.shell);
		final String selectedDirectory = dialog.open();

		if (null != selectedDirectory) {
			this.model.loadMonitoringLogFromFS(selectedDirectory);
		}
	}

	void setVisibleMainComponent(final Control component) {
		final StackLayout layout = (StackLayout) this.mainComposite.getLayout();
		layout.topControl = component;

		this.mainComposite.layout();
	}
}
