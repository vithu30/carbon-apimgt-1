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



public class Body10DTO   {
    private File mediationPolicyFile = null;    private String inlineContent = null;    private String type = null;

    /**
    * Mediation Policy to upload
    **/
    public Body10DTO mediationPolicyFile(File mediationPolicyFile) {
        this.mediationPolicyFile = mediationPolicyFile;
        return this;
    }

  
    @ApiModelProperty(value = "Mediation Policy to upload")
    @JsonProperty("mediationPolicyFile")

    public File getMediationPolicyFile() {
        return mediationPolicyFile;
    }

    public void setMediationPolicyFile(File mediationPolicyFile) {
        this.mediationPolicyFile = mediationPolicyFile;
    }

    /**
    * Inline content of the Mediation Policy
    **/
    public Body10DTO inlineContent(String inlineContent) {
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
    * Type of the mediation sequence
    **/
    public Body10DTO type(String type) {
        this.type = type;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "Type of the mediation sequence")
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
    Body10DTO body10 = (Body10DTO) o;
        return Objects.equals(mediationPolicyFile, body10.mediationPolicyFile) &&
        Objects.equals(inlineContent, body10.inlineContent) &&
        Objects.equals(type, body10.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mediationPolicyFile, inlineContent, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body10DTO {\n");
        
        sb.append("    mediationPolicyFile: ").append(toIndentedString(mediationPolicyFile)).append("\n");
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
