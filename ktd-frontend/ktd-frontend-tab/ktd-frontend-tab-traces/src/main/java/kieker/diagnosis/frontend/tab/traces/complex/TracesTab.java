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

package kieker.diagnosis.frontend.tab.traces.complex;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.pattern.PatternService;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.backend.search.traces.TracesService;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.dialog.alert.Alert;
import kieker.diagnosis.frontend.tab.traces.composite.TraceDetailsPane;
import kieker.diagnosis.frontend.tab.traces.composite.TraceFilterPane;
import kieker.diagnosis.frontend.tab.traces.composite.TraceStatusBar;
import kieker.diagnosis.frontend.tab.traces.composite.TracesTreeTableView;

public final class TracesTab extends Tab {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( TracesTab.class.getName( ) );

	private final TraceFilterPane filterPane = new TraceFilterPane( );
	private final TracesTreeTableView treeTableView = new TracesTreeTableView( );
	private final TraceDetailsPane detailsPane = new TraceDetailsPane( );
	private final TraceStatusBar statusBar = new TraceStatusBar( );

	private Consumer<TracesFilter> onSaveAsFavorite;
	private List<MethodCall> traceRootsForRefresh;
	private int totalTracesForRefresh;
	private String durationSuffixForRefresh;

	public TracesTab( ) {
		createControl( );
		performInitialize( );
	}

	private void createControl( ) {
		final VBox vbox = new VBox( );
		setContent( vbox );

		configureFilterPane( );
		vbox.getChildren( ).add( filterPane );

		configureTreeTableView( );
		vbox.getChildren( ).add( treeTableView );

		vbox.getChildren( ).add( detailsPane );

		configureStatusBar( );
		vbox.getChildren( ).add( statusBar );
	}

	private void configureFilterPane( ) {
		filterPane.setOnSearch( e -> performSearch( ) );
		filterPane.setOnSaveAsFavorite( e -> performSaveAsFavorite( ) );
	}

	private void configureTreeTableView( ) {
		treeTableView.setId( "tabTracesTreeTable" );
		treeTableView.addSelectionChangeListener( ( aObservable, aOldValue, aNewValue ) -> detailsPane.setValue( aNewValue != null ? aNewValue.getValue( ) : null ) );

		VBox.setVgrow( treeTableView, Priority.ALWAYS );
	}

	private void configureStatusBar( ) {
		VBox.setMargin( statusBar, new Insets( 5 ) );
	}

	private void performInitialize( ) {
		detailsPane.setValue( null );
		statusBar.setValue( 0, 0 );
		filterPane.setValue( new TracesFilter( ) );
	}

	/**
	 * Returns the default button property of the search button.
	 *
	 * @return The default button property.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	/**
	 * This action is performed, when the user wants to perform a search.
	 */
	private void performSearch( ) {
		// Get the filter input from the user
		final TracesFilter filter = filterPane.getValue( );
		if ( checkFilter( filter ) ) {

			// Find the trace roots to display
			final TracesService tracesService = ServiceFactory.getService( TracesService.class );
			final List<MethodCall> traceRoots = tracesService.searchTraces( filter );
			final int totalTraces = tracesService.countTraces( );

			// Update the view
			treeTableView.setItems( traceRoots );
			statusBar.setValue( traceRoots.size( ), totalTraces );
		}
	}

	private void performSaveAsFavorite( ) {
		if ( onSaveAsFavorite != null ) {
			final TracesFilter filter = filterPane.getValue( );
			if ( checkFilter( filter ) ) {
				onSaveAsFavorite.accept( filter );
			}
		}
	}

	public void prepareRefresh( ) {
		// Find the trace roots to display
		final TracesService tracesService = ServiceFactory.getService( TracesService.class );
		traceRootsForRefresh = tracesService.searchTraces( new TracesFilter( ) );
		totalTracesForRefresh = tracesService.countTraces( );

		// Get the duration suffix
		final SettingsService settingsService = ServiceFactory.getService( SettingsService.class );
		durationSuffixForRefresh = settingsService.getCurrentDurationSuffix( );
	}

	public void performRefresh( ) {
		// Reset the filter
		final TracesFilter filter = new TracesFilter( );
		filterPane.setValue( filter );

		// Update the view
		treeTableView.setItems( traceRootsForRefresh );
		statusBar.setValue( traceRootsForRefresh.size( ), totalTracesForRefresh );

		// Update the column header of the table
		treeTableView.setDurationSuffix( durationSuffixForRefresh );
	}

	private boolean checkFilter( final TracesFilter filter ) {
		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = ServiceFactory.getService( PatternService.class );

			final StringBuilder strBuilder = new StringBuilder( );

			if ( !( patternService.isValidPattern( filter.getHost( ) ) || filter.getHost( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getHost( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getClazz( ) ) || filter.getClazz( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getClazz( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getMethod( ) ) || filter.getMethod( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getMethod( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getException( ) ) || filter.getException( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getException( ) ) );
			}

			if ( strBuilder.length( ) > 0 ) {
				final Alert alert = new Alert( AlertType.WARNING );
				final String msg = RESOURCE_BUNDLE.getString( "errorMessageInvalidInput" ) + strBuilder.toString( );
				alert.setContentText( msg );
				alert.showAndWait( );

				return false;
			}
		}

		return true;
	}

	public void setOnSaveAsFavorite( final Consumer<TracesFilter> onSaveAsFavorite ) {
		this.onSaveAsFavorite = onSaveAsFavorite;
	}

	public void setFilterValue( final TracesFilter value ) {
		filterPane.setValue( value );
		performSearch( );
	}

	public void setFilterValue( final MethodCall value ) {
		filterPane.setValue( value );
		performSearch( );

		treeTableView.setSelected( value );
	}

}
