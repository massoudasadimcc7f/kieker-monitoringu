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

package kieker.diagnosis.subview.aggregatedtraces;

import java.util.List;

import kieker.diagnosis.common.domain.AggregatedOperationCall;
import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.common.model.DataModel;
import kieker.diagnosis.common.model.PropertiesModel;
import kieker.diagnosis.subview.Filter;
import kieker.diagnosis.subview.ISubController;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.util.AbstractDataModelProxy;
import kieker.diagnosis.subview.util.IModel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public final class Controller implements ISubController, SelectionListener {

	private final ISubView view;
	private final Model model;

	public Controller(final Filter filter, final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<AggregatedTrace> modelProxy = Controller.createModelProxy(dataModel, filter);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof AggregatedOperationCall) {
			this.model.setCurrentActiveCall((AggregatedOperationCall) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Just implemented for the interface
	}

	private static IModel<AggregatedTrace> createModelProxy(final DataModel dataModel, final Filter filter) {
		if (filter == Filter.JUST_FAILED) {
			return new FailedTracesModelProxy(dataModel);
		}
		if (filter == Filter.JUST_FAILURE_CONTAINING) {
			return new FailureContainingTracesModelProxy(dataModel);
		}
		return new TracesModelProxy(dataModel);
	}

	private static final class TracesModelProxy extends AbstractDataModelProxy<AggregatedTrace> {

		public TracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<AggregatedTrace> getContent() {
			return super.getDataModel().getAggregatedTracesCopy();
		}

	}

	private static final class FailedTracesModelProxy extends AbstractDataModelProxy<AggregatedTrace> {

		public FailedTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<AggregatedTrace> getContent() {
			return super.getDataModel().getFailedAggregatedTracesCopy();
		}

	}

	private static final class FailureContainingTracesModelProxy extends AbstractDataModelProxy<AggregatedTrace> {

		public FailureContainingTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<AggregatedTrace> getContent() {
			return super.getDataModel().getFailureContainingAggregatedTracesCopy();
		}

	}

}