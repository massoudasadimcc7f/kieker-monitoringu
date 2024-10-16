/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.aggregatedmethods.components;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.settings.properties.TimeUnitProperty;

/**
 * This is a cell factory for a table which shows the duration of a method call in the configured manner. It has to be in the CDI context, as it has to have
 * access to the application properties.
 *
 * @author Nils Christian Ehmke
 */
public class DurationCellValueFactory implements Callback<CellDataFeatures<AggregatedMethodCall, Long>, ObservableValue<Long>> {

	@Inject
	private PropertiesService ivPropertiesService;

	private Function<AggregatedMethodCall, Long> ivGetter;

	public void setGetter( final Function<AggregatedMethodCall, Long> aGetter ) {
		ivGetter = aGetter;
	}

	@Override
	public ObservableValue<Long> call( final CellDataFeatures<AggregatedMethodCall, Long> aParam ) {
		final TimeUnit timeUnit = ivPropertiesService.loadApplicationProperty( TimeUnitProperty.class );
		return new ReadOnlyObjectWrapper<>( timeUnit.convert( ivGetter.apply( aParam.getValue( ) ), TimeUnit.NANOSECONDS ) );
	}

}
