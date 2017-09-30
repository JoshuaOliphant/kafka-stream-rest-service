/**
 * Created by joshuaoliphant on 2/6/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageLinks {

    private String thumbnail;

    private String extraLarge;

    private String smallThumbnail;

    private String small;

    private String large;

    private String medium;
}
