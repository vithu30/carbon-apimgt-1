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



public class Body6DTO   {
    private String url = null;    private File file = null;

    /**
    * Definition url
    **/
    public Body6DTO url(String url) {
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
    * Definition to upload as a file
    **/
    public Body6DTO file(File file) {
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


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body6DTO body6 = (Body6DTO) o;
        return Objects.equals(url, body6.url) &&
        Objects.equals(file, body6.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, file);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body6DTO {\n");
        
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
