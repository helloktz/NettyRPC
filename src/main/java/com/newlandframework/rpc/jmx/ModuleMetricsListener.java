package com.newlandframework.rpc.jmx;

import javax.management.AttributeChangeNotification;
import javax.management.JMException;
import javax.management.Notification;
import javax.management.NotificationListener;

import com.newlandframework.rpc.event.AbstractInvokeEventBus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModuleMetricsListener implements NotificationListener {
	@Override
	public void handleNotification(Notification notification, Object handback) {
		if (!(notification instanceof AttributeChangeNotification)) {
			return;
		}

		AttributeChangeNotification acn = (AttributeChangeNotification) notification;
		AbstractInvokeEventBus.ModuleEvent event = Enum.valueOf(AbstractInvokeEventBus.ModuleEvent.class, acn.getAttributeType());
		ModuleMetricsVisitor visitor = ModuleMetricsHandler.getInstance().visit(acn.getMessage(), acn.getAttributeName());

		switch (event) {
		case INVOKE_EVENT:
			visitor.setInvokeCount(((Long) acn.getNewValue()).longValue());
			break;
		case INVOKE_SUCC_EVENT:
			visitor.setInvokeSuccCount(((Long) acn.getNewValue()).longValue());
			break;
		case INVOKE_FAIL_EVENT:
			visitor.setInvokeFailCount(((Long) acn.getNewValue()).longValue());
			break;
		case INVOKE_FILTER_EVENT:
			visitor.setInvokeFilterCount(((Long) acn.getNewValue()).longValue());
			break;
		case INVOKE_TIMESPAN_EVENT:
			visitor.setInvokeTimespan(((Long) acn.getNewValue()).longValue());
			visitor.getHistogram().record(((Long) acn.getNewValue()).longValue());
			break;
		case INVOKE_MAX_TIMESPAN_EVENT:
			if ((Long) acn.getNewValue() > (Long) acn.getOldValue()) {
				visitor.setInvokeMaxTimespan(((Long) acn.getNewValue()).longValue());
			}
			break;
		case INVOKE_MIN_TIMESPAN_EVENT:
			if ((Long) acn.getNewValue() < (Long) acn.getOldValue()) {
				visitor.setInvokeMinTimespan(((Long) acn.getNewValue()).longValue());
			}
			break;
		case INVOKE_FAIL_STACKTRACE_EVENT:
			try {
				visitor.setLastStackTrace((Exception) acn.getNewValue());
				visitor.buildErrorCompositeData((Exception) acn.getNewValue());
			} catch (JMException e) {
				log.error(e.getMessage(), e);
			}
			break;
		}
	}
}
