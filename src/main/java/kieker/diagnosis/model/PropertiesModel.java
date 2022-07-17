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

package kieker.diagnosis.model;

import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/**
 * @author Nils Christian Ehmke
 */
public final class PropertiesModel extends Observable {

	private static final String KEY_TIMEUNIT = "timeunit";
	private static final String KEY_OPERATIONS = "operations";
	private static final String KEY_COMPONENTS = "components";
	private static final String KEY_GRAPHVIZ_PATH = "graphvizpath";
	private static final String KEY_MAX_TRACES = "maxTraces";

	private static final Logger LOGGER = Logger.getGlobal();

	private boolean commit = true;

	private String graphvizPath;
	private TimeUnit timeUnit;
	private ComponentNames componentNames;
	private OperationNames operationNames;
	private final SimpleObjectProperty<Integer> maxTracesToShow = new SimpleObjectProperty<Integer>();

	public PropertiesModel() {
		this.loadSettings();
	}

	private void loadSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		this.graphvizPath = preferences.get(PropertiesModel.KEY_GRAPHVIZ_PATH, ".");
		this.timeUnit = TimeUnit.valueOf(preferences.get(PropertiesModel.KEY_TIMEUNIT, TimeUnit.NANOSECONDS.name()));
		this.componentNames = ComponentNames.valueOf(preferences.get(PropertiesModel.KEY_COMPONENTS, ComponentNames.LONG.name()));
		this.operationNames = OperationNames.valueOf(preferences.get(PropertiesModel.KEY_OPERATIONS, OperationNames.SHORT.name()));
		this.maxTracesToShow.setValue(Integer.parseInt(preferences.get(PropertiesModel.KEY_MAX_TRACES, "1000000")));
	}

	private void saveSettings() {
		final Preferences preferences = Preferences.userNodeForPackage(PropertiesModel.class);

		preferences.put(PropertiesModel.KEY_GRAPHVIZ_PATH, this.graphvizPath);
		preferences.put(PropertiesModel.KEY_TIMEUNIT, this.timeUnit.name());
		preferences.put(PropertiesModel.KEY_COMPONENTS, this.componentNames.name());
		preferences.put(PropertiesModel.KEY_OPERATIONS, this.operationNames.name());
		preferences.put(PropertiesModel.KEY_MAX_TRACES, this.maxTracesToShow.getValue().toString());

		try {
			preferences.flush();
		} catch (final BackingStoreException e) {
			PropertiesModel.LOGGER.warning(e.getLocalizedMessage());
		}
	}

	public String getGraphvizPath() {
		return this.graphvizPath;
	}

	public void setGraphvizPath(final String graphvizPath) {
		this.graphvizPath = graphvizPath;

		this.notifyObserversAndSaveSettings();
	}

	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	public void setTimeUnit(final TimeUnit timeUnit) {
		this.timeUnit = timeUnit;

		this.notifyObserversAndSaveSettings();
	}

	public ComponentNames getComponentNames() {
		return this.componentNames;
	}

	public void setComponentNames(final ComponentNames componentNames) {
		this.componentNames = componentNames;

		this.notifyObserversAndSaveSettings();
	}

	public OperationNames getOperationNames() {
		return this.operationNames;
	}

	public void setOperationNames(final OperationNames operationNames) {
		this.operationNames = operationNames;

		this.notifyObserversAndSaveSettings();
	}

	public ObservableValue<Integer> getMaxTracesToShow() {
		return this.maxTracesToShow;
	}

	public void setMaxTracesToShow(final int maxTracesToShow) {
		this.maxTracesToShow.set(maxTracesToShow);
	}

	public void startModification() {
		this.commit = false;
	}

	public void commitModification() {
		this.commit = true;

		this.notifyObserversAndSaveSettings();
	}

	private void notifyObserversAndSaveSettings() {
		if (this.commit) {
			this.setChanged();
			this.notifyObservers();
			this.saveSettings();
		}
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	public enum ComponentNames {
		SHORT, LONG
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	public enum OperationNames {
		SHORT, LONG
	}

	public static PropertiesModel getInstance() {
		// To be removed
		return new PropertiesModel();
	}

}
