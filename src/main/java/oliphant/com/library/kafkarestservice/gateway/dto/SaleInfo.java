/**
 * Created by joshuaoliphant on 2/6/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaleInfo {

    private RetailPrice retailPrice;

    private String saleability;

    private ListPrice listPrice;

    private Offers[] offers;

    private String buyLink;

    private String isEbook;

    private String country;
}
