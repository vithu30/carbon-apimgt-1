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



public class Body14DTO   {
    private File certificate = null;    private String tier = null;

    /**
    * The certificate that needs to be uploaded.
    **/
    public Body14DTO certificate(File certificate) {
        this.certificate = certificate;
        return this;
    }

  
    @ApiModelProperty(value = "The certificate that needs to be uploaded.")
    @JsonProperty("certificate")

    public File getCertificate() {
        return certificate;
    }

    public void setCertificate(File certificate) {
        this.certificate = certificate;
    }

    /**
    * The tier of the certificate
    **/
    public Body14DTO tier(String tier) {
        this.tier = tier;
        return this;
    }

  
    @ApiModelProperty(value = "The tier of the certificate")
    @JsonProperty("tier")

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body14DTO body14 = (Body14DTO) o;
        return Objects.equals(certificate, body14.certificate) &&
        Objects.equals(tier, body14.tier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate, tier);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body14DTO {\n");
        
        sb.append("    certificate: ").append(toIndentedString(certificate)).append("\n");
        sb.append("    tier: ").append(toIndentedString(tier)).append("\n");
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
