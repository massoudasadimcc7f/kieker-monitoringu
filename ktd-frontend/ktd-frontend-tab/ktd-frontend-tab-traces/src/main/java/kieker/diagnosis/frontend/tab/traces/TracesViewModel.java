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

package kieker.diagnosis.frontend.tab.traces;

import java.text.NumberFormat;
import java.util.List;
import java.util.Stack;

import com.google.inject.Singleton;

import javafx.scene.control.TreeItem;
import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.pattern.PatternService;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.backend.settings.MethodCallAggregation;
import kieker.diagnosis.backend.settings.properties.MaxNumberOfMethodCallsProperty;
import kieker.diagnosis.backend.settings.properties.MethodCallAggregationProperty;
import kieker.diagnosis.backend.settings.properties.MethodCallThresholdProperty;
import kieker.diagnosis.backend.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.frontend.base.ui.ViewModelBase;
import kieker.diagnosis.frontend.tab.traces.aggregator.Aggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.DurationAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.IdentityAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.ThresholdAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.TraceDepthAggregator;
import kieker.diagnosis.frontend.tab.traces.aggregator.TraceSizeAggregator;
import kieker.diagnosis.frontend.tab.traces.atom.MethodCallTreeItem;

/**
 * The view model of the traces tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class TracesViewModel extends ViewModelBase<TracesView> {

	public void updatePresentationTraces( final List<MethodCall> aTraceRoots ) {
		final PropertiesService propertiesService = getService( PropertiesService.class );
		final boolean showUnmonitoredTime = propertiesService.loadApplicationProperty( ShowUnmonitoredTimeProperty.class );

		// Prepare the aggregator based on the properties
		final Aggregator aggregator;

		final MethodCallAggregation aggregation = propertiesService.loadApplicationProperty( MethodCallAggregationProperty.class );
		final float threshold = propertiesService.loadApplicationProperty( MethodCallThresholdProperty.class );
		final int maxCalls = propertiesService.loadApplicationProperty( MaxNumberOfMethodCallsProperty.class );

		switch ( aggregation ) {
			case BY_DURATION:
				aggregator = new DurationAggregator( maxCalls );
			break;
			case BY_THRESHOLD:
				aggregator = new ThresholdAggregator( threshold );
			break;
			case BY_TRACE_DEPTH:
				aggregator = new TraceDepthAggregator( maxCalls );
			break;
			case BY_TRACE_SIZE:
				aggregator = new TraceSizeAggregator( maxCalls );
			break;
			case NONE:
			default:
				aggregator = new IdentityAggregator( );
			break;

		}

		final TreeItem<MethodCall> root = new TreeItem<>( );
		root.setValue( new MethodCall( ) );

		// Convert the trace roots to tree items
		for ( final MethodCall methodCall : aTraceRoots ) {
			root.getChildren( ).add( new MethodCallTreeItem( methodCall, showUnmonitoredTime, aggregator ) );
		}

		getView( ).getTreeTableView( ).setRoot( root );
	}

	public void updatePresentationDurationColumnHeader( final String aSuffix ) {
		getView( ).getDurationColumn( ).setText( getLocalizedString( "columnDuration" ) + " " + aSuffix );
	}

	public void updatePresentationDetails( final MethodCall methodCall ) {
		getView( ).getDetails( ).setValue( methodCall );
	}

	public void updatePresentationStatus( final int aTraces, final int aTotalTraces ) {
		final NumberFormat decimalFormat = NumberFormat.getInstance( );
		getView( ).getStatus( ).setValue( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aTraces ), decimalFormat.format( aTotalTraces ) ) );
	}

	public MethodCall getSelected( ) {
		final TreeItem<MethodCall> selectedItem = getView( ).getTreeTableView( ).getSelectionModel( ).getSelectedItem( );
		return selectedItem != null ? selectedItem.getValue( ) : null;
	}

	public void updatePresentationFilter( final TracesFilter filter ) {
		getView( ).getFilter( ).setValue( filter );
	}

	public TracesFilter savePresentationFilter( ) throws BusinessException {
		final TracesFilter filter = getView( ).getFilter( ).getValue( );

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

	public void select( final MethodCall aMethodCall ) {
		final TreeItem<MethodCall> root = getView( ).getTreeTableView( ).getRoot( );

		final Stack<TreeItem<MethodCall>> stack = new Stack<>( );
		stack.push( root );

		while ( !stack.isEmpty( ) ) {
			final TreeItem<MethodCall> treeItem = stack.pop( );

			if ( treeItem.getValue( ) == aMethodCall ) {
				// We found the item. Select it - and expand all parents
				expand( treeItem );
				getView( ).getTreeTableView( ).getSelectionModel( ).select( treeItem );

				break;
			} else {
				// Search in the children
				stack.addAll( treeItem.getChildren( ) );
			}
		}
	}

	private void expand( final TreeItem<MethodCall> aRoot ) {
		TreeItem<MethodCall> root = aRoot;
		while ( root != null ) {
			root.setExpanded( true );
			root = root.getParent( );
		}
	}

}
