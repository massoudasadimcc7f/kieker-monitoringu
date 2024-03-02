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

package kieker.diagnosis.guitest.tests;

import static org.junit.Assert.assertTrue;

import kieker.diagnosis.application.Main;
import kieker.diagnosis.guitest.GuiTestConfiguration;
import kieker.diagnosis.guitest.views.CallsView;
import kieker.diagnosis.guitest.views.MainView;
import kieker.diagnosis.guitestarchitecture.JavaFXThreadPerformer;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = GuiTestConfiguration.class )
public class CallsViewTest {

	@Autowired
	private JavaFXThreadPerformer performer;

	@Autowired
	private MainView mainView;

	@Autowired
	private CallsView callsView;

	@Autowired
	private Main ivMain;

	@Test
	public void testFilterForCallView( ) throws InterruptedException {
		// Unfortunately TestFX cannot handle the native file dialog of JavaFX. Therefore we have to use direct access as a workaround.
		performer.perform( ( ) -> {
			ivMain.getDataService( ).loadMonitoringLogFromFS( new File( "../kieker-trace-diagnosis-release-engineering/example/event monitoring log" ) );
		} );

		mainView.getCallsButton( ).click( );

		callsView.getFilterContainerTextField( ).setText( "SE-Nils-Ehmke" );
		callsView.getFilterContainerTextField( ).pushEnter( );
		callsView.getFilterComponentTextField( ).setText( "kieker.examples.bookstore.Bookstore" );
		callsView.getFilterComponentTextField( ).pushEnter( );
		callsView.getFilterOperationTextField( ).setText( "public void kieker.examples.bookstore.Bookstore.searchBook()" );
		callsView.getFilterOperationTextField( ).pushEnter( );
		callsView.getFilterTraceIDTextField( ).setText( "4658150164341456896" );
		callsView.getFilterTraceIDTextField( ).pushEnter( );

		assertTrue( callsView.getCounterTextField( ).getText( ).startsWith( "1 " ) );
	}

}