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



public class BodyDTO   {
    private String apiDefinition = null;    private String url = null;    private File file = null;

    /**
    * Swagger definition of the API
    **/
    public BodyDTO apiDefinition(String apiDefinition) {
        this.apiDefinition = apiDefinition;
        return this;
    }

  
    @ApiModelProperty(value = "Swagger definition of the API")
    @JsonProperty("apiDefinition")

    public String getApiDefinition() {
        return apiDefinition;
    }

    public void setApiDefinition(String apiDefinition) {
        this.apiDefinition = apiDefinition;
    }

    /**
    * Swagger definition URL of the API
    **/
    public BodyDTO url(String url) {
        this.url = url;
        return this;
    }

  
    @ApiModelProperty(value = "Swagger definition URL of the API")
    @JsonProperty("url")

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
    * Swagger definitio as a file
    **/
    public BodyDTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "Swagger definitio as a file")
    @JsonProperty("file")

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    BodyDTO body = (BodyDTO) o;
        return Objects.equals(apiDefinition, body.apiDefinition) &&
        Objects.equals(url, body.url) &&
        Objects.equals(file, body.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiDefinition, url, file);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class BodyDTO {\n");
        
        sb.append("    apiDefinition: ").append(toIndentedString(apiDefinition)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
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
