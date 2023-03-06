/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.common.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.holoinsight.server.common.dao.entity.Workspace;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author jsy1001de
 * @version 1.0: WorkspaceMapper.java, v 0.1 2023年03月03日 下午5:56 jsy1001de Exp $
 */
@Mapper
@Repository
public interface WorkspaceMapper extends BaseMapper<Workspace> {
}
