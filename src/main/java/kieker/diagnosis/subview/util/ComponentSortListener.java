package kieker.diagnosis.subview.util;

import kieker.diagnosis.common.domain.AbstractTrace;

public final class ComponentSortListener extends AbstractTraceTreeColumnSortListener<AbstractTrace<?>> {

	@Override
	protected int compare(final AbstractTrace<?> fstTrace, final AbstractTrace<?> sndTrace) {
		final String fstComponent = fstTrace.getRootOperationCall().getComponent();
		final String sndComponent = sndTrace.getRootOperationCall().getComponent();

		return fstComponent.compareTo(sndComponent);
	}

}
