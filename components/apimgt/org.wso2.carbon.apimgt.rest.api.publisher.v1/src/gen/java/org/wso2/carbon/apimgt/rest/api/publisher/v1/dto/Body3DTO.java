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



public class Body3DTO   {
    private File file = null;    private String url = null;    private String additionalProperties = null;
        @XmlType(name="ImplementationTypeEnum")
        @XmlEnum(String.class)
        public enum ImplementationTypeEnum {
            SOAPTOREST("SOAPTOREST"),
            SOAP("SOAP");
            private String value;
    
            ImplementationTypeEnum (String v) {
                value = v;
            }
    
            public String value() {
                return value;
            }
    
            @Override
            public String toString() {
                return String.valueOf(value);
            }
    
            @JsonCreator
            public static ImplementationTypeEnum fromValue(String v) {
                for (ImplementationTypeEnum b : ImplementationTypeEnum.values()) {
                    if (String.valueOf(b.value).equals(v)) {
                        return b;
                    }
                }
    return null;
            }
        }    private ImplementationTypeEnum implementationType = ImplementationTypeEnum.SOAP;

    /**
    * WSDL definition as a file
    **/
    public Body3DTO file(File file) {
        this.file = file;
        return this;
    }

  
    @ApiModelProperty(value = "WSDL definition as a file")
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
    public Body3DTO url(String url) {
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

    /**
    * Additional attributes specified as a stringified JSON with API&#x27;s schema
    **/
    public Body3DTO additionalProperties(String additionalProperties) {
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

    /**
    * If &#x27;SOAP&#x27; is specified, the API will be created with only one resource &#x27;POST /_*&#x27; which is to be used for SOAP operations.  If &#x27;HTTP_BINDING&#x27; is specified, the API will be created with resources using HTTP binding operations which are extracted from the WSDL. 
    **/
    public Body3DTO implementationType(ImplementationTypeEnum implementationType) {
        this.implementationType = implementationType;
        return this;
    }

  
    @ApiModelProperty(value = "If 'SOAP' is specified, the API will be created with only one resource 'POST /_*' which is to be used for SOAP operations.  If 'HTTP_BINDING' is specified, the API will be created with resources using HTTP binding operations which are extracted from the WSDL. ")
    @JsonProperty("implementationType")

    public ImplementationTypeEnum getImplementationType() {
        return implementationType;
    }

    public void setImplementationType(ImplementationTypeEnum implementationType) {
        this.implementationType = implementationType;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body3DTO body3 = (Body3DTO) o;
        return Objects.equals(file, body3.file) &&
        Objects.equals(url, body3.url) &&
        Objects.equals(additionalProperties, body3.additionalProperties) &&
        Objects.equals(implementationType, body3.implementationType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, url, additionalProperties, implementationType);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body3DTO {\n");
        
        sb.append("    file: ").append(toIndentedString(file)).append("\n");
        sb.append("    url: ").append(toIndentedString(url)).append("\n");
        sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
        sb.append("    implementationType: ").append(toIndentedString(implementationType)).append("\n");
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
