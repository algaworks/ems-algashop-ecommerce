package com.algaworks.algashop.ecommerce.application.model.client;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.groovy.util.StringUtil;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalInfoModel {

    @NotBlank
    private String fullName;

    @NotBlank
    private String document;

    @NotBlank
    private String phone;

    @NotNull
    @Valid
    private AddressModel address;

    public String getAddressLine() {
        return address.getAddressLine();
    }

}