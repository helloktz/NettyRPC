package com.newlandframework.rpc.parallel.policy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RejectedPolicyType {
	ABORT_POLICY("AbortPolicy"), BLOCKING_POLICY("BlockingPolicy"), CALLER_RUNS_POLICY("CallerRunsPolicy"), DISCARDED_POLICY("DiscardedPolicy"), REJECTED_POLICY("RejectedPolicy");

	private String value;

	public static RejectedPolicyType fromString(String value) {
		for (RejectedPolicyType type : RejectedPolicyType.values()) {
			if (type.getValue().equalsIgnoreCase(value.trim())) {
				return type;
			}
		}

		throw new IllegalArgumentException("Mismatched type with value=" + value);
	}
}
