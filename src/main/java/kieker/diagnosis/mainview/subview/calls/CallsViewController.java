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

package kieker.diagnosis.mainview.subview.calls;

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.mainview.subview.util.NameConverter;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class CallsViewController {

	private final SimpleObjectProperty<Optional<OperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	private FilteredList<OperationCall> fstFilteredData;
	private FilteredList<OperationCall> sndFilteredData;
	private FilteredList<OperationCall> thdFilteredData;
	private FilteredList<OperationCall> fthFilteredData;
	private FilteredList<OperationCall> fifFilteredData;

	@FXML private TableView<OperationCall> table;
	@FXML private TextField filterContainer;
	@FXML private TextField filterComponent;
	@FXML private TextField filterOperation;
	@FXML private TextField filterTraceID;

	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField timestamp;
	@FXML private TextField duration;
	@FXML private TextField traceID;
	@FXML private TextField failed;

	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	public void initialize() {
		final DataModel dataModel = DataModel.getInstance();

		this.fstFilteredData = new FilteredList<>(dataModel.getOperationCalls());
		this.sndFilteredData = new FilteredList<>(this.fstFilteredData);
		this.thdFilteredData = new FilteredList<>(this.sndFilteredData);
		this.fthFilteredData = new FilteredList<>(this.thdFilteredData);
		this.fifFilteredData = new FilteredList<>(this.fthFilteredData);

		this.fifFilteredData.addListener((ListChangeListener<OperationCall>) change -> this.selection.set(Optional.empty()));

		final SortedList<OperationCall> sortedData = new SortedList<>(this.fifFilteredData);
		sortedData.comparatorProperty().bind(this.table.comparatorProperty());
		this.table.setItems(sortedData);

		this.container.textProperty().bind(this.createStringBindingForSelection(OperationCall::getContainer));
		this.component.textProperty().bind(this.createStringBindingForSelection(OperationCall::getComponent));
		this.operation.textProperty().bind(this.createStringBindingForSelection(OperationCall::getOperation));
		this.timestamp.textProperty().bind(this.createStringBindingForSelection(OperationCall::getTimestamp));
		this.duration.textProperty().bind(this.createDurationStringBindingForSelection(OperationCall::getDuration));
		this.traceID.textProperty().bind(this.createStringBindingForSelection(OperationCall::getTraceID));
		this.failed.textProperty().bind(this.createStringBindingForSelection(OperationCall::getFailedCause));

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> sortedData.size() + " " + this.resources.getString("CallsView.lbCounter.text"), sortedData));
	}

	private StringBinding createStringBindingForSelection(final Function<OperationCall, Object> mapper) {
		return Bindings.createStringBinding(() -> this.selection.get().map(mapper).map(Object::toString).orElse("N/A"), this.selection);
	}

	private StringBinding createDurationStringBindingForSelection(final Function<OperationCall, Object> mapper) {
		return Bindings.createStringBinding(
				() -> this.selection.get().map(mapper).map(x -> x.toString() + " " + NameConverter.toShortTimeUnit(DataModel.getInstance().getTimeUnit())).orElse("N/A"),
				this.selection);
	}

	public void selectCall(final MouseEvent event) {
		this.selection.set(Optional.ofNullable(this.table.getSelectionModel().getSelectedItem()));
	}

	public void showAllMethods() {
		this.fstFilteredData.setPredicate(null);
	}

	public void showJustFailedMethods() {
		this.fstFilteredData.setPredicate(OperationCall::isFailed);
	}

	public void useContainerFilter() {
		final String text = this.filterContainer.getText();

		if ((text == null) || text.isEmpty()) {
			this.sndFilteredData.setPredicate(null);
		} else {
			this.sndFilteredData.setPredicate(call -> call.getContainer().toLowerCase().contains(text.toLowerCase()));
		}
	}

	public void useComponentFilter() {
		final String text = this.filterComponent.getText();

		if ((text == null) || text.isEmpty()) {
			this.thdFilteredData.setPredicate(null);
		} else {
			this.thdFilteredData.setPredicate(call -> call.getComponent().toLowerCase().contains(text.toLowerCase()));
		}
	}

	public void useOperationFilter() {
		final String text = this.filterOperation.getText();

		if ((text == null) || text.isEmpty()) {
			this.fthFilteredData.setPredicate(null);
		} else {
			this.fthFilteredData.setPredicate(call -> call.getOperation().toLowerCase().contains(text.toLowerCase()));
		}
	}

	public void useTraceIDFilter() {
		final String text = this.filterTraceID.getText();

		if ((text == null) || text.isEmpty()) {
			this.fifFilteredData.setPredicate(null);
		} else {
			this.fifFilteredData.setPredicate(call -> Long.toString(call.getTraceID()).contains(text));
		}
	}

}
