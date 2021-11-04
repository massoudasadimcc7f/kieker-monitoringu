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

package kieker.diagnosis.domain;

/**
 * This class represents an aggregated operation call within this application. It adds some properties that are only available due to aggregation, like the average duration of all
 * calls.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedOperationCall extends AbstractOperationCall<AggregatedOperationCall> {

	private long totalDuration;
	private long medianDuration;
	private long minDuration;
	private long maxDuration;
	private long meanDuration;
	private int calls;

	public AggregatedOperationCall(final OperationCall call) {
		super(call.getContainer(), call.getComponent(), call.getOperation(), call.getFailedCause());

		for (final OperationCall child : call.getChildren()) {
			super.addChild(new AggregatedOperationCall(child));
		}

		this.setStackDepth(call.getStackDepth());
		this.setStackSize(call.getStackSize());
	}

	public AggregatedOperationCall(final String container, final String component, final String operation, // NOPMD (a long parameter list cannot be avoided)
			final String failedCause, final long totalDuration, final long medianDuration, final long minDuration, final long maxDuration, final long meanDuration,
			final int calls) {
		super(container, component, operation);

		this.totalDuration = totalDuration;
		this.medianDuration = medianDuration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.meanDuration = meanDuration;
		this.calls = calls;

		this.setFailedCause(failedCause);
	}

	public long getTotalDuration() {
		return this.totalDuration;
	}

	public void setTotalDuration(final long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public long getMedianDuration() {
		return this.medianDuration;
	}

	public void setMedianDuration(final long meanDuration) {
		this.medianDuration = meanDuration;
	}

	public long getMinDuration() {
		return this.minDuration;
	}

	public void setMinDuration(final long minDuration) {
		this.minDuration = minDuration;
	}

	public long getMaxDuration() {
		return this.maxDuration;
	}

	public void setMaxDuration(final long maxDuration) {
		this.maxDuration = maxDuration;
	}

	public long getMeanDuration() {
		return this.meanDuration;
	}

	public void setMeanDuration(final long avgDuration) {
		this.meanDuration = avgDuration;
	}

	public int getCalls() {
		return this.calls;
	}

	public void setCalls(final int calls) {
		this.calls = calls;
	}

}