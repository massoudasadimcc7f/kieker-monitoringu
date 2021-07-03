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

package kieker.gui.common.model.importer.stages;

import java.util.List;

import kieker.gui.common.domain.AggregatedTrace;
import kieker.gui.common.domain.Trace;
import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.framework.TerminationStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.stage.CollectorSink;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

public final class TraceAggregationComposite extends Stage {

	private final TraceAggregator aggregator;

	private final CollectorSink<AggregatedTrace> tracesCollector;
	private final CollectorSink<AggregatedTrace> failedTracesCollector;
	private final CollectorSink<AggregatedTrace> failureContainingTracesCollector;
	private final AggregatedTraceStatisticsDecorator statisticsDecorator;

	public TraceAggregationComposite(final List<AggregatedTrace> traces, final List<AggregatedTrace> failedTraces, final List<AggregatedTrace> failureContainingTraces) {
		this.aggregator = new TraceAggregator();
		this.statisticsDecorator = new AggregatedTraceStatisticsDecorator();

		final Distributor<AggregatedTrace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final FailedTraceFilter<AggregatedTrace> failedTraceFilter = new FailedTraceFilter<>();
		final FailureContainingTraceFilter<AggregatedTrace> failureContainingTraceFilter = new FailureContainingTraceFilter<>();

		this.tracesCollector = new CollectorSink<>(traces);
		this.failedTracesCollector = new CollectorSink<>(failedTraces);
		this.failureContainingTracesCollector = new CollectorSink<>(failureContainingTraces);

		final IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(this.aggregator.getOutputPort(), this.statisticsDecorator.getInputPort());
		pipeFactory.create(this.statisticsDecorator.getOutputPort(), distributor.getInputPort());

		pipeFactory.create(distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		pipeFactory.create(distributor.getNewOutputPort(), failedTraceFilter.getInputPort());
		pipeFactory.create(distributor.getNewOutputPort(), failureContainingTraceFilter.getInputPort());

		pipeFactory.create(failedTraceFilter.getOutputPort(), this.failedTracesCollector.getInputPort());
		pipeFactory.create(failureContainingTraceFilter.getOutputPort(), this.failureContainingTracesCollector.getInputPort());
	}

	@Override
	protected void executeWithPorts() {
		this.aggregator.executeWithPorts();
	}

	public InputPort<Trace> getInputPort() {
		return this.aggregator.getInputPort();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		// No code necessary
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.aggregator.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return this.aggregator.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		this.aggregator.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return this.aggregator.shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return this.aggregator.getInputPorts();
	}

	@Override
	protected boolean isStarted() {
		return this.tracesCollector.isStarted() && this.failedTracesCollector.isStarted() && this.failureContainingTracesCollector.isStarted()
				&& this.statisticsDecorator.isStarted();
	}

}