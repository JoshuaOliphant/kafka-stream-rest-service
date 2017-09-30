/*
 * Created by joshuaoliphant on 4/22/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class Volumes implements Serializable{

    private String kind;

    private int totalItems;

    List<Volume> items;
}
