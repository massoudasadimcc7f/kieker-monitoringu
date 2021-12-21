/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.mainview;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.annotation.PostConstruct;

import kieker.diagnosis.mainview.dialog.SettingsDialog;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.aggregatedcalls.AggregatedCallsViewController;
import kieker.diagnosis.subview.aggregatedtraces.AggregatedTracesViewController;
import kieker.diagnosis.subview.calls.CallsViewController;
import kieker.diagnosis.subview.traces.TracesViewController;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The main controller of this application. It is responsible for creating top level models, further sub-controllers, and for creating and controlling the
 * application's main view. The sub-views and their corresponding models are created by the sub-controllers.
 * 
 * @author Nils Christian Ehmke
 */
@Component
public final class Controller implements SelectionListener {

	private static final Logger LOGGER = Logger.getGlobal();

	@Autowired
	private DataModel dataModel;

	@Autowired
	private AggregatedTracesViewController aggregatedTracesViewController;

	@Autowired
	private CallsViewController callsViewController;

	@Autowired
	private TracesViewController tracesViewController;

	@Autowired
	private AggregatedCallsViewController aggregatedCallsViewController;

	@Autowired
	private View mainView;

	@Autowired
	private Model mainViewModel;

	private Map<String, ISubView> subViews;

	@PostConstruct
	public void initialize() {
		this.subViews = new HashMap<>();
		this.subViews.put(SubView.AGGREGATED_TRACES_SUB_VIEW.name(), this.aggregatedTracesViewController.getView());
		this.subViews.put(SubView.TRACES_SUB_VIEW.name(), this.tracesViewController.getView());
		this.subViews.put(SubView.AGGREGATED_OPERATION_CALLS_SUB_VIEW.name(), this.aggregatedCallsViewController.getView());
		this.subViews.put(SubView.OPERATION_CALLS_SUB_VIEW.name(), this.callsViewController.getView());
	}

	public void showView() {
		this.mainView.show();
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		this.handlePotentialTreeSelection(e);
		this.handlePotentialMenuSelection(e);
	}

	private void handlePotentialMenuSelection(final SelectionEvent e) {
		if (e.widget == this.mainView.getMntmOpenMonitoringLog()) {
			final Preferences preferences = Preferences.userNodeForPackage(Controller.class);
			final String filterPath = preferences.get("lastimportpath", ".");

			this.mainView.getDirectoryDialog().setFilterPath(filterPath);
			final String selectedDirectory = this.mainView.getDirectoryDialog().open();

			if (null != selectedDirectory) {
				this.mainViewModel.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_WAIT));
				this.dataModel.loadMonitoringLogFromFS(selectedDirectory);
				this.mainViewModel.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_ARROW));

				preferences.put("lastimportpath", selectedDirectory);
				try {
					preferences.flush();
				} catch (final BackingStoreException ex) {
					Controller.LOGGER.warning(ex.getLocalizedMessage());
				}
			}
		}

		if (e.widget == this.mainView.getMntmSettings()) {
			final SettingsDialog settingsDialog = this.mainView.getSettingsDialog();
			settingsDialog.open();
		}

		if (e.widget == this.mainView.getMntmExit()) {
			this.mainView.close();
		}

		if (e.widget == this.mainView.getMntmAbout()) {
			this.mainView.getAboutDialog().open();
		}
	}

	private void handlePotentialTreeSelection(final SelectionEvent e) { // NOPMD (this method violates some metrics. This is acceptable, as it is readable)
		if (e.item == this.mainView.getTrtmExplorer()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.NONE.name());
		}
		if (e.item == this.mainView.getTrtmTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmAggregatedTraces()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.AGGREGATED_TRACES_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmAggregatedOperationCalls()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.AGGREGATED_OPERATION_CALLS_SUB_VIEW.name());
		}
		if (e.item == this.mainView.getTrtmOperationCalls()) {
			this.mainViewModel.setCurrentActiveSubView(SubView.OPERATION_CALLS_SUB_VIEW.name());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Nothing to do here. This method is just required by the interface.
	}

	public Map<String, ISubView> getSubViews() {
		return this.subViews;
	}

	public enum SubView {
		TRACES_SUB_VIEW, AGGREGATED_TRACES_SUB_VIEW, NONE, AGGREGATED_OPERATION_CALLS_SUB_VIEW, OPERATION_CALLS_SUB_VIEW,
	}

}
