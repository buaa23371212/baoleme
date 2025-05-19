package org.demo.baoleme.controller;

import org.demo.baoleme.common.CommonResponse;
import org.demo.baoleme.common.ResponseBuilder;
import org.demo.baoleme.dto.request.store.StoreCreateRequest;
import org.demo.baoleme.dto.request.store.StoreDeleteRequest;
import org.demo.baoleme.dto.request.store.StoreUpdateRequest;
import org.demo.baoleme.dto.request.store.StoreViewInfoRequest;
import org.demo.baoleme.dto.response.store.StoreCreateResponse;
import org.demo.baoleme.dto.response.store.StoreUpdateResponse;
import org.demo.baoleme.dto.response.store.StoreViewInfoResponse;
import org.demo.baoleme.pojo.Store;
import org.demo.baoleme.service.StoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public CommonResponse createStore(@RequestBody StoreCreateRequest request) {
        // Step1: 创建空店铺对象
        Store store = new Store();

        // Step2: 拷贝请求参数到领域对象
        BeanUtils.copyProperties(request, store);

        // Step3: 调用服务层创建店铺
        Store createdStore = storeService.createStore(store);

        // Step4: 处理空返回值异常情况
        if (createdStore == null) {
            return ResponseBuilder.fail("店铺创建失败，参数校验不通过");
        }

        // Step5: 构造标准化响应
        StoreCreateResponse response = new StoreCreateResponse();
        BeanUtils.copyProperties(createdStore, response);
        return ResponseBuilder.ok(response);
    }

    @PostMapping("/view")  // 改为POST方式接收请求体
    public CommonResponse getStoreById(@RequestBody StoreViewInfoRequest request) {
        // Step1: 从请求体中提取店铺ID
        Long storeId = request.getStoreId();

        // Step2: 查询店铺详细信息
        Store store = storeService.getStoreById(storeId);

        // Step3: 处理空查询结果
        if (store == null) {
            return ResponseBuilder.fail("店铺ID不存在");
        }

        // Step4: 转换领域对象为响应DTO
        StoreViewInfoResponse response = new StoreViewInfoResponse();
        BeanUtils.copyProperties(store, response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse updateStore(@RequestBody StoreUpdateRequest request) {
        // Step1: 初始化待更新店铺对象
        Store store = new Store();
        store.setId(request.getId());

        // Step2: 拷贝更新字段
        BeanUtils.copyProperties(request, store);

        // Step3: 执行更新操作
        boolean success = storeService.updateStore(store);

        // Step4: 处理更新失败情况
        if (!success) {
            return ResponseBuilder.fail("店铺信息更新失败");
        }

        // Step5: 返回更新后的完整数据
        StoreUpdateResponse response = new StoreUpdateResponse();
        BeanUtils.copyProperties(store, response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/status")  // 路径修改为统一使用请求体
    public CommonResponse toggleStoreStatus(@RequestBody StoreUpdateRequest request) {
        // Step1: 从请求体获取参数
        Long storeId = request.getId();
        int status = request.getStatus();

        // Step2: 校验状态值合法性
        if (status < 0 || status > 1) {
            return ResponseBuilder.fail("状态值必须是0或1");
        }

        // Step3: 执行状态切换操作
        boolean success = storeService.toggleStoreStatus(storeId, status);

        // Step4: 根据操作结果返回响应
        return success ?
                ResponseBuilder.ok("店铺状态更新成功") :
                ResponseBuilder.fail("状态更新失败，店铺可能不存在");
    }

    @DeleteMapping("/delete")  // 改为使用请求体的DELETE方法
    public CommonResponse deleteStore(@RequestBody StoreDeleteRequest request) {
        // Step1: 从请求体获取店铺ID
        Long storeId = request.getStoreId();

        // Step2: 执行删除操作
        boolean success = storeService.deleteStore(storeId);

        // Step3: 处理删除结果
        return success ?
                ResponseBuilder.ok("店铺数据已删除") :
                ResponseBuilder.fail("删除操作失败，店铺可能不存在");
    }
}