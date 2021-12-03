/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.model.importer.stages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.stage.CollectorSink;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

public final class OperationCallHandlerComposite extends AbstractCompositeStage {

	private final InputPort<Trace> inputPort;
	private final OperationCallExtractor operationCallExtractor;
	private final CollectorSink<OperationCall> callCollector;
	private final CollectorSink<OperationCall> failedCallCollector;
	private final CollectorSink<AggregatedOperationCall> aggCallCollector;
	private final CollectorSink<AggregatedOperationCall> aggFailedCallCollector;

	public OperationCallHandlerComposite(final List<OperationCall> operationCalls, final List<OperationCall> failedOperationCalls,
			final List<AggregatedOperationCall> aggOperationCalls, final List<AggregatedOperationCall> aggFailedOperationCalls) {
		this.operationCallExtractor = new OperationCallExtractor();
		this.callCollector = new CollectorSink<>(operationCalls);
		final Distributor<OperationCall> distributor1 = new Distributor<>(new CopyByReferenceStrategy());
		final FailedCallFilter<OperationCall> failedCallFilter = new FailedCallFilter<>();
		this.failedCallCollector = new CollectorSink<>(failedOperationCalls);
		final OperationCallAggregator callAggregator = new OperationCallAggregator();
		this.aggCallCollector = new CollectorSink<>(aggOperationCalls);
		final FailedCallFilter<AggregatedOperationCall> aggFailedCallFilter = new FailedCallFilter<>();
		this.aggFailedCallCollector = new CollectorSink<>(aggFailedOperationCalls);
		final Distributor<AggregatedOperationCall> distributor2 = new Distributor<>(new CopyByReferenceStrategy());

		this.inputPort = this.operationCallExtractor.getInputPort();

		super.connectStages(this.operationCallExtractor.getOutputPort(), distributor1.getInputPort());
		super.connectStages(distributor1.getNewOutputPort(), this.callCollector.getInputPort());
		super.connectStages(distributor1.getNewOutputPort(), failedCallFilter.getInputPort());
		super.connectStages(distributor1.getNewOutputPort(), callAggregator.getInputPort());
		super.connectStages(callAggregator.getOutputPort(), distributor2.getInputPort());
		super.connectStages(distributor2.getNewOutputPort(), this.aggCallCollector.getInputPort());
		super.connectStages(distributor2.getNewOutputPort(), aggFailedCallFilter.getInputPort());
		super.connectStages(aggFailedCallFilter.getOutputPort(), this.aggFailedCallCollector.getInputPort());
		super.connectStages(failedCallFilter.getOutputPort(), this.failedCallCollector.getInputPort());
	}

	public InputPort<Trace> getInputPort() {
		return this.inputPort;
	}

	@Override
	protected Stage getFirstStage() {
		return this.operationCallExtractor;
	}

	@Override
	protected Collection<? extends Stage> getLastStages() {
		return Arrays.asList(this.callCollector, this.failedCallCollector, this.aggCallCollector, this.aggFailedCallCollector);
	}
}
