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

package kieker.diagnosis.gui.about;

import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.Context;

/**
 * The controller for the about dialog. The about dialog shows some information about the application.
 *
 * @author Nils Christian Ehmke
 */
public final class AboutDialogController extends AbstractController<AboutDialogView> implements AboutDialogControllerIfc {

	public AboutDialogController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		// Nothing to initialize
	}

	@Override
	public void performCloseDialog( ) {
		getView( ).getStage( ).hide( );
	}

}
