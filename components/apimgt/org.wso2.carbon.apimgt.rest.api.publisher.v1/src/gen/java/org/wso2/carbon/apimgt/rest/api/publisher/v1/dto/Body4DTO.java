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



public class Body4DTO   {
    private String type = null;    private File file = null;    private String additionalProperties = null;

    /**
    * Definition type to upload
    **/
    public Body4DTO type(String type) {
        this.type = type;
        return this;
    }

  
    @ApiModelProperty(value = "Definition type to upload")
    @JsonProperty("type")

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
    * Definition to uploads a file
    **/
    public Body4DTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "Definition to uploads a file")
    @JsonProperty("file")

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
    * Additional attributes specified as a stringified JSON with API&#x27;s schema
    **/
    public Body4DTO additionalProperties(String additionalProperties) {
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
    Body4DTO body4 = (Body4DTO) o;
        return Objects.equals(type, body4.type) &&
        Objects.equals(file, body4.file) &&
        Objects.equals(additionalProperties, body4.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, file, additionalProperties);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body4DTO {\n");
        
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
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
