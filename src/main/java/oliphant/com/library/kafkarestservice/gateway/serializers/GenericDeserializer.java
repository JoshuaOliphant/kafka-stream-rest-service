package oliphant.com.library.kafkarestservice.gateway.serializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@Named
public class GenericDeserializer<T extends Serializable>  implements Deserializer<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Class<T> deserializedClass;

    public GenericDeserializer() {}

    public GenericDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        if(deserializedClass == null)
            deserializedClass = (Class<T>) configs.get("GenericClass");
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if(isNull(data)) {
            log.error("Received null bytes from topic={}", topic);
            return null;
        } else {
            try {
                T readValue = OBJECT_MAPPER.readValue(data, deserializedClass);
                log.info("deserialized object={}", readValue);
                return readValue;
            } catch (JsonParseException e) {
                log.error("Error parsing data to deserialize from topic={}, exception=e", topic, e);
            } catch (JsonMappingException e) {
                log.error("Error mapping data to deserialize from topic={}, exception=e", topic, e);
            } catch (IOException e) {
                log.error("IO issues while trying to deserialize from topic={}, exception=e", topic, e);
            }
        }
        return null;
    }

    @Override
    public void close() {

    }
}
