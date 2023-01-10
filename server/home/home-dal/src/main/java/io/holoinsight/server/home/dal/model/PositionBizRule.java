/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * bizops错误码规则对照表
 */
@Data
@Table(name = "position_biz_rule")
public class PositionBizRule {

    private static final long serialVersionUID = 1804705751152364751L;

    /**
     * id：主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @Column(name = "gmt_modified")
    private Date gmtModified;

    /**
     * 账号的下的站点信息
     */
    @Column(name = "app_id")
    public String appId;

    /**
     * 环境
     */
    @Column(name = "env_id")
    public String envId;

    /**
     * 服务名
     */
    @Column(name = "app_name")
    public String appName;

    /**
     * 接口类型：这里分为Rpc类型和Http类型
     */
    @Column(name = "interface_type")
    private String interfaceType;

    /**
     * 服务接口
     */
    @Column(name = "interface_name")
    private String interfaceName;

    /**
     * 返回值类型：只有Http服务类型才会用到该字段，其中Return表示直接从返回值中取，ModelMap表示从ModelMap中取
     */
    @Column(name = "response_type")
    private String responseType;

    /**
     * 从modelmap里取的属性，只有选择ModelMap才需要该字段
     */
    @Column(name = "response_property")
    private String responseProperty;

    /**
     * json字符串
     * 格式见
     * {\"it\":\"interfaceType\",\"im\":\"interfaceName\",\"rt\":\"responseType\",\"rpt\":\"responseProperty\",\"go\":\"globalOpen\",\"bm\":\"bizResultMvel\",\"em\":\"errorCodeMvel\"}
     */
    @Column(name = "error_code_config")
    private String errorCodeConfig;

    /**
     * 规则全局生效与否，T代表生效、F代表不生效
     */
    @Column(name = "global_open")
    private String globalOpen;
}
