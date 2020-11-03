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



public class Body13DTO   {
    private File certificate = null;    private String alias = null;    private String tier = null;

    /**
    * The certificate that needs to be uploaded.
    **/
    public Body13DTO certificate(File certificate) {
        this.certificate = certificate;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "The certificate that needs to be uploaded.")
    @JsonProperty("certificate")
    @NotNull

    public File getCertificate() {
        return certificate;
    }

    public void setCertificate(File certificate) {
        this.certificate = certificate;
    }

    /**
    * Alias for the certificate
    **/
    public Body13DTO alias(String alias) {
        this.alias = alias;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "Alias for the certificate")
    @JsonProperty("alias")
    @NotNull
 @Size(min=1,max=30)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
    * api tier to which the certificate should be applied.
    **/
    public Body13DTO tier(String tier) {
        this.tier = tier;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "api tier to which the certificate should be applied.")
    @JsonProperty("tier")
    @NotNull

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
    Body13DTO body13 = (Body13DTO) o;
        return Objects.equals(certificate, body13.certificate) &&
        Objects.equals(alias, body13.alias) &&
        Objects.equals(tier, body13.tier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate, alias, tier);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body13DTO {\n");
        
        sb.append("    certificate: ").append(toIndentedString(certificate)).append("\n");
        sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
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
