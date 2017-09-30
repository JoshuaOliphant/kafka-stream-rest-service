/**
 * Created by joshuaoliphant on 2/6/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Offers {

    private RetailPrice retailPrice;

    private ListPrice listPrice;

    private String finskyOfferType;

    private String giftable;
}
