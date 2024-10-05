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

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.settings.MethodAppearance;
import kieker.diagnosis.service.settings.properties.MethodAppearanceProperty;

@Singleton
public class MethodCellValueFactory implements Callback<CellDataFeatures<AggregatedMethodCall, String>, ObservableValue<String>> {

	@Inject
	private PropertiesService ivPropertiesService;

	@Override
	public ObservableValue<String> call( final CellDataFeatures<AggregatedMethodCall, String> aParam ) {
		final MethodAppearance methodAppearance = ivPropertiesService.loadApplicationProperty( MethodAppearanceProperty.class );
		return new ReadOnlyObjectWrapper<>( methodAppearance.convert( aParam.getValue( ).getMethod( ) ) );
	}

}
