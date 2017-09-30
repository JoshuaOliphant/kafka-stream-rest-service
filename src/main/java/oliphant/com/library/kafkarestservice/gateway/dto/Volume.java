/**
 * Created by joshuaoliphant on 2/6/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Volume {

    private SaleInfo saleInfo;

    private String id;

    private String etag;

    private VolumeInfo volumeInfo;

    private LayerInfo layerInfo;

    private String selfLink;

    private AccessInfo accessInfo;

    private String kind;
}
