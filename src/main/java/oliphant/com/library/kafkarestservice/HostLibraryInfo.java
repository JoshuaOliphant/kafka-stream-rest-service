package oliphant.com.library.kafkarestservice;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class HostLibraryInfo {

    private String host;
    private int port;
    private Set<String> libraryNames;
}
