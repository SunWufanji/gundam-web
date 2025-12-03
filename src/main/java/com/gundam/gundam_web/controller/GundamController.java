package com.gundam.gundam_web.controller;

import com.gundam.gundam_web.entity.ForumPost; // 新增导入
import com.gundam.gundam_web.entity.Gundam;
import com.gundam.gundam_web.entity.User;
import com.gundam.gundam_web.mapper.GundamMapper;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // 新增导入
import org.springframework.web.bind.annotation.RequestParam;
@Controller
public class GundamController {

    @Autowired
    private GundamMapper gundamMapper;

    // 1. 首页
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        model.addAttribute("user", session.getAttribute("user"));
        // 首页下方的"新品速递"，这里简单展示所有机体的前4个
        model.addAttribute("gundamList", gundamMapper.findAll().stream().limit(4).toList());
        return "index";
    }

    // 资料库
    @GetMapping("/library")
    public String library(@RequestParam(required = false) String grade, Model model, HttpSession session) {
        model.addAttribute("user", session.getAttribute("user"));

        List<Gundam> gundamList;
        if (grade != null && !grade.isEmpty() && !grade.equals("ALL")) {
            gundamList = gundamMapper.findByGrade(grade);
            model.addAttribute("currentGrade", grade);
        } else {
            gundamList = gundamMapper.findAll();
            model.addAttribute("currentGrade", "ALL");
        }

        // --- 【核心修正】在传递给前端前，确保数据是最新的 ---
        // 这里可以加断点调试，看看从数据库查出来的数据对不对
        // System.out.println("查出的第一条数据：" + gundamList.get(0).getName());
        // System.out.println("图片链接：" + gundamList.get(0).getImageUrl());
        
        model.addAttribute("gundamList", gundamList);
        return "library";
    }

    // 3. 详情页
    @GetMapping("/detail")
    public String detail(@RequestParam Integer id, Model model) {
        model.addAttribute("g", gundamMapper.findById(id));
        
        return "detail";
    }

    // 4. 登录注册页面
    @GetMapping("/login")
    public String loginPage() { return "login"; }
    @GetMapping("/register")
    public String registerPage() { return "register"; }

    // 登录逻辑
    @PostMapping("/login")
    public String doLogin(String username, String password, HttpSession session, Model model) {
        User user = gundamMapper.login(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            // 如果是管理员(这里简单判断用户名是admin)，跳转到后台
            if("admin".equals(user.getUsername())) {
                return "redirect:/admin";
            }
            return "redirect:/";
        }
        model.addAttribute("msg", "ACCESS DENIED: Invalid Credentials");
        return "login";
    }

    // 注册逻辑
    @PostMapping("/register")
    public String doRegister(User user) {
        try {
            gundamMapper.insertUser(user);
            return "redirect:/login";
        } catch (Exception e) {
            return "register";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // --- 新增：后台管理模块 (对应 AdminDashboard.tsx) ---
    
     // --- 新增：后台管理模块 (修正版) ---
    
    // 1. 进入后台看板
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        
        // 1. 如果没登录 -> 去登录页
        if (user == null) {
            return "redirect:/login";
        }

        // 2. 【核心修改】 如果登录了，但身份不是 admin -> 踢回首页 (禁止访问)
        if (!"admin".equals(user.getRole())) {
            return "redirect:/"; 
        }

        // 只有是 admin 才能运行到这里
        model.addAttribute("gundamList", gundamMapper.findAll());
        return "dashboard";
    }

    // 2. 处理“添加机体”表单
    @PostMapping("/admin/add")
    public String addGundam(Gundam gundam) {
        // 调用 Mapper 的 insert 方法
        gundamMapper.insert(gundam);
        // 添加完跳回 /dashboard 刷新列表
        return "redirect:/dashboard";
    }

    // 3. 处理“删除机体”请求
    @GetMapping("/admin/delete")
    public String deleteGundam(@RequestParam Integer id) {
        gundamMapper.deleteById(id);
        return "redirect:/dashboard";
    }
    // 新增：论坛页面
     @GetMapping("/forum")
    public String forum(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 没登录不能进论坛
        }
        model.addAttribute("user", user);
        
        // 从数据库查询所有帖子
        model.addAttribute("posts", gundamMapper.findAllPosts());
        
        return "forum";}
    // 新增：个人中心 (驾驶员档案)
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // 没登录就踢回登录页
        }
        model.addAttribute("user", user);
        return "profile"; // 对应 templates/profile.html
    }
    // --- 【新增】处理发送讯息的请求 ---
    @PostMapping("/forum/post")
    public String addPost(ForumPost post, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // 把当前登录用户的名字设置进去
        post.setCallsign(user.getUsername());
        
        // 插入数据库
        gundamMapper.insertPost(post);
        
        // 发表成功后，重新回到论坛页面
        return "redirect:/forum";
    }
}
