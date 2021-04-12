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

package kieker.gui.subview.traces;

import java.util.List;

import kieker.gui.common.IModel;
import kieker.gui.common.domain.Execution;
import kieker.gui.common.model.DataModel;
import kieker.gui.common.model.PropertiesModel;
import kieker.gui.subview.util.AbstractDataModelProxy;

/**
 * The sub-controller responsible for the sub-view presenting the available failed traces.
 *
 * @author Nils Christian Ehmke
 */
public final class FailedController extends AbstractController {

	public FailedController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		super(dataModel, propertiesModel);
	}

	@Override
	protected IModel<Execution> createModelProxy(final DataModel dataModel) {
		return new ModelProxy(dataModel);
	}

	private final class ModelProxy extends AbstractDataModelProxy<Execution> {

		private ModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Execution> getContent() {
			return super.dataModel.getFailedTracesCopy();
		}

	}

}
