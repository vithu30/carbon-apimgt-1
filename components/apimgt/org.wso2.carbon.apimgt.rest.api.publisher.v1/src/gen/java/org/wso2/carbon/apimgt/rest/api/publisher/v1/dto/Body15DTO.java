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



public class Body15DTO   {
    private File certificate = null;    private String alias = null;    private String endpoint = null;

    /**
    * The certificate that needs to be uploaded.
    **/
    public Body15DTO certificate(File certificate) {
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
    public Body15DTO alias(String alias) {
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
    * Endpoint to which the certificate should be applied.
    **/
    public Body15DTO endpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "Endpoint to which the certificate should be applied.")
    @JsonProperty("endpoint")
    @NotNull

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body15DTO body15 = (Body15DTO) o;
        return Objects.equals(certificate, body15.certificate) &&
        Objects.equals(alias, body15.alias) &&
        Objects.equals(endpoint, body15.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate, alias, endpoint);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body15DTO {\n");
        
        sb.append("    certificate: ").append(toIndentedString(certificate)).append("\n");
        sb.append("    alias: ").append(toIndentedString(alias)).append("\n");
        sb.append("    endpoint: ").append(toIndentedString(endpoint)).append("\n");
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
