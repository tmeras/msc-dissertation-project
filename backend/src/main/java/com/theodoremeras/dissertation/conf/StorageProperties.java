package com.theodoremeras.dissertation.conf;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
@Getter
public class StorageProperties {

    // Folder location for uploading files
    private final String location = "uploaded-evidence";

}
