package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.merchant.*;
import org.demo.baoleme.dto.response.merchant.*;
import org.demo.baoleme.pojo.Merchant;
import org.demo.baoleme.service.MerchantService;
import org.demo.baoleme.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    private final MerchantService merchantService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @PostMapping("/register")
    public CommonResponse register(@RequestBody MerchantRegisterRequest request) {
        System.out.println("收到注册请求: " + request);

        // Step1: 创建Merchant对象并拷贝属性
        Merchant merchant = new Merchant();
        BeanUtils.copyProperties(request, merchant);

        // Step2: 调用Service层创建商家
        Merchant result = merchantService.createMerchant(merchant);

        // Step3: 处理创建结果
        if (result == null) {
            return ResponseBuilder.fail("注册失败：用户名或手机号重复");
        }

        // Step4: 构建响应体
        MerchantRegisterResponse response = new MerchantRegisterResponse();
        response.setUserId(result.getId());
        response.setUsername(result.getUsername());
        response.setPhone(result.getPhone());
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/login")
    public CommonResponse login(@RequestBody MerchantLoginRequest request) {
        // Step1: 根据用户名查询商家
        Merchant result = merchantService.getMerchantByUsername(request.getUsername());

        // Step2: 验证用户存在性
        if (result == null) {
            return ResponseBuilder.fail("用户名不存在");
        }

        // Step3: 验证密码匹配
        // 使用 BCrypt 验证密码
        if (!passwordEncoder.matches(request.getPassword(), result.getPassword())) {
            return ResponseBuilder.fail("密码错误");
        }

        String loginKey = "merchant:login:" + result.getId();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(loginKey))) {
            return ResponseBuilder.fail("该商家已登录，请先登出");
        }

        // Step4: 生成JWT令牌
        String token = JwtUtils.createToken(result.getId(), "merchant", result.getUsername());
        redisTemplate.opsForValue().set("merchant:token:" + token, result.getId(), 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().set(loginKey, token, 1, TimeUnit.DAYS);

        // Step5: 构建登录响应
        MerchantLoginResponse response = new MerchantLoginResponse();
        response.setToken(token);
        response.setUserId(result.getId());
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/info")
    public CommonResponse getInfo() {
        // Step1: 从线程上下文获取用户ID
        Long id = UserHolder.getId();

        // Step2: 查询商家详细信息
        Merchant merchant = merchantService.getMerchantById(id);

        // Step3: 验证查询结果
        if (merchant == null) {
            return ResponseBuilder.fail("当前身份无效或用户不存在");
        }

        // Step4: 构建信息响应体
        MerchantInfoResponse response = new MerchantInfoResponse();
        BeanUtils.copyProperties(merchant, response);
        response.setUserId(merchant.getId());
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse update(@RequestBody MerchantUpdateRequest request) {
        // Step1: 创建Merchant对象并设置ID
        Merchant merchant = new Merchant();
        merchant.setId(UserHolder.getId());

        // Step2: 拷贝请求参数
        BeanUtils.copyProperties(request, merchant);

        // Step3: 调用Service更新数据
        Merchant m = merchantService.updateInfo(merchant);

        // Step4: 处理更新结果
        boolean success = (m != null);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("更新失败，请检查请求字段");
    }

    @PostMapping("/logout")
    public CommonResponse logout(@RequestHeader("Authorization") String tokenHeader) {
        String token = tokenHeader.replace("Bearer ", "");
        String tokenKey = "merchant:token:" + token;

        Object merchantId = redisTemplate.opsForValue().get(tokenKey);
        if (merchantId != null) {
            String loginKey = "merchant:login:" + merchantId;
            redisTemplate.delete(loginKey);       // ✅ 删除登录标识
        }

        redisTemplate.delete(tokenKey);          // ✅ 删除 token 本体

        // 设置为离线
        Long id = UserHolder.getId();
        Merchant merchant = new Merchant();
        merchant.setId(id);

        boolean success = (merchantService.updateInfo(merchant) != null);
        return success ? ResponseBuilder.ok() : ResponseBuilder.fail("登出失败");
    }

    @DeleteMapping("/delete")
    public CommonResponse delete() {
        // Step1: 获取当前用户ID
        Long id = UserHolder.getId();

        // Step2: 执行删除操作
        boolean ok = merchantService.deleteMerchant(id);

        // Step3: 返回操作结果
        return ok ? ResponseBuilder.ok() : ResponseBuilder.fail("注销失败");
    }
}