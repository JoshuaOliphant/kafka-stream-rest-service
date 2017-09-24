package oliphant.com.library.kafkarestservice;

import org.apache.kafka.connect.errors.NotFoundException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.StreamsMetadata;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MetaDataService {

    private final KafkaStreams streams;

    public MetaDataService(KafkaStreams streams) {
        this.streams = streams;
    }

    /**
     * Get the metadata for all of the instances of this Kafka Streams application
     * @return List of {@link HostLibraryInfo}
     */
    public List<HostLibraryInfo> streamsMetadata() {
        // Get metadata for all of the instances of this Kafka Streams application
        final Collection<StreamsMetadata> metadata = streams.allMetadata();
        return mapInstancesToHostLibraryInfo(metadata);
    }

    /**
     * Get the metadata for all instances of this Kafka Streams application that currently
     * has the provided store.
     * @param store   The store to locate
     * @return  List of {@link HostLibraryInfo}
     */
    public List<HostLibraryInfo> streamsMetadataForStore(final  String store) {
        // Get metadata for all of the instances of this Kafka Streams application hosting the store
        final Collection<StreamsMetadata> metadata = streams.allMetadataForStore(store);
        return mapInstancesToHostLibraryInfo(metadata);
    }

    /**
     * Find the metadata for the instance of this Kafka Streams Application that has the given
     * store and would have the given key if it exists.
     * @param store   Store to find
     * @param key     The key to find
     * @return {@link HostLibraryInfo}
     */
    public <K> HostLibraryInfo streamsMetadataForStoreAndKey(final String store,
                                                           final K key,
                                                           final Serializer<K> serializer) {
        // Get metadata for the instances of this Kafka Streams application hosting the store and
        // potentially the value for key
        final StreamsMetadata metadata = streams.metadataForKey(store, key, serializer);
        if (metadata == null) {
            throw new NotFoundException();
        }

        return new HostLibraryInfo(metadata.host(),
                metadata.port(),
                metadata.stateStoreNames());
    }

    private List<HostLibraryInfo> mapInstancesToHostLibraryInfo(
            final Collection<StreamsMetadata> metadatas) {
        return metadatas.stream().map(metadata -> new HostLibraryInfo(metadata.host(),
                metadata.port(),
                metadata.stateStoreNames()))
                .collect(Collectors.toList());
    }
}
