package com.example.ShardedSagaWallet.services.saga;

import lombok.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SagaContext {

    private Map<String,Object> data = new HashMap<>();

    public SagaContext(Map<String,Object> data) {
        this.data = data !=null ? data : new HashMap<>();
    }

    public void put(String key,Object value) {
        data.put(key,value);
    }
    public Object get(String key)
    {
        return data.get(key);
    }

    public Long getLong(String key) {
        Object value = get(key);
        if(value instanceof Number) {
            // if it is a instance of number then first type case to number and then to long value
            return ((Number)value).longValue();
        }
        // if not a instance of number then return null.
        return null;
    }

    public BigDecimal getBigDecimal(String key) {
        Object value = get(key);
        if(value instanceof Number) {
            return BigDecimal.valueOf(((Number)value).doubleValue());
        }
        return null;
    }
}
