/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service.data.stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.diagnosis.service.data.domain.AggregatedTrace;
import kieker.diagnosis.service.data.domain.Trace;

import teetime.stage.basic.AbstractTransformation;

/**
 * This stage aggregates incoming traces into trace equivalence classes.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceAggregator extends AbstractTransformation<Trace, AggregatedTrace> {

	private final Map<TraceWrapper, List<Trace>> ivAggregationMap = new HashMap<>( );

	@Override
	protected void execute( final Trace aTrace ) {
		final TraceWrapper wrapper = new TraceWrapper( aTrace );
		if ( !ivAggregationMap.containsKey( wrapper ) ) {
			final List<Trace> aggregationList = new ArrayList<>( );
			ivAggregationMap.put( wrapper, aggregationList );
		}
		ivAggregationMap.get( wrapper ).add( aTrace );
	}

	@Override
	public void onTerminating( ) throws Exception { // NOPMD (the throws clause is forced by the framework)
		ivAggregationMap.values( ).forEach( list -> super.getOutputPort( ).send( new AggregatedTrace( list ) ) );

		super.onTerminating( );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private static class TraceWrapper {

		private final Trace ivTrace;

		public TraceWrapper( final Trace aTrace ) {
			ivTrace = aTrace;
		}

		@Override
		public int hashCode( ) {
			return ivTrace.calculateHashCode( );
		}

		@Override
		public boolean equals( final Object aObj ) {
			if ( !( aObj instanceof TraceWrapper ) ) {
				return false;
			}
			return ivTrace.isEqualTo( ( (TraceWrapper) aObj ).ivTrace );
		}

	}

}
