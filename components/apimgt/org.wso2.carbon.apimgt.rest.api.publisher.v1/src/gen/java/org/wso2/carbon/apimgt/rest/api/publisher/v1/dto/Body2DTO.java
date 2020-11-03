package org.wso2.carbon.apimgt.rest.api.publisher.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.File;
import javax.validation.constraints.*;

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.util.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;



public class Body2DTO   {
    private File file = null;    private String url = null;    private String additionalProperties = null;

    /**
    * Definition to upload as a file
    **/
    public Body2DTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "Definition to upload as a file")
    @JsonProperty("file")

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
    * Definition url
    **/
    public Body2DTO url(String url) {
        this.url = url;
        return this;
    }

  
    @ApiModelProperty(value = "Definition url")
    @JsonProperty("url")

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
    * Additional attributes specified as a stringified JSON with API&#x27;s schema
    **/
    public Body2DTO additionalProperties(String additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

  
    @ApiModelProperty(value = "Additional attributes specified as a stringified JSON with API's schema")
    @JsonProperty("additionalProperties")

    public String getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(String additionalProperties) {
        this.additionalProperties = additionalProperties;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body2DTO body2 = (Body2DTO) o;
        return Objects.equals(file, body2.file) &&
        Objects.equals(url, body2.url) &&
        Objects.equals(additionalProperties, body2.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, url, additionalProperties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body2DTO {\n");
        
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
    * Convert the given object to string with each line indented by 4 spaces
    * (except the first line).
    */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
        return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
