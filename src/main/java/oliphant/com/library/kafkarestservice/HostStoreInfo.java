package oliphant.com.library.kafkarestservice;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class HostStoreInfo {

    private String host;
    private int port;
    private Set<String> storeNames;
}
