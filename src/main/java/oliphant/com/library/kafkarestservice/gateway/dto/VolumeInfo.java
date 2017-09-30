/**
 * Created by joshuaoliphant on 2/6/17.
 */

package oliphant.com.library.kafkarestservice.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VolumeInfo {

    private String pageCount;

    private String averageRating;

    private ReadingModes readingModes;

    private String infoLink;

    private String printType;

    private String allowAnonLogging;

    private String printedPageCount;

    private String publisher;

    private String[] authors;

    private String canonicalVolumeLink;

    private PanelizationSummary panelizationSummary;

    private String title;

    private String previewLink;

    private String ratingsCount;

    private String description;

    private ImageLinks imageLinks;

    private String subtitle;

    private String contentVersion;

    private String[] categories;

    private String language;

    private String publishedDate;

    private IndustryIdentifiers[] industryIdentifiers;

    private String maturityRating;

}
