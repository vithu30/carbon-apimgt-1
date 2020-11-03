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



public class Body9DTO   {
    private File file = null;    private String inlineContent = null;

    /**
    * Document to upload
    **/
    public Body9DTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "Document to upload")
    @JsonProperty("file")

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
    * Inline content of the document
    **/
    public Body9DTO inlineContent(String inlineContent) {
        this.inlineContent = inlineContent;
        return this;
    }

  
    @ApiModelProperty(value = "Inline content of the document")
    @JsonProperty("inlineContent")

    public String getInlineContent() {
        return inlineContent;
    }

    public void setInlineContent(String inlineContent) {
        this.inlineContent = inlineContent;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body9DTO body9 = (Body9DTO) o;
        return Objects.equals(file, body9.file) &&
        Objects.equals(inlineContent, body9.inlineContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, inlineContent);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body9DTO {\n");
        
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
        sb.append("    inlineContent: ").append(toIndentedString(inlineContent)).append("\n");
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
