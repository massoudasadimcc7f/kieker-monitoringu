/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.methods.atom;

import com.google.inject.Inject;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.MethodAppearance;
import kieker.diagnosis.backend.settings.properties.MethodAppearanceProperty;
import kieker.diagnosis.frontend.base.mixin.CdiMixin;

/**
 * This is a cell factory for a table which shows the method of a method call in the configured manner.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodCellValueFactory implements Callback<CellDataFeatures<MethodCall, String>, ObservableValue<String>>, CdiMixin {

	@Inject
	private PropertiesService ivPropertiesService;

	public MethodCellValueFactory( ) {
		injectFields( );
	}

	@Override
	public ObservableValue<String> call( final CellDataFeatures<MethodCall, String> aParam ) {
		final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( methodAppearance.convert( aParam.getValue( ).getMethod( ) ) );
	}

}
