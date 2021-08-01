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

package kieker.diagnosis.common.model.importer.stages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kieker.diagnosis.common.domain.AggregatedOperationCall;
import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.domain.Trace;

public final class AggregatedTraceStatisticsDecorator extends AbstractStage<AggregatedTrace, AggregatedTrace> {

	@Override
	public void execute(final AggregatedTrace trace) {
		addNumberOfCalls(trace.getRootOperationCall(), trace.getTraces().size());

		final TraceDurationVisitor traceDurationVisitor = new TraceDurationVisitor();
		for (final Trace t : trace.getTraces()) {
			traceDurationVisitor.visit(t);
		}
		traceDurationVisitor.addDurationStatistics(trace);

		super.send(trace);
	}

	private static void addNumberOfCalls(final AggregatedOperationCall rootCall, final int calls) {
		rootCall.setCalls(calls);

		for (final AggregatedOperationCall child : rootCall.getChildren()) {
			addNumberOfCalls(child, calls);
		}
	}

	private static final class TraceDurationVisitor {

		private final List<List<Long>> durationsPerEdge = new ArrayList<>();
		private int edgeIndex;

		public void visit(final Trace trace) {
			this.edgeIndex = -1;
			this.visit(trace.getRootOperationCall());
		}

		private void visit(final OperationCall rootOperationCall) {
			this.edgeIndex++;
			if (this.durationsPerEdge.size() <= this.edgeIndex) {
				this.durationsPerEdge.add(new ArrayList<Long>());
			}

			final List<Long> durationsOfCurrentEdge = this.durationsPerEdge.get(this.edgeIndex);

			durationsOfCurrentEdge.add(rootOperationCall.getDuration());

			for (final OperationCall child : rootOperationCall.getChildren()) {
				this.visit(child);
			}
		}

		public void addDurationStatistics(final AggregatedTrace trace) {
			this.edgeIndex = -1;
			this.addDurationStatistics(trace.getRootOperationCall());
		}

		private void addDurationStatistics(final AggregatedOperationCall rootOperationCall) {
			this.edgeIndex++;

			final List<Long> durationsOfCurrentEdge = this.durationsPerEdge.get(this.edgeIndex);

			rootOperationCall.setMinDuration(this.findMinDuration(durationsOfCurrentEdge));
			rootOperationCall.setMaxDuration(this.findMaxDuration(durationsOfCurrentEdge));
			rootOperationCall.setAvgDuration(this.calculateAvgDuration(durationsOfCurrentEdge));
			rootOperationCall.setTotalDuration(this.calculateTotalDuration(durationsOfCurrentEdge));
			rootOperationCall.setMeanDuration(this.calculateMeanDuration(durationsOfCurrentEdge));

			for (final AggregatedOperationCall child : rootOperationCall.getChildren()) {
				this.addDurationStatistics(child);
			}
		}

		private long findMinDuration(final List<Long> durations) {
			long minDuration = Long.MAX_VALUE;

			for (final Long duration : durations) {
				minDuration = Math.min(minDuration, duration);
			}

			return minDuration;
		}

		private long findMaxDuration(final List<Long> durations) {
			long maxDuration = 0;

			for (final Long duration : durations) {
				maxDuration = Math.max(maxDuration, duration);
			}

			return maxDuration;
		}

		private long calculateAvgDuration(final List<Long> durations) {
			long totalDuration = 0;

			for (final Long duration : durations) {
				totalDuration += duration;
			}

			return totalDuration / durations.size();
		}

		private long calculateTotalDuration(final List<Long> durations) {
			long totalDuration = 0;

			for (final Long duration : durations) {
				totalDuration += duration;
			}

			return totalDuration;
		}

		private long calculateMeanDuration(final List<Long> durationsOfCurrentEdge) {
			Collections.sort(durationsOfCurrentEdge);

			return durationsOfCurrentEdge.get(durationsOfCurrentEdge.size() / 2);
		}

	}

}
