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
import java.util.Observable;
import java.util.Observer;

import kieker.gui.model.DataSource;
import kieker.gui.model.Properties;
import kieker.gui.model.domain.AggregatedExecutionEntry;
import kieker.gui.model.domain.ExecutionEntry;
import kieker.gui.model.domain.RecordEntry;
import kieker.gui.view.util.AbstractDirectedComparator;
import kieker.gui.view.util.AggregatedExecutionTracesTreeSetDataListener;
import kieker.gui.view.util.ExecutionTracesTreeSetDataListener;
import kieker.gui.view.util.RecordEntryTimestampComparator;
import kieker.gui.view.util.RecordEntryTypeComparator;
import kieker.gui.view.util.RecordsTableSetDataListener;
import kieker.gui.view.util.TableColumnSortListener;
import kieker.gui.view.util.TreeColumnSortListener;

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
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * The main window of this application. This file should probably be maintained with the Eclipse GUI builder.
 *
 * @author Nils Christian Ehmke
 */
public final class MainWindow {

	protected Shell shell;
	private Composite mainComposite;
	Table recordsTable;
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
	private Tree tracesTree;
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
	private TreeColumn trclmnPercent;
	Label lblNa;
	private SashForm executionTracesForm;
	private TraceDetailComposite traceDetailComposite;
	private SashForm sashForm;
	private Tree aggregatedTracesTree;
	private TreeColumn treeColumn;
	private TreeColumn treeColumn_1;
	private TreeColumn treeColumn_2;
	private TreeColumn trclmnCalls;
	private AggregatedTraceDetailComposite aggregatedTraceDetailComposite;
	TreeItem trtmAggregatedExecutionTraces;

	public static void main(final String[] args) {
		final MainWindow window = new MainWindow();
		window.open();
	}

	public void open() {
		final Display display = Display.getDefault();
		this.createContents();
		this.addLogic();
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

		this.explorerForm.setWeights(new int[] { 3 });

		this.mainComposite = new Composite(this.outerForm, SWT.NONE);
		this.mainComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		this.mainComposite.setLayout(new StackLayout());

		this.recordsTableViewer = new TableViewer(this.mainComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.recordsTable = this.recordsTableViewer.getTable();
		this.recordsTable.setHeaderVisible(true);

		this.recordsTableTimestampColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableTimestampColumn.setWidth(100);
		this.recordsTableTimestampColumn.setText("Timestamp");

		this.recordsTableTypeColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableTypeColumn.setWidth(100);
		this.recordsTableTypeColumn.setText("Type");

		this.recordsTableRecordColumn = new TableColumn(this.recordsTable, SWT.NONE);
		this.recordsTableRecordColumn.setWidth(100);
		this.recordsTableRecordColumn.setText("Record");

		this.executionTracesForm = new SashForm(this.mainComposite, SWT.VERTICAL);

		this.tracesTree = new Tree(this.executionTracesForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.tracesTree.setHeaderVisible(true);

		this.tracesTreeContainerColumn = new TreeColumn(this.tracesTree, SWT.NONE);
		this.tracesTreeContainerColumn.setWidth(100);
		this.tracesTreeContainerColumn.setText("Execution Container");

		this.treeColumn_8 = new TreeColumn(this.tracesTree, SWT.NONE);
		this.treeColumn_8.setWidth(100);
		this.treeColumn_8.setText("Component");

		this.treeColumn_10 = new TreeColumn(this.tracesTree, SWT.NONE);
		this.treeColumn_10.setWidth(100);
		this.treeColumn_10.setText("Operation");

		this.treeColumn_11 = new TreeColumn(this.tracesTree, SWT.NONE);
		this.treeColumn_11.setWidth(100);
		this.treeColumn_11.setText("Duration");

		this.trclmnPercent = new TreeColumn(this.tracesTree, SWT.NONE);
		this.trclmnPercent.setWidth(100);
		this.trclmnPercent.setText("Percent");

		this.treeColumn_16 = new TreeColumn(this.tracesTree, SWT.NONE);
		this.treeColumn_16.setWidth(100);
		this.treeColumn_16.setText("Trace ID");

		this.traceDetailComposite = new TraceDetailComposite(this.executionTracesForm, SWT.BORDER);
		this.executionTracesForm.setWeights(new int[] { 2, 1 });

		this.sashForm = new SashForm(this.mainComposite, SWT.VERTICAL);

		this.aggregatedTracesTree = new Tree(this.sashForm, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		this.aggregatedTracesTree.setHeaderVisible(true);

		this.treeColumn = new TreeColumn(this.aggregatedTracesTree, SWT.NONE);
		this.treeColumn.setWidth(100);
		this.treeColumn.setText("Execution Container");

		this.treeColumn_1 = new TreeColumn(this.aggregatedTracesTree, SWT.NONE);
		this.treeColumn_1.setWidth(100);
		this.treeColumn_1.setText("Component");

		this.treeColumn_2 = new TreeColumn(this.aggregatedTracesTree, SWT.NONE);
		this.treeColumn_2.setWidth(100);
		this.treeColumn_2.setText("Operation");

		this.trclmnCalls = new TreeColumn(this.aggregatedTracesTree, SWT.NONE);
		this.trclmnCalls.setWidth(100);
		this.trclmnCalls.setText("# Calls");

		this.aggregatedTraceDetailComposite = new AggregatedTraceDetailComposite(this.sashForm, SWT.BORDER);
		this.sashForm.setWeights(new int[] { 2, 1 });

		this.outerForm.setWeights(new int[] { 2, 4 });

		this.menuBar = new Menu(this.shell, SWT.BAR);
		this.shell.setMenuBar(this.menuBar);

		this.fileMenuItem = new MenuItem(this.menuBar, SWT.CASCADE);
		this.fileMenuItem.setText("File");

		this.fileMenu = new Menu(this.fileMenuItem);
		this.fileMenuItem.setMenu(this.fileMenu);

		this.openMonitoringLogMenuItem = new MenuItem(this.fileMenu, SWT.NONE);
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
		this.mntmShortOperationParameters.setText("Short Operation Parameters");

		this.mntmLongOperationParameters = new MenuItem(this.menu, SWT.RADIO);
		this.mntmLongOperationParameters.setText("Long Operation Parameters");

		new MenuItem(this.menu, SWT.SEPARATOR);

		this.mntmShortComponentNames = new MenuItem(this.menu, SWT.RADIO);
		this.mntmShortComponentNames.setText("Short Component Names");

		this.mntmLongComponentNames = new MenuItem(this.menu, SWT.RADIO);
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

	private void addLogic() {
		DataSource.getInstance().addObserver(new Observer() {

			@Override
			public void update(final Observable o, final Object arg) {
				MainWindow.this.reloadFromModel();
			}

		});

		this.openMonitoringLogMenuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				MainWindow.this.showOpenMonitoringLogDialog();
			}

		});

		this.mntmShortOperationParameters.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortOperationParameters(true);
			}

		});

		this.mntmLongOperationParameters.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortOperationParameters(false);
			}

		});

		this.mntmShortComponentNames.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortComponentNames(true);
			}

		});

		this.mntmLongComponentNames.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Properties.getInstance().setShortComponentNames(false);
			}

		});

		Properties.getInstance().addObserver(new Observer() {

			@Override
			public void update(final Observable o, final Object arg) {
				MainWindow.this.aggregatedTracesTree.clearAll(true);
				MainWindow.this.tracesTree.clearAll(true);
			}

		});

		this.recordsTable.addListener(SWT.SetData, new RecordsTableSetDataListener());
		this.aggregatedTracesTree.addListener(SWT.SetData, new AggregatedExecutionTracesTreeSetDataListener());
		this.tracesTree.addListener(SWT.SetData, new ExecutionTracesTreeSetDataListener());

		this.recordsTableTimestampColumn.addListener(SWT.Selection, new TableColumnSortListener<RecordEntry>(new RecordEntryTimestampComparator()));
		this.recordsTableTypeColumn.addListener(SWT.Selection, new TableColumnSortListener<RecordEntry>(new RecordEntryTypeComparator()));
		this.recordsTableRecordColumn.addListener(SWT.Selection, new TableColumnSortListener<RecordEntry>(new RecordEntryTimestampComparator()));

		this.treeColumn_11.addListener(SWT.Selection, new TreeColumnSortListener(new AbstractDirectedComparator<ExecutionEntry>() {

			@Override
			public int compare(final ExecutionEntry arg0, final ExecutionEntry arg1) {
				int result = Long.compare(arg0.getDuration(), arg1.getDuration());
				if (this.getDirection() == SWT.UP) {
					result = -result;
				}
				return result;

			}
		}));

		this.explorerTree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Widget selectedWidget = e.item;
				MainWindow.this.handleExplorerSelection(selectedWidget);
			}
		});

		this.tracesTree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object data = e.item.getData();
				if (data instanceof ExecutionEntry) {
					MainWindow.this.traceDetailComposite.setTraceToDisplay((ExecutionEntry) data);
				}
			}

		});

		this.aggregatedTracesTree.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Object data = e.item.getData();
				if (data instanceof AggregatedExecutionEntry) {
					MainWindow.this.aggregatedTraceDetailComposite.setTraceToDisplay((AggregatedExecutionEntry) data);
				}
			}

		});
	}

	private void showAggregatedExecutionTraces() {
		// Show the tree
		this.setVisibleMainComponent(this.sashForm);

		// Resize the columns
		for (final TreeColumn column : this.aggregatedTracesTree.getColumns()) {
			column.pack();
		}
	}

	private void showExecutionTraces() {
		// Show the tree
		this.setVisibleMainComponent(this.executionTracesForm);

		// Resize the columns
		for (final TreeColumn column : this.tracesTree.getColumns()) {
			column.pack();
		}
	}

	private void showRecords() {
		// Show the table
		this.setVisibleMainComponent(this.recordsTable);

		// Resize the columns
		for (final TableColumn column : this.recordsTable.getColumns()) {
			column.pack();
		}
	}

	private void handleExplorerSelection(final Widget selectedWidget) {
		if (MainWindow.this.recordsTreeItem == selectedWidget) {
			MainWindow.this.showRecords();
		} else if (MainWindow.this.executionTracesTreeItem == selectedWidget) {
			MainWindow.this.showExecutionTraces();
		} else if (MainWindow.this.trtmAggregatedExecutionTraces == selectedWidget) {
			MainWindow.this.showAggregatedExecutionTraces();
		} else {
			MainWindow.this.setVisibleMainComponent(null);
			MainWindow.this.lblNa.setText("");
		}
	}

	private void reloadFromModel() {
		// Reload the data from the model...
		final List<RecordEntry> records = DataSource.getInstance().getRecords();
		final List<ExecutionEntry> traces = DataSource.getInstance().getTraces();
		final List<AggregatedExecutionEntry> aggregatedTraces = DataSource.getInstance().getAggregatedTrace();

		// ...and write it into the corresponding widgets
		MainWindow.this.recordsTable.setItemCount(records.size());
		MainWindow.this.recordsTable.setData(records);

		MainWindow.this.tracesTree.setItemCount(traces.size());
		MainWindow.this.tracesTree.setData(traces);

		MainWindow.this.aggregatedTracesTree.setItemCount(aggregatedTraces.size());
		MainWindow.this.aggregatedTracesTree.setData(aggregatedTraces);
	}

	private void showOpenMonitoringLogDialog() {
		final DirectoryDialog dialog = new DirectoryDialog(this.shell);
		final String selectedDirectory = dialog.open();

		if (null != selectedDirectory) {
			DataSource.getInstance().loadMonitoringLogFromFS(selectedDirectory);
		}
	}

	private void setVisibleMainComponent(final Control component) {
		final StackLayout layout = (StackLayout) this.mainComposite.getLayout();
		layout.topControl = component;

		this.mainComposite.layout();
	}

}
