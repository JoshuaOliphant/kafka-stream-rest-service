/**
 * Created by joshuaoliphant on 2/6/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessInfo {

    private String webReaderLink;

    private String textToSpeechPermission;

    private String publicDomain;

    private String viewability;

    private String accessViewStatus;

    private Pdf pdf;

    private Epub epub;

    private String embeddable;

    private String quoteSharingAllowed;

    private String country;
}
