package com.fw.imbp.admin.entity;


import com.google.gson.annotations.SerializedName;

public class Payload {

    @SerializedName("time")
    private String created_day;
    private String device_type;
    private String label;

    private String created_time;
    private String board_id;
    private String board_loc;
    private String extension;
    private String file_name;
    private String product_type;
    private String image;

    public String getCreated_day() {
        return created_day;
    }

    public void setCreated_day(String created_day) {
        this.created_day = created_day;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getBoard_id() {
        return board_id;
    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
    }

    public String getBoard_loc() {
        return board_loc;
    }

    public void setBoard_loc(String board_loc) {
        this.board_loc = board_loc;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
