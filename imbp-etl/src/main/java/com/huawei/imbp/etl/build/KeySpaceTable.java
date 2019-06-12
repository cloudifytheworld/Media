package com.huawei.imbp.etl.build;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Charles(Li) Cai
 * @date 6/11/2019
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KeySpaceTable {

    String keySpace;
    String table;
}
