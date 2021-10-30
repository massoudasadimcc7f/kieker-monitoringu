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

package kieker.diagnosis.common.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.common.domain.AggregatedOperationCall;
import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.domain.Trace;
import kieker.diagnosis.common.model.importer.ImportAnalysisConfiguration;
import kieker.diagnosis.common.util.Mapper;
import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class DataModel extends Observable {

	private final Mapper<TimeUnit, String> shortTimeUnitMapper = new Mapper<>();

	private List<Trace> traces = Collections.emptyList();
	private List<Trace> failureContainingTraces = Collections.emptyList();
	private List<Trace> failedTraces = Collections.emptyList();
	private List<AggregatedTrace> aggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failedAggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failureAggregatedContainingTraces = Collections.emptyList();
	private List<OperationCall> operationCalls = Collections.emptyList();
	private List<OperationCall> failedOperationCalls = Collections.emptyList();
	private List<AggregatedOperationCall> aggregatedOperationCalls = Collections.emptyList();
	private List<AggregatedOperationCall> aggregatedFailedOperationCalls = Collections.emptyList();

	private String shortTimeUnit = "";

	public DataModel() {
		this.initializeMapper();
	}

	private void initializeMapper() {
		this.shortTimeUnitMapper.map(TimeUnit.NANOSECONDS).to("ns");
		this.shortTimeUnitMapper.map(TimeUnit.MICROSECONDS).to("us");
		this.shortTimeUnitMapper.map(TimeUnit.MILLISECONDS).to("ms");
		this.shortTimeUnitMapper.map(TimeUnit.SECONDS).to("s");
		this.shortTimeUnitMapper.map(TimeUnit.MINUTES).to("m");
		this.shortTimeUnitMapper.map(TimeUnit.HOURS).to("h");
		this.shortTimeUnitMapper.map(TimeUnit.DAYS).to("d");
	}

	public void loadMonitoringLogFromFS(final String directory) {
		// Load and analyze the monitoring logs from the given directory
		final File importDirectory = new File(directory);
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(importDirectory);
		final Analysis analysis = new Analysis(analysisConfiguration);
		analysis.start();

		// Store the results from the analysis
		this.traces = analysisConfiguration.getTracesList();
		this.failedTraces = analysisConfiguration.getFailedTracesList();
		this.failureContainingTraces = analysisConfiguration.getFailureContainingTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();
		this.failedAggregatedTraces = analysisConfiguration.getFailedAggregatedTracesList();
		this.failureAggregatedContainingTraces = analysisConfiguration.getFailureContainingAggregatedTracesList();
		this.operationCalls = analysisConfiguration.getOperationCalls();
		this.failedOperationCalls = analysisConfiguration.getFailedOperationCalls();
		this.aggregatedOperationCalls = analysisConfiguration.getAggregatedOperationCalls();
		this.aggregatedFailedOperationCalls = analysisConfiguration.getAggregatedFailedOperationCalls();

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords();
		if (!metadataRecords.isEmpty()) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get(0);
			this.shortTimeUnit = this.convertToShortTimeUnit(TimeUnit.valueOf(metadataRecord.getTimeUnit()));
		} else {
			this.shortTimeUnit = "";
		}

		this.setChanged();
		this.notifyObservers();
	}

	private String convertToShortTimeUnit(final TimeUnit timeUnit) {
		return this.shortTimeUnitMapper.resolve(timeUnit);
	}

	public String getShortTimeUnit() {
		return this.shortTimeUnit;
	}

	public List<Trace> getTracesCopy() {
		return new ArrayList<>(this.traces);
	}

	public List<Trace> getFailedTracesCopy() {
		return new ArrayList<>(this.failedTraces);
	}

	public List<Trace> getFailureContainingTracesCopy() {
		return new ArrayList<>(this.failureContainingTraces);
	}

	public List<AggregatedTrace> getAggregatedTracesCopy() {
		return new ArrayList<>(this.aggregatedTraces);
	}

	public List<AggregatedTrace> getFailedAggregatedTracesCopy() {
		return new ArrayList<>(this.failedAggregatedTraces);
	}

	public List<AggregatedTrace> getFailureContainingAggregatedTracesCopy() {
		return new ArrayList<>(this.failureAggregatedContainingTraces);
	}

	public List<OperationCall> getOperationCalls() {
		return new ArrayList<>(this.operationCalls);
	}

	public List<OperationCall> getFailedOperationCalls() {
		return new ArrayList<>(this.failedOperationCalls);
	}

	public List<AggregatedOperationCall> getAggregatedOperationCalls() {
		return new ArrayList<>(this.aggregatedOperationCalls);
	}

	public List<AggregatedOperationCall> getAggregatedFailedOperationCalls() {
		return new ArrayList<>(this.aggregatedFailedOperationCalls);
	}

}