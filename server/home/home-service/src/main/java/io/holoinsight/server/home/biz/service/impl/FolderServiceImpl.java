/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.biz.service.impl;

import io.holoinsight.server.home.biz.service.FolderService;
import io.holoinsight.server.home.common.util.StringUtil;
import io.holoinsight.server.home.dal.mapper.FolderMapper;
import io.holoinsight.server.home.dal.model.Folder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: FolderServiceImpl.java, v 0.1 2022年05月23日 8:33 下午 jinsong.yjs Exp $
 */
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {

    @Override
    public List<Folder> getListByKeyword(String keyword, String tenant) {
        QueryWrapper<Folder> wrapper = new QueryWrapper<>();
        if (StringUtil.isNotBlank(tenant)) {
            wrapper.eq("tenant", tenant);
        }
        wrapper.like("id", keyword).or().like("name", keyword);
        Page<Folder> pluginPage = new Page<>(1, 20);
        pluginPage = page(pluginPage, wrapper);

        return pluginPage.getRecords();
    }

    @Override
    public List<Folder> getListByNameLike(String name, String tenant) {
        QueryWrapper<Folder> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant",tenant);
        wrapper.select().like("name",name);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Folder> findByIds(List<String> ids) {
        QueryWrapper<Folder> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Folder queryById(Long id, String tenant) {
        QueryWrapper<Folder> wrapper = new QueryWrapper<>();
        wrapper.eq("tenant", tenant);
        wrapper.eq("id", id);
        wrapper.last("LIMIT 1");

        return this.getOne(wrapper);
    }

    @Override
    public Long create(Folder folder) {
        folder.setGmtCreate(new Date());
        folder.setGmtModified(new Date());
        this.save(folder);
        return folder.getId();
    }

}