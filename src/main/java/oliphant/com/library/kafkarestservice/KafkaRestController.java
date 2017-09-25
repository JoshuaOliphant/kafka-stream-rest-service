package oliphant.com.library.kafkarestservice;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.errors.NotFoundException;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/state")
public class KafkaRestController {

    private final MetaDataService metadataService;

    @Inject
    public KafkaRestController(final MetaDataService metaDataService) {
        this.metadataService = metaDataService;
    }

    /**
     * Get a key-value pair from a KeyValue Store
     * @param storeName   the store to look in
     * @param key         the key to get
     * @return {@link KeyValue} representing the key-value pair
     */
    @RequestMapping(method = RequestMethod.GET, value = "/keyvalue/{storeName}/{key}")
    public KeyValue byKey(@PathVariable("storeName") final String storeName,
                              @PathVariable("key") final String key) {

        // Lookup the KeyValueStore with the provided storeName
        final ReadOnlyKeyValueStore<String, Long> store = metadataService.getStreams()
                .store(storeName, QueryableStoreTypes.<String, Long>keyValueStore());
        if (store == null) {
            throw new NotFoundException("Could not find KeyValueStore with storeName=" + storeName);
        }

        // Get the value from the store
        final Long value = store.get(key);
        if (value == null) {
            throw new NotFoundException("Could not retrieve value from store with key=" + value);
        }
        return new KeyValue(key, value);
    }

    /**
     * Get all of the key-value pairs available in a store
     * @param storeName   store to query
     * @return A List of {@link KeyValue}s representing all of the key-values in the provided
     * store
     */
    @RequestMapping(method = RequestMethod.GET, value = "/keyvalues/{storeName}/all")
    public List<KeyValue> allForStore(@PathVariable("storeName") final String storeName) {
        log.info("storeName={}", storeName);
        return rangeForKeyValueStore(storeName, ReadOnlyKeyValueStore::all);
    }


    /**
     * Get all of the key-value pairs that have keys within the range from...to
     * @param storeName   store to query
     * @param from        start of the range (inclusive)
     * @param to          end of the range (inclusive)
     * @return A List of {@link KeyValue}s representing all of the key-values in the provided
     * store that fall withing the given range.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/keyvalues/{storeName}/range/{from}/{to}")
    public List<KeyValue> keyRangeForStore(@PathVariable("storeName") final String storeName,
                                               @PathVariable("from") final String from,
                                               @PathVariable("to") final String to) {
        return rangeForKeyValueStore(storeName, store -> store.range(from, to));
    }

    /**
     * Query a window store for key-value pairs representing the value for a provided key within a
     * range of windows
     * @param storeName   store to query
     * @param key         key to look for
     * @param from        time of earliest window to query
     * @param to          time of latest window to query
     * @return A List of {@link KeyValue}s representing the key-values for the provided key
     * across the provided window range.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/windowed/{storeName}/{key}/{from}/{to}")
    public List<KeyValue> windowedByKey(@PathVariable("storeName") final String storeName,
                                            @PathVariable("key") final String key,
                                            @PathVariable("from") final Long from,
                                            @PathVariable("to") final Long to) {

        // Lookup the WindowStore with the provided storeName
        final ReadOnlyWindowStore<String, Long> store = metadataService.getStreams().store(storeName,
                QueryableStoreTypes.<String, Long>windowStore());
        if (store == null) {
            throw new NotFoundException("Store is null");
        }

        // fetch the window results for the given key and time range
        final WindowStoreIterator<Long> results = store.fetch(key, from, to);

        final List<KeyValue> windowResults = new ArrayList<>();
        while (results.hasNext()) {
            final KeyValue<Long, Long> next = results.next();
            // convert the result to have the window time and the key (for display purposes)
            windowResults.add(new KeyValue(key + "@" + next.key, next.value));
        }
        return windowResults;
    }

    /**
     * Get the metadata for all of the instances of this Kafka Streams application
     * @return List of {@link HostStoreInfo}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/instances")
    public List<HostStoreInfo> streamsMetadata() {
        return metadataService.streamsMetadata();
    }

    /**
     * Get the metadata for all instances of this Kafka Streams application that currently
     * has the provided store.
     * @param store   The store to locate
     * @return  List of {@link HostStoreInfo}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/instances/{storeName}")
    public List<HostStoreInfo> streamsMetadataForStore(@PathVariable("storeName") String store) {
        return metadataService.streamsMetadataForStore(store);
    }

    /**
     * Find the metadata for the instance of     this Kafka Streams Application that has the given
     * store and would have the given key if it exists.
     * @param store   Store to find
     * @param key     The key to find
     * @return {@link HostStoreInfo}
     */
    @RequestMapping(method = RequestMethod.GET, value = "/instance/{storeName}/{key}")
    public HostStoreInfo streamsMetadataForStoreAndKey(@PathVariable("storeName") String store,
                                                       @PathVariable("key") String key) {
        return metadataService.streamsMetadataForStoreAndKey(store, key, new StringSerializer());
    }

    /**
     * Performs a range query on a KeyValue Store and converts the results into a List of
     * {@link KeyValue}
     * @param storeName       The store to query
     * @param rangeFunction   The range query to run, i.e., all, from(start, end)
     * @return  List of {@link KeyValue}
     */
    private List<KeyValue> rangeForKeyValueStore(final String storeName,
                                                     final Function<ReadOnlyKeyValueStore<String, Long>,
                                                                                                                  KeyValueIterator<String, Long>> rangeFunction) {

        // Get the KeyValue Store
        final ReadOnlyKeyValueStore<String, Long> store = metadataService
                .getStreams()
                .store(storeName, QueryableStoreTypes.keyValueStore());
        if (store == null) {
            throw new NotFoundException("Store is null");
        }

        final List<KeyValue> results = new ArrayList<>();
        // Apply the function, i.e., query the store
        final KeyValueIterator<String, Long> range = rangeFunction.apply(store);

        // Convert the results
        while (range.hasNext()) {
            final KeyValue<String, Long> next = range.next();
            results.add(new KeyValue(next.key, next.value));
        }

        return results;
    }
}
