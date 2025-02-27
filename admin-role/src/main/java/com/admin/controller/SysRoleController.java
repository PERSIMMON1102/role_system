package com.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.admin.entity.*;
import com.admin.service.SysRoleMenuService;
import com.admin.service.SysRoleService;
import com.admin.service.SysUserRoleService;
import com.admin.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统角色Controller控制器
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @GetMapping("/listAll")
    @PreAuthorize("hasAuthority('system:role:query')")
    public R listAll(){
        Map<String,Object> resultMap=new HashMap<>();
        List<SysRole> roleList = sysRoleService.list();
        resultMap.put("roleList",roleList);
        return R.ok(resultMap);
    }

    /**
     * 根据条件分页查询角色信息
     * @param pageBean
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:role:query')")
    public R list(@RequestBody PageBean pageBean){
        String query=pageBean.getQuery().trim();
        Page<SysRole> pageResult = sysRoleService.page(new Page<>(pageBean.getPageNum(), pageBean.getPageSize()),new QueryWrapper<SysRole>().like(StringUtil.isNotEmpty(query),"name",query));
        List<SysRole> roleList = pageResult.getRecords();
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("roleList",roleList);
        resultMap.put("total",pageResult.getTotal());
        return R.ok(resultMap);
    }

    /**
     * 添加或者修改
     * @param sysRole
     * @return
     */
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('system:role:add')"+"||"+"hasAuthority('system:role:edit')")
    public R save(@RequestBody SysRole sysRole){
        if(sysRole.getId()==null || sysRole.getId()==-1){
            sysRole.setCreateTime(new Date());
            sysRoleService.save(sysRole);
        }else{
            sysRole.setUpdateTime(new Date());
            sysRoleService.updateById(sysRole);
        }
        return R.ok();
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public R findById(@PathVariable(value = "id")Integer id){
        SysRole sysRole = sysRoleService.getById(id);
        Map<String,Object> map=new HashMap<>();
        map.put("sysRole",sysRole);
        return R.ok(map);
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public R delete(@RequestBody Long[] ids){
        sysRoleService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id",ids));
        return R.ok();
    }

    /**
     * 获取当前角色的权限菜单ID集合
     * @param id
     * @return
     */
    @GetMapping("/menus/{id}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public R menus(@PathVariable(value = "id")Integer id){
        List<SysRoleMenu> roleMenuList = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        List<Long> menuIdList = roleMenuList.stream().map(p -> p.getMenuId()).collect(Collectors.toList());
        return R.ok().put("menuIdList",menuIdList);
    }

    /**
     * 更新角色权限信息
     * @param id
     * @param menuIds
     * @return
     */
    @Transactional
    @PostMapping("/updateMenus/{id}")
    @PreAuthorize("hasAuthority('system:role:menu')")
    public R updateMenus(@PathVariable(value = "id")Long id, @RequestBody Long[] menuIds){
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",id));
        List<SysRoleMenu> sysRoleMenuList=new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId->{
            SysRoleMenu roleMenu=new SysRoleMenu();
            roleMenu.setRoleId(id);
            roleMenu.setMenuId(menuId);
            sysRoleMenuList.add(roleMenu);
        });
        sysRoleMenuService.saveBatch(sysRoleMenuList);
        return R.ok();
    }

}