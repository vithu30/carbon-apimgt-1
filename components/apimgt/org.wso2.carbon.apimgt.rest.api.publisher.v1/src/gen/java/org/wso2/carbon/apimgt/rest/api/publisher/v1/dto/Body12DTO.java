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



public class Body12DTO   {
    private File file = null;    private String url = null;

    /**
    * WSDL file or archive to upload
    **/
    public Body12DTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "WSDL file or archive to upload")
    @JsonProperty("file")

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
    * WSDL Definition url
    **/
    public Body12DTO url(String url) {
        this.url = url;
        return this;
    }

  
    @ApiModelProperty(value = "WSDL Definition url")
    @JsonProperty("url")

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body12DTO body12 = (Body12DTO) o;
        return Objects.equals(file, body12.file) &&
        Objects.equals(url, body12.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, url);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body12DTO {\n");
        
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
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
