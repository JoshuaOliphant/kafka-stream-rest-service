package oliphant.com.library.kafkarestservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.connect.errors.NotFoundException;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.StreamsMetadata;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Log4j2
@NoArgsConstructor
@Getter
@Named
public class MetaDataService {

    private KafkaStreams streams;

    @PostConstruct
    public void startKafkaStreams() throws IOException {
        Properties streamsConfiguration = new Properties();
        // Give the Streams application a unique name.  The name must be unique in the Kafka cluster
        // against which the application is run.
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "interactive-queries-example");
        streamsConfiguration.put(StreamsConfig.CLIENT_ID_CONFIG, "interactive-queries-example-client");
        // Where to find Kafka broker(s).
        //TODO: moved configs to yml file
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Provide the details of our embedded http service that we'll use to connect to this streams
        // instance and discover locations of stores.
        streamsConfiguration.put(StreamsConfig.APPLICATION_SERVER_CONFIG, "localhost:8080");
        final File example;
        try {
            example = Files.createTempDirectory(new File("/tmp").toPath(), "example").toFile();
        } catch (IOException e) {
            log.error("Unable to create file for state directory");
            throw new IOException("Unable to create file for state directory");
        }
        streamsConfiguration.put(StreamsConfig.STATE_DIR_CONFIG, example.getPath());

        streams = createStreams(streamsConfiguration);
        // Always (and unconditionally) clean local state prior to starting the processing topology.
        // We opt for this unconditional call here because this will make it easier for you to play around with the example
        // when resetting the application for doing a re-run (via the Application Reset Tool,
        // http://docs.confluent.io/current/streams/developer-guide.html#application-reset-tool).
        //
        // The drawback of cleaning up local state prior is that your app must rebuilt its local state from scratch, which
        // will take time and will require reading all the state-relevant data from the Kafka cluster over the network.
        // Thus in a production scenario you typically do not want to clean up always as we do here but rather only when it
        // is truly needed, i.e., only under certain conditions (e.g., the presence of a command line flag for your app).
        // See `ApplicationResetExample.java` for a production-like example.
        streams.cleanUp();
        // Now that we have finished the definition of the processing topology we can actually run
        // it via `start()`.  The Streams application as a whole can be launched just like any
        // normal Java application that has a `main()` method.
        streams.start();

        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                streams.close();
            } catch (Exception e) {
                // ignored
            }
        }));
    }

    private KafkaStreams createStreams(final Properties streamsConfiguration) {
        final Serde<String> stringSerde = Serdes.String();
        KStreamBuilder builder = new KStreamBuilder();
        KStream<String, String>
                textLines = builder.stream(stringSerde, stringSerde, "my-topic");

        log.info("textLines={}", textLines);

        final KGroupedStream<String, String> groupedByWord = textLines
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word, stringSerde, stringSerde);

        // Create a State Store for with the all time word count
        KTable<String, Long> count = groupedByWord.count("word-count");

        // Create a Windowed State Store that contains the word count for every
        // 1 minute
        groupedByWord.count(TimeWindows.of(60000), "windowed-word-count");

        return new KafkaStreams(builder, streamsConfiguration);
    }

    /**
     * Get the metadata for all of the instances of this Kafka Streams application
     *
     * @return List of {@link HostStoreInfo}
     */
    public List<HostStoreInfo> streamsMetadata() {
        // Get metadata for all of the instances of this Kafka Streams application
        final Collection<StreamsMetadata> metadata = streams.allMetadata();
        return mapInstancesToHostLibraryInfo(metadata);
    }

    /**
     * Get the metadata for all instances of this Kafka Streams application that currently
     * has the provided store.
     *
     * @param store The store to locate
     * @return List of {@link HostStoreInfo}
     */
    public List<HostStoreInfo> streamsMetadataForStore(final String store) {
        // Get metadata for all of the instances of this Kafka Streams application hosting the store
        final Collection<StreamsMetadata> metadata = streams.allMetadataForStore(store);
        return mapInstancesToHostLibraryInfo(metadata);
    }

    /**
     * Find the metadata for the instance of this Kafka Streams Application that has the given
     * store and would have the given key if it exists.
     *
     * @param store Store to find
     * @param key   The key to find
     * @return {@link HostStoreInfo}
     */
    public <K> HostStoreInfo streamsMetadataForStoreAndKey(final String store,
                                                           final K key,
                                                           final Serializer<K> serializer) {
        // Get metadata for the instances of this Kafka Streams application hosting the store and
        // potentially the value for key
        final StreamsMetadata metadata = streams.metadataForKey(store, key, serializer);
        if (metadata == null) {
            throw new NotFoundException("Metadata is null");
        }

        return new HostStoreInfo(metadata.host(),
                metadata.port(),
                metadata.stateStoreNames());
    }

    private List<HostStoreInfo> mapInstancesToHostLibraryInfo(
            final Collection<StreamsMetadata> metadatas) {
        return metadatas.stream().map(metadata -> new HostStoreInfo(metadata.host(),
                metadata.port(),
                metadata.stateStoreNames()))
                .collect(Collectors.toList());
    }
}
