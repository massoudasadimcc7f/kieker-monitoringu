package kieker.diagnosis.mainview.subview.util;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;

public class DurationTreeCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<Long>> {

	private final DataModel dataModel = DataModel.getInstance();
	private final PropertiesModel propertiesModel = PropertiesModel.getInstance();

	private final String property;

	public DurationTreeCellValueFactory(@NamedArg(value = "property") final String property) {
		this.property = property.substring(0, 1).toUpperCase() + property.substring(1);
	}

	@Override
	public ObservableValue<Long> call(final CellDataFeatures<?, String> call) {
		try {
			final TimeUnit srcTimeUnit = this.dataModel.getTimeUnit();
			final TimeUnit dstTimeUnit = this.propertiesModel.getTimeUnit();

			final TreeItem<?> item = (call.getValue());
			final Method getter = item.getValue().getClass().getMethod("get" + this.property, new Class<?>[0]);
			final long duration = (long) getter.invoke(item.getValue(), new Object[0]);

			final long newDuration = dstTimeUnit.convert(duration, srcTimeUnit);
			return new ReadOnlyObjectWrapper<Long>(newDuration);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
