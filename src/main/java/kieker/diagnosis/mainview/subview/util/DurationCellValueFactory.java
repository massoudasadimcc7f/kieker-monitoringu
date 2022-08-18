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

package kieker.diagnosis.mainview.subview.util;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

/**
 * @author Nils Christian Ehmke
 */
public class DurationCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<Long>> {

	private final DataModel dataModel = DataModel.getInstance();
	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();

	private final String property;

	public DurationCellValueFactory(@NamedArg(value = "property") final String property) {
		this.property = property.substring(0, 1).toUpperCase() + property.substring(1);
	}

	@Override
	public ObservableValue<Long> call(final CellDataFeatures<?, String> call) {
		try {
			final TimeUnit srcTimeUnit = this.dataModel.getTimeUnit();
			final TimeUnit dstTimeUnit = this.propertiesModel.getTimeUnit();

			final Method getter = call.getValue().getClass().getMethod("get" + this.property, new Class<?>[0]);
			final long duration = (long) getter.invoke(call.getValue(), new Object[0]);

			final long newDuration = dstTimeUnit.convert(duration, srcTimeUnit);
			return new ReadOnlyObjectWrapper<Long>(newDuration);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
