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

package kieker.diagnosis.common;

import java.util.HashMap;
import java.util.Map;

public final class Mapper<I, O> {

	private final Map<I, O> internalMap = new HashMap<>();
	public O defaultValue;

	public To map(final I key) {
		return new To(key);
	}

	public To mapPerDefault() {
		return new To();
	}

	public O resolve(final I key) {
		return this.internalMap.getOrDefault(key, this.defaultValue);
	}

	public I invertedResolve(final O value) {
		for (final Map.Entry<I, O> entry : this.internalMap.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public final class To {

		private final I key;
		private final boolean keyAvailable;

		To(final I key) {
			this.key = key;
			this.keyAvailable = true;
		}

		public To() {
			this.key = null;
			this.keyAvailable = false;
		}

		public void to(final O value) { // NOPMD (the method name may be short, but this is acceptable in this case)
			if (this.keyAvailable) {
				Mapper.this.internalMap.put(this.key, value);
			} else {
				Mapper.this.defaultValue = value;
			}
		}

	}

}
