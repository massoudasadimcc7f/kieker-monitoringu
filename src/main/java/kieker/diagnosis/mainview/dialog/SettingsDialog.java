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

package kieker.diagnosis.mainview.dialog;

import java.util.concurrent.TimeUnit;

import kieker.diagnosis.common.Mapper;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;
import kieker.diagnosis.model.PropertiesModel.OperationNames;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import swing2swt.layout.FlowLayout;

public final class SettingsDialog extends Dialog {

	private final PropertiesModel model;

	private int result;
	private Shell shlSettings;

	private Combo comboBoxTimeUnit;
	private Combo comboBoxOperationNames;
	private Combo comboBoxComponentNames;

	private Mapper<TimeUnit, Integer> timeUnitMapper;

	public SettingsDialog(final Shell parent, final int style, final PropertiesModel model) {
		super(parent, style);

		this.model = model;
		this.initializeMapper();
	}

	private void initializeMapper() {
		this.timeUnitMapper = new Mapper<>();

		this.timeUnitMapper.map(TimeUnit.NANOSECONDS).to(0);
		this.timeUnitMapper.map(TimeUnit.MICROSECONDS).to(1);
		this.timeUnitMapper.map(TimeUnit.MILLISECONDS).to(2);
		this.timeUnitMapper.map(TimeUnit.SECONDS).to(3);
		this.timeUnitMapper.map(TimeUnit.MINUTES).to(4);
		this.timeUnitMapper.map(TimeUnit.HOURS).to(5);
		this.timeUnitMapper.map(TimeUnit.DAYS).to(6);
	}

	public int open() {
		this.createContents();

		this.loadSettings();

		this.shlSettings.pack();

		this.shlSettings.open();
		final Display display = this.getParent().getDisplay();

		final Rectangle screenSize = display.getPrimaryMonitor().getBounds();
		this.shlSettings.setLocation((screenSize.width - this.shlSettings.getBounds().width) / 2, (screenSize.height - this.shlSettings.getBounds().height) / 2);

		while (!this.shlSettings.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return this.result;
	}

	private void loadSettings() {
		this.comboBoxTimeUnit.select(this.timeUnitMapper.resolve(this.model.getTimeUnit()));
		this.comboBoxOperationNames.select(this.model.getOperationNames() == OperationNames.SHORT ? 0 : 1);
		this.comboBoxComponentNames.select(this.model.getComponentNames() == ComponentNames.SHORT ? 0 : 1);
	}

	private void saveSettings() {
		this.model.startModification();

		this.model.setTimeUnit(this.timeUnitMapper.invertedResolve(this.comboBoxTimeUnit.getSelectionIndex()));
		this.model.setOperationNames(this.comboBoxOperationNames.getSelectionIndex() == 0 ? OperationNames.SHORT : OperationNames.LONG);
		this.model.setComponentNames(this.comboBoxComponentNames.getSelectionIndex() == 0 ? ComponentNames.SHORT : ComponentNames.LONG);

		this.model.commitModification();
	}

	private void createContents() {
		this.shlSettings = new Shell(this.getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.shlSettings.setText("Settings");
		this.shlSettings.setLayout(new GridLayout(1, false));

		final Group grpAppearance = new Group(this.shlSettings, SWT.NONE);
		grpAppearance.setText("Appearance");
		grpAppearance.setLayout(new GridLayout(2, false));

		final Label lblTimeUnit = new Label(grpAppearance, SWT.NONE);
		lblTimeUnit.setText("Time Unit:");

		this.comboBoxTimeUnit = new Combo(grpAppearance, SWT.READ_ONLY);
		this.comboBoxTimeUnit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.comboBoxTimeUnit.setItems(new String[] { "Nanoseconds (ns)", "Microseconds (\u00B5s)", "Milliseconds (ms)", "Seconds (s)", "Minutes (m)", "Hours (h)", "Days (d)" });
		this.comboBoxTimeUnit.select(0);

		final Label lblOperationNames = new Label(grpAppearance, SWT.NONE);
		lblOperationNames.setText("Operations:");

		this.comboBoxOperationNames = new Combo(grpAppearance, SWT.READ_ONLY);
		this.comboBoxOperationNames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.comboBoxOperationNames.setItems(new String[] { "getBook(...)", "public void kieker.examples.bookstore.Catalog.getBook(boolean)" });
		this.comboBoxOperationNames.select(0);

		final Label lblComponentNames = new Label(grpAppearance, SWT.NONE);
		lblComponentNames.setText("Components:");

		this.comboBoxComponentNames = new Combo(grpAppearance, SWT.READ_ONLY);
		this.comboBoxComponentNames.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		this.comboBoxComponentNames.setItems(new String[] { "Catalog", "kieker.examples.bookstore.Catalog" });
		this.comboBoxComponentNames.select(0);

		final Composite composite = new Composite(this.shlSettings, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final Button btnOkay = new Button(composite, SWT.NONE);
		btnOkay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SettingsDialog.this.result = SWT.OK;

				SettingsDialog.this.saveSettings();

				SettingsDialog.this.shlSettings.close();
			}
		});
		btnOkay.setText("OK");

		final Button btnCancel = new Button(composite, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SettingsDialog.this.result = SWT.CANCEL;
				SettingsDialog.this.shlSettings.close();
			}
		});
		btnCancel.setText("Cancel");
	}

}
