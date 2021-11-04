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

package kieker.diagnosis.subview.calls;

import java.util.List;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
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
		final IModel<OperationCall> modelProxy = Controller.createModelProxy(dataModel, filter);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof OperationCall) {
			this.model.setCurrentActiveCall((OperationCall) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Just implemented for the interface
	}

	private static IModel<OperationCall> createModelProxy(final DataModel dataModel, final Filter filter) {
		if (filter == Filter.JUST_FAILED) {
			return new FailedOperationCallsModelProxy(dataModel);
		} else {
			return new OperationCallsModelProxy(dataModel);
		}
	}

	private static final class FailedOperationCallsModelProxy extends AbstractDataModelProxy<OperationCall> {

		public FailedOperationCallsModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<OperationCall> getContent() {
			return super.getDataModel().getFailedOperationCalls();
		}

	}

	private static final class OperationCallsModelProxy extends AbstractDataModelProxy<OperationCall> {

		public OperationCallsModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<OperationCall> getContent() {
			return super.getDataModel().getOperationCalls();
		}

	}

}
