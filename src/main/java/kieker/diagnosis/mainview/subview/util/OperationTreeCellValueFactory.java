package kieker.diagnosis.mainview.subview.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.OperationNames;

public class OperationTreeCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<String>> {

	private static final Logger LOGGER = LogManager.getLogger(DurationTreeCellValueFactory.class);
	
	private final String property;

	public OperationTreeCellValueFactory(@NamedArg(value = "property") final String property) {
		this.property = property.substring(0, 1).toUpperCase(Locale.ROOT) + property.substring(1);
	}

	@Override
	public ObservableValue<String> call(final CellDataFeatures<?, String> call) {
		try {
			final TreeItem<?> item = (call.getValue());
			final Method getter = item.getValue().getClass().getMethod("get" + this.property, new Class<?>[0]);
			String operationName = (String) getter.invoke(item.getValue(), new Object[0]);

			if (PropertiesModel.getInstance().getOperationNames() == OperationNames.SHORT) {
				operationName = NameConverter.toShortOperationName(operationName);
			}

			return new ReadOnlyObjectWrapper<String>(operationName);
		} catch (final NullPointerException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			LOGGER.warn(ex);
			return null;
		}
	}

}
