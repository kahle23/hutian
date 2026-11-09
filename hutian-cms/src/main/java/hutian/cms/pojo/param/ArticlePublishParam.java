package hutian.cms.pojo.param;

import kunlun.data.definition.annotation.FieldMeta;
import kunlun.data.json.support.jackson.annotation.JsonSceneDeserialize;
import kunlun.data.json.support.jackson.model.Scene;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 内容发布操作的入参对象（立即发布/定时发布/重新发布三合一）.
 *
 * @author Hutian
 * @since 2026-11-06
 */
@Data
@NoArgsConstructor
public class ArticlePublishParam implements Serializable {

    /**
     * 内容ID列表（批量）
     */
    @NotEmpty(message = "请选择要发布的内容！")
    @JsonSceneDeserialize(Scene.SINGLE_TO_LIST)
    @FieldMeta(description = "内容ID列表")
    private List<Long> ids;
    /**
     * 发布时间；传未来时间表示定时发布（保持草稿，由定时任务到点发布），为空表示立即发布
     */
    @FieldMeta(description = "发布时间；传未来时间表示定时发布，为空表示立即发布")
    private Date publishTime;
    /**
     * 下线时间（有效期截止）；为空表示长期有效
     */
    @FieldMeta(description = "下线时间（有效期截止）；为空表示长期有效")
    private Date expireTime;

}
