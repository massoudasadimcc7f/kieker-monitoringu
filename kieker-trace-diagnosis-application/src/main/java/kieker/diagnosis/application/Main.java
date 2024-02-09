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

package kieker.diagnosis.application;

import kieker.diagnosis.application.gui.main.MainController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.architecture.gui.GuiLoader;

import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.collect.Iterables;

/**
 * This class contains the main method of this application.
 *
 * @author Nils Christian Ehmke
 */
@SpringBootApplication
public class Main extends Application {

	private DataService ivDataService;

	/**
	 * The main method of this application.
	 *
	 * @param aArgs
	 *            The command line arguments. They are currently not intended to be used.
	 */
	public static void main( final String[] aArgs ) {
		Application.launch( aArgs );
	}

	@Override
	public void start( final Stage aPrimaryStage ) throws Exception {
		// Load the Spring context
		final SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder( getClass( ) );
		final ConfigurableApplicationContext context = springApplicationBuilder.bannerMode( Mode.OFF ).logStartupInfo( false ).run( getArguments( ) );

		// Now load the main view
		final GuiLoader guiLoader = context.getBean( GuiLoader.class );
		guiLoader.loadAsMainView( MainController.class, aPrimaryStage );

		// Load the dataservice. This is just for the GUI test as the dialogs can not be handled by test fx
		ivDataService = context.getBean( DataService.class );
	}

	private String[] getArguments( ) {
		final Parameters parameters = getParameters( );
		if ( parameters != null ) {
			final List<String> rawParameters = parameters.getRaw( );
			return Iterables.toArray( rawParameters, String.class );
		} else {
			return new String[0];
		}
	}

	public DataService getDataService( ) {
		return ivDataService;
	}

}
