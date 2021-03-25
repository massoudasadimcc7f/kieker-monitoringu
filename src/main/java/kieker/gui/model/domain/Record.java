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

package kieker.gui.model.domain;

/**
 * A simplified representation of a monitoring record.
 *
 * @author Nils Christian Ehmke
 */
public final class Record {

	private final long timestamp;
	private final String type;
	private final String representation;
 
	public Record(final long timestamp, final String type, final String representation) {
		this.timestamp = timestamp;
		this.type = type;
		this.representation = representation;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getType() {
		return this.type;
	}

	public String getRepresentation() {
		return this.representation;
	}

}