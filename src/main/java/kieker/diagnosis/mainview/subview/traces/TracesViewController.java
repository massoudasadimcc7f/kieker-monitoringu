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

package kieker.diagnosis.mainview.subview.traces;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.mainview.subview.util.LazyOperationCallTreeItem;
import kieker.diagnosis.model.DataModel;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewController {

	private final DataModel dataModel = DataModel.getInstance();

	private final SimpleObjectProperty<Optional<OperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<OperationCall> treetable;
	@FXML private TextField counter;

	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField failed;
	@FXML private TextField traceDepth;
	@FXML private TextField traceSize;
	@FXML private TextField duration;
	@FXML private TextField percent;
	@FXML private TextField traceID;
	@FXML private TextField timestamp;

	@FXML private ResourceBundle resources;

	public void initialize() {
		this.reloadTreetable();

		final ObservableList<Trace> traces = this.dataModel.getTraces();

		traces.addListener((final Change<? extends Trace> c) -> this.reloadTreetable());

		this.counter.textProperty().bind(Bindings.createStringBinding(() -> traces.size() + " " + this.resources.getString("TracesView.lblCounter.text"), traces));

		this.container.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getContainer()).orElse("N/A"), this.selection));
		this.component.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getComponent()).orElse("N/A"), this.selection));
		this.operation.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getOperation()).orElse("N/A"), this.selection));
		this.failed.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> call.getFailedCause()).orElse("N/A"), this.selection));
		this.traceDepth.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Integer.toString(call.getStackDepth())).orElse("N/A"), this.selection));
		this.traceSize.textProperty().bind(
				Bindings.createStringBinding(() -> this.selection.get().map(call -> Integer.toString(call.getStackSize())).orElse("N/A"), this.selection));
		this.duration.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getDuration())).orElse("N/A"), this.selection));
		this.percent.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> Float.toString(call.getPercent())).orElse("N/A"), this.selection));
		this.traceID.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getTraceID())).orElse("N/A"), this.selection));
		this.timestamp.textProperty().bind(Bindings.createStringBinding(() -> this.selection.get().map(call -> Long.toString(call.getTimestamp())).orElse("N/A"), this.selection));
	}

	public void selectCall(final MouseEvent event) {
		this.selection.set(Optional.of(this.treetable.getSelectionModel().getSelectedItem().getValue()));
	}

	private void reloadTreetable() {
		final List<Trace> traces = this.dataModel.getTraces();
		final TreeItem<OperationCall> root = new TreeItem<>();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		for (final Trace trace : traces) {
			root.getChildren().add(new LazyOperationCallTreeItem<OperationCall>(trace.getRootOperationCall()));
		}
	}
}
