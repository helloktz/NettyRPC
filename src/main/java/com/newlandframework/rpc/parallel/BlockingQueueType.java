package com.newlandframework.rpc.parallel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum BlockingQueueType {
	LINKED_BLOCKING_QUEUE("LinkedBlockingQueue"), ARRAY_BLOCKING_QUEUE("ArrayBlockingQueue"), SYNCHRONOUS_QUEUE("SynchronousQueue");

	private String value;

	public static BlockingQueueType fromString(String value) {
		for (BlockingQueueType type : BlockingQueueType.values()) {
			if (type.getValue().equalsIgnoreCase(value.trim())) {
				return type;
			}
		}

		throw new IllegalArgumentException("Mismatched type with value=" + value);
	}

}
