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

package kieker.diagnosis.frontend.tab.methods;

import java.text.NumberFormat;
import java.util.List;

import com.google.inject.Singleton;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.pattern.PatternService;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.frontend.base.ui.ViewModelBase;

/**
 * The view model of the methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class MethodsViewModel extends ViewModelBase<MethodsView> {

	public void updatePresentationMethods( final List<MethodCall> aMethods ) {
		getView( ).getMethods( ).setItems( FXCollections.observableList( aMethods ) );
		getView( ).getMethods( ).refresh( );
	}

	public void updatePresentationDetails( final MethodCall methodCall ) {
		getView( ).getDetails( ).setValue( methodCall );
	}

	public void updatePresentationDurationColumnHeader( final String aSuffix ) {
		getView( ).getDurationColumn( ).setText( getLocalizedString( "columnDuration" ) + " " + aSuffix );
	}

	public void updatePresentationStatus( final int aMethods, final int aTotalMethods ) {
		final NumberFormat decimalFormat = NumberFormat.getInstance( );
		getView( ).getStatus( ).setValue( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aMethods ), decimalFormat.format( aTotalMethods ) ) );
	}

	public void updatePresentationFilter( final MethodsFilter filter ) {
		getView( ).getFilter( ).setValue( filter );
	}

	public MethodsFilter savePresentationFilter( ) throws BusinessException {
		final MethodsFilter filter = getView( ).getFilter( ).getValue( );

		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = getService( PatternService.class );

			if ( !( patternService.isValidPattern( filter.getHost( ) ) || filter.getHost( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getHost( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getClazz( ) ) || filter.getClazz( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getClazz( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getMethod( ) ) || filter.getMethod( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getMethod( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getException( ) ) || filter.getException( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getException( ) ) );
			}
		}

		return filter;
	}

	public MethodCall getSelected( ) {
		return getView( ).getMethods( ).getSelectionModel( ).getSelectedItem( );
	}

	public CSVData savePresentationAsCSV( ) {
		final TableView<MethodCall> tableView = getView( ).getMethods( );
		final ObservableList<TableColumn<MethodCall, ?>> visibleColumns = tableView.getVisibleLeafColumns( );

		final int columnSize = visibleColumns.size( );
		final int itemsSize = tableView.getItems( ).size( );

		final String[] headers = new String[columnSize];
		for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ ) {
			headers[columnIndex] = visibleColumns.get( columnIndex ).getText( );
		}

		final String[][] values = new String[columnSize][itemsSize];
		for ( int rowIndex = 0; rowIndex < itemsSize; rowIndex++ ) {
			for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ ) {
				final Object cellData = visibleColumns.get( columnIndex ).getCellData( rowIndex );
				values[columnIndex][rowIndex] = cellData.toString( );
			}
		}

		final CSVData csvData = new CSVData( );
		csvData.setHeader( headers );
		csvData.setValues( values );
		return csvData;
	}

}
