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

package kieker.diagnosis.controller.traces;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import kieker.diagnosis.components.LazyOperationCallTreeItem;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;
import kieker.diagnosis.util.FilterUtility;
import kieker.diagnosis.util.NameConverter;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewController extends AbstractController {

	private final DataModel dataModel = DataModel.getInstance();

	private final SimpleObjectProperty<Optional<OperationCall>> selection = new SimpleObjectProperty<>(Optional.empty());

	@FXML private TreeTableView<OperationCall> treetable;
	@FXML private TextField filterContainer;
	@FXML private TextField filterComponent;
	@FXML private TextField filterOperation;
	@FXML private TextField filterTraceID;

	@FXML private TextField traceDepth;
	@FXML private TextField traceSize;
	@FXML private TextField timestamp;
	@FXML private TextField container;
	@FXML private TextField component;
	@FXML private TextField operation;
	@FXML private TextField duration;
	@FXML private TextField percent;
	@FXML private TextField traceID;
	@FXML private TextField failed;

	@FXML private TextField counter;

	@FXML private ResourceBundle resources;

	private Predicate<OperationCall> fstPredicate = call -> true;
	private Predicate<OperationCall> sndPredicate = call -> true;
	private Predicate<OperationCall> thdPredicate = call -> true;
	private Predicate<OperationCall> fthPredicate = call -> true;
	private Predicate<OperationCall> fifPredicate = call -> true;

	public TracesViewController(final Context context) {
		super(context);
	}

	@ErrorHandling
	public void initialize() {
		this.reloadTreetable();

		final ObservableList<Trace> traces = this.dataModel.getTraces();
		traces.addListener((final Change<? extends Trace> c) -> this.reloadTreetable());

		this.selection.addListener(e -> this.updateDetailPanel());

		final Object call = super.getContext().get(ContextKey.OPERATION_CALL);
		if (call instanceof OperationCall) {
			jumpToCall((OperationCall) call);
		}

	}

	private void jumpToCall(final OperationCall call) {
		final TreeItem<OperationCall> root = this.treetable.getRoot();

		final Optional<TreeItem<OperationCall>> traceRoot = findTraceRoot(root, call);
		if (traceRoot.isPresent()) {
			final TreeItem<OperationCall> treeItem = findCall(traceRoot.get(), call);
			if (treeItem != null) {
				this.treetable.getSelectionModel().select(treeItem);
				this.selection.set(Optional.ofNullable(treeItem.getValue()));
			}
		}
	}

	private Optional<TreeItem<OperationCall>> findTraceRoot(final TreeItem<OperationCall> root, final OperationCall call) {
		return root.getChildren().stream().filter(t -> t.getValue().getTraceID() == call.getTraceID()).findFirst();
	}

	private TreeItem<OperationCall> findCall(final TreeItem<OperationCall> root, final OperationCall call) {
		 if (root.getValue() == call) {
			 root.setExpanded(true);
			 return root;
		 }
		 
		 for (final TreeItem<OperationCall> child : root.getChildren()) {
			final TreeItem<OperationCall> item = findCall(child, call);
			if (item != null) {
				root.setExpanded(true);
				return item;
			}
		}
		 
		 return null;
	}

	private void updateDetailPanel() {
		if (this.selection.get().isPresent()) {
			final OperationCall call = this.selection.get().get();
			final TimeUnit sourceTimeUnit = DataModel.getInstance().getTimeUnit();
			final TimeUnit targetTimeUnit = PropertiesModel.getInstance().getTimeUnit();

			this.container.setText(call.getContainer());
			this.component.setText(call.getComponent());
			this.operation.setText(call.getOperation());
			this.timestamp.setText(Long.toString(call.getTimestamp()));
			this.duration.setText(NameConverter.toDurationString(call.getDuration(), sourceTimeUnit, targetTimeUnit));
			this.traceID.setText(Long.toString(call.getTraceID()));
			this.traceDepth.setText(Integer.toString(call.getStackDepth()));
			this.traceSize.setText(Integer.toString(call.getStackSize()));
			this.percent.setText(call.getPercent() + " %");
			this.failed.setText(call.getFailedCause() != null ? call.getFailedCause() : "N/A");
		} else {
			this.container.setText("N/A");
			this.component.setText("N/A");
			this.operation.setText("N/A");
			this.timestamp.setText("N/A");
			this.duration.setText("N/A");
			this.traceID.setText("N/A");
			this.percent.setText("N/A");
			this.failed.setText("N/A");
		}
	}

	@ErrorHandling
	public void selectCall(final MouseEvent event) {
		final TreeItem<OperationCall> selectedItem = this.treetable.getSelectionModel().getSelectedItem();
		if (selectedItem != null) {
			this.selection.set(Optional.ofNullable(selectedItem.getValue()));
		}
	}

	@ErrorHandling
	public void showAllTraces() {
		this.fstPredicate = call -> true;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void showJustFailedTraces() {
		this.fstPredicate = OperationCall::isFailed;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void showJustFailureContainingTraces() {
		this.fstPredicate = OperationCall::containsFailure;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useContainerFilter() {
		final Predicate<OperationCall> predicate = FilterUtility.useFilter(this.filterContainer, OperationCall::getContainer);
		this.sndPredicate = predicate;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useComponentFilter() {
		final Predicate<OperationCall> predicate = FilterUtility.useFilter(this.filterComponent, OperationCall::getComponent);
		this.thdPredicate = predicate;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useOperationFilter() {
		final Predicate<OperationCall> predicate = FilterUtility.useFilter(this.filterOperation, OperationCall::getOperation);
		this.fthPredicate = predicate;
		this.reloadTreetable();
	}

	@ErrorHandling
	public void useTraceIDFilter() {
		final Function<OperationCall, String> function = (call -> Long.toString(call.getTraceID()));
		final Predicate<OperationCall> predicate = FilterUtility.useFilter(this.filterTraceID, function);
		this.fifPredicate = predicate;
		this.reloadTreetable();
	}

	private void reloadTreetable() {
		this.selection.set(Optional.empty());

		final List<Trace> traces = this.dataModel.getTraces();
		final TreeItem<OperationCall> root = new TreeItem<>();
		final ObservableList<TreeItem<OperationCall>> rootChildren = root.getChildren();
		this.treetable.setRoot(root);
		this.treetable.setShowRoot(false);

		traces.stream().map(trace -> trace.getRootOperationCall()).filter(this.fstPredicate).filter(this.sndPredicate).filter(this.thdPredicate).filter(this.fthPredicate)
				.filter(this.fifPredicate).forEach(call -> rootChildren.add(new LazyOperationCallTreeItem(call)));

		this.counter.textProperty().set(rootChildren.size() + " " + this.resources.getString("TracesView.lblCounter.text"));
	}
}