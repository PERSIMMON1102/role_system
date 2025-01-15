package com.admin.controller;

import com.admin.entity.R;
import com.admin.entity.SysUser;
import com.admin.service.SysUserService;

import com.admin.util.JwtUtils;
import com.admin.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/test")
public class TestController {

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/user/list")
    public R userList(@RequestHeader(required = false)String token){
        if(StringUtil.isNotEmpty(token)){
            Map<String,Object> resutlMap=new HashMap<>();
            List<SysUser> userList = sysUserService.list();
            resutlMap.put("userList",userList);
            return R.ok(resutlMap);
        }else{
            return R.error(401,"没有权限访问");
        }

    }

    @RequestMapping("/login")
    public R login(){
        String token = JwtUtils.genJwtToken("java1234");
        return R.ok().put("token",token);
    }
}

