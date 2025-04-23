/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.dialogs.manual;

import com.google.inject.Singleton;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import kieker.diagnosis.architecture.ui.ViewModelBase;

/**
 * The view model of the user manual dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class ManualDialogViewModel extends ViewModelBase<ManualDialogView> {

	public void updatePresentation( final String aContent ) {
		final WebView webView = getView( ).getWebView( );
		final WebEngine engine = webView.getEngine( );
		engine.loadContent( aContent );
	}

}