package com.github.olypolyu.betterwands.util;

import net.minecraft.core.util.collection.NamespaceID;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

public class Signal<T> {

	protected final Map<NamespaceID, Consumer<T>> signalHandlers = new HashMap<>();

	public void emit(T value) {
		signalHandlers.values().forEach(it -> it.accept(value));
	}

	public void connect(NamespaceID namespaceID, Consumer<T> handler) {
		if (signalHandlers.containsKey(namespaceID)) {
			throw new RuntimeException("Attempted to connect to signal with non unique identifier!");
		}

		signalHandlers.put(namespaceID, handler);
	}

	public void remove(NamespaceID namespaceID) {
		signalHandlers.remove(namespaceID);
	}

	public HashSet<NamespaceID> getConsumers() {
		return new HashSet<>(signalHandlers.keySet());
	}
}
