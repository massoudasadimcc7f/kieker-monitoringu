package kieker.diagnosis.service.properties;

public class MyFirstProperty extends AbstractBooleanApplicationProperty {

	@Override
	public String getDefaultValue( ) {
		return serialize( Boolean.TRUE );
	}

	@Override
	public String getKey( ) {
		return "testProperty1";
	}

}