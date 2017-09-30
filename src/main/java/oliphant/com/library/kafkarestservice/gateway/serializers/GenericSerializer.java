package oliphant.com.library.kafkarestservice.gateway.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import javax.inject.Named;
import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Named
public class GenericSerializer<T extends Serializable> implements Serializer<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public GenericSerializer() {}

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, T data) {
        log.debug("Data to serialize={}", data);
        if (isNull(data)) {
            log.error("Sending null to topic={}", topic);
            return new byte[0];
        } else {
            try {
                return OBJECT_MAPPER.writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new SerializationException("Unable to serialized message", e);
            }
        }
    }

    @Override
    public void close() {

    }
}
