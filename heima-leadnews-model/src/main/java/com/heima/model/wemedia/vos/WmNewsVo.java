package com.heima.model.wemedia.vos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.heima.model.wemedia.pojos.WmNews;
import lombok.Data;

@Data
public class WmNewsVo extends WmNews {
    /**
     * 作者名称
     */
    @TableField("author_name")
    private String authorName;
}
