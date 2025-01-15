package com.admin.service;

import com.admin.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SysUserService extends IService<SysUser> {
    SysUser getByUsername(String username);
    String getUserAuthorityInfo(Long userId);
}
