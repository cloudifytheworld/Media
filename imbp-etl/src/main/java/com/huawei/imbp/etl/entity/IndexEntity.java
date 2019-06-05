package com.huawei.imbp.etl.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Charles(Li) Cai
 * @date 4/8/2019
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexEntity {

    Long index;
    String system;
    String errorMsg;

}
