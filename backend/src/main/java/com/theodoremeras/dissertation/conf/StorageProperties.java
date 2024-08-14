package com.theodoremeras.dissertation.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
@Getter
public class StorageProperties {

    // Folder location for uploading files
    private String location = "uploaded-evidence";

}
