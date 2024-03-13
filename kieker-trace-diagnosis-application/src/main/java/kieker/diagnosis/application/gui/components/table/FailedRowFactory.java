/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.gui.components.table;

import kieker.diagnosis.application.service.data.domain.AbstractOperationCall;
import kieker.diagnosis.architecture.gui.components.AutowireCandidate;

import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;

/**
 * @author Nils Christian Ehmke
 *
 * @param <S>
 *            The type of the table.
 */
public final class FailedRowFactory<S> implements Callback<TableView<S>, TableRow<S>>, AutowireCandidate {

	@Override
	public TableRow<S> call( final TableView<S> aParam ) {
		return new FailedTableRow( );
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	private final class FailedTableRow extends TableRow<S> {

		private static final String STYLECLASS_FAILED = "failed";

		@Override
		protected void updateItem( final S aItem, final boolean aEmpty ) {
			super.updateItem( aItem, aEmpty );

			if ( aItem instanceof AbstractOperationCall<?> ) {
				final AbstractOperationCall<?> call = (AbstractOperationCall<?>) aItem;

				getStyleClass( ).remove( STYLECLASS_FAILED );

				if ( call.isFailed( ) ) {
					getStyleClass( ).add( STYLECLASS_FAILED );
				}
			}
		}

	}

}
