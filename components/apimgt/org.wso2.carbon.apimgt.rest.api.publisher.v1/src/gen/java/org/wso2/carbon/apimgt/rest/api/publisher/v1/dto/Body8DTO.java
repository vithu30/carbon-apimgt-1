package org.wso2.carbon.apimgt.rest.api.publisher.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import javax.validation.constraints.*;

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.util.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;



public class Body8DTO   {
    private String schemaDefinition = null;

    /**
    * schema definition of the GraphQL API
    **/
    public Body8DTO schemaDefinition(String schemaDefinition) {
        this.schemaDefinition = schemaDefinition;
        return this;
    }

  
    @ApiModelProperty(required = true, value = "schema definition of the GraphQL API")
    @JsonProperty("schemaDefinition")
    @NotNull

    public String getSchemaDefinition() {
        return schemaDefinition;
    }

    public void setSchemaDefinition(String schemaDefinition) {
        this.schemaDefinition = schemaDefinition;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
    Body8DTO body8 = (Body8DTO) o;
        return Objects.equals(schemaDefinition, body8.schemaDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaDefinition);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Body8DTO {\n");
        
        sb.append("    schemaDefinition: ").append(toIndentedString(schemaDefinition)).append("\n");
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
