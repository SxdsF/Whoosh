package com.sxdsf.whoosh.impl;

import com.sxdsf.whoosh.Theme;
import com.sxdsf.whoosh.Filter;
import com.sxdsf.whoosh.info.Message;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StorageUnit
 *
 * @author sunbowen
 * @date 2016/5/17-14:58
 * @desc 存储单元
 */
class StorageUnit {
	public final Map<UUID, List<Theme<Message>>> themesMapper = new ConcurrentHashMap<>();
	public final Map<Theme<Message>, List<Filter>> filtersMapper = new ConcurrentHashMap<>();
}
