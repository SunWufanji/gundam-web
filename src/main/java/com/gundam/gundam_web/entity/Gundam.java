package com.gundam.gundam_web.entity;
import lombok.Data;

@Data
public class Gundam {
    private Integer id;
    private String name;
    private String series;
    private String grade; // HG, MG, RG, PG
    private String imageUrl;
    private String description;
    
    // 新增详细参数
    private String pilot;
    private String height;
    private String weight;
    private Integer price;      // 价格
    private String releaseDate; // 发售日
    private String manualUrl;   // 说明书图片/PDF链接
}