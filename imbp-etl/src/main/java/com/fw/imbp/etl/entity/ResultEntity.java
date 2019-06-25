package com.fw.imbp.etl.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Charles(Li) Cai
 * @date 4/29/2019
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultEntity {

    String status;
    String message;
    List<String> ids;
    String id;
}
