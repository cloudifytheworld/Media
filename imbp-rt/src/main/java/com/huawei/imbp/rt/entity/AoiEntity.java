package com.huawei.imbp.etl.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.nio.ByteBuffer;
import java.sql.Timestamp;

/**
 * @author Charles(Li) Cai
 * @date 5/13/2019
 */

@Table("aoi_single_component_image_1")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AoiEntity {

    @PrimaryKey
    private AoiKey keys;

    @Column(value="board_id")
    private String boardId;

    @Column(value="board_loc")
    private String boardLoc;
    @Column(value="file_name")
    private String fileName;
    @Column(value="product_type")
    private String productType;
    @Column
    private String extension;
    @Column
    private ByteBuffer image;

}
