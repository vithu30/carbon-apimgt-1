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



public class Body11DTO   {
    private File file = null;    private String inlineContent = null;    private String type = null;

    /**
    * Mediation Policy to upload
    **/
    public Body11DTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "Mediation Policy to upload")
    @JsonProperty("file")

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /**
    * Inline content of the Mediation Policy
    **/
    public Body11DTO inlineContent(String inlineContent) {
        this.inlineContent = inlineContent;
        return this;
    }

  
    @ApiModelProperty(value = "Inline content of the Mediation Policy")
    @JsonProperty("inlineContent")

    public String getInlineContent() {
        return inlineContent;
    }

    public void setInlineContent(String inlineContent) {
        this.inlineContent = inlineContent;
    }

    /**
    * Type of the mediation sequence(in/out/fault)
    **/
    public Body11DTO type(String type) {
        this.type = type;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "Type of the mediation sequence(in/out/fault)")
    @JsonProperty("type")
    @NotNull

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body11DTO body11 = (Body11DTO) o;
        return Objects.equals(file, body11.file) &&
        Objects.equals(inlineContent, body11.inlineContent) &&
        Objects.equals(type, body11.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, inlineContent, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body11DTO {\n");
        
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
        sb.append("    inlineContent: ").append(toIndentedString(inlineContent)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
