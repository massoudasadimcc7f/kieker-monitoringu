package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractStringApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.StringApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class StringApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractStringApplicationProperty property = new StringApplicationProperty( );

		assertThat( property.serialize( "42" ), is( "42" ) );
		assertThat( property.serialize( "test" ), is( "test" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractStringApplicationProperty property = new StringApplicationProperty( );

		assertThat( property.deserialize( "42" ), is( "42" ) );
		assertThat( property.deserialize( "test" ), is( "test" ) );
	}

}