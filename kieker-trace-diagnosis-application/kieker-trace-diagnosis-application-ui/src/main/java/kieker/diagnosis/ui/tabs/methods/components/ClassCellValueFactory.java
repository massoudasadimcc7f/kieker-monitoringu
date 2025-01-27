/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.tabs.methods.components;

import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.ServiceFactory;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.settings.ClassAppearance;
import kieker.diagnosis.service.settings.properties.ClassAppearanceProperty;

/**
 * This is a cell factory for a table which shows the class of a method call in the configured manner. It has to be in the CDI context, as it has to have access
 * to the application properties.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class ClassCellValueFactory implements Callback<CellDataFeatures<MethodCall, String>, ObservableValue<String>> {

	private final PropertiesService ivPropertiesService = ServiceFactory.getService( PropertiesService.class );

	@Override
	public ObservableValue<String> call( final CellDataFeatures<MethodCall, String> aParam ) {
		final ClassAppearance classAppearance = ivPropertiesService.loadApplicationProperty( ClassAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( classAppearance.convert( aParam.getValue( ).getClazz( ) ) );
	}

}
