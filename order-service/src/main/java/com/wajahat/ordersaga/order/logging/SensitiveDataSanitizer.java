package com.wajahat.ordersaga.order.logging;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SensitiveDataSanitizer {
    private static final String MASK = "****";
    private static final Set<String> SENSITIVE_FIELDS = Set.of("password", "token", "authorization", "secret");

    public Object sanitize(Object value) {
        if (value == null || isSimpleValue(value)) {
            return value;
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> String.valueOf(entry.getKey()),
                            entry -> isSensitive(String.valueOf(entry.getKey())) ? MASK : sanitize(entry.getValue())
                    ));
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream().map(this::sanitize).toList();
        }
        if (value.getClass().isArray()) {
            return Arrays.stream((Object[]) value).map(this::sanitize).toList();
        }
        if (value instanceof Throwable throwable) {
            return throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
        }
        if (value.getClass().isRecord()) {
            return sanitizeRecord(value);
        }
        return value.toString();
    }

    private Map<String, Object> sanitizeRecord(Object record) {
        return Arrays.stream(record.getClass().getRecordComponents())
                .collect(Collectors.toMap(
                        RecordComponent::getName,
                        component -> isSensitive(component.getName()) ? MASK : readComponent(record, component)
                ));
    }

    private Object readComponent(Object record, RecordComponent component) {
        try {
            return sanitize(component.getAccessor().invoke(record));
        } catch (ReflectiveOperationException exception) {
            return "<unavailable>";
        }
    }

    private boolean isSimpleValue(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum<?>;
    }

    private boolean isSensitive(String fieldName) {
        return SENSITIVE_FIELDS.contains(fieldName.toLowerCase());
    }
}
