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

import java.util.List;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/create")
    public CommonResponse createStore(@RequestBody StoreCreateRequest request) {
        // Step1: 创建 Store 对象并拷贝属性
        Store store = new Store();
        BeanUtils.copyProperties(request, store);

        // Step2: 调用 Service 创建店铺
        Store createdStore = storeService.createStore(store);

        // Step3: 处理创建结果
        if (createdStore == null) {
            return ResponseBuilder.fail("店铺创建失败，请检查输入参数");
        }

        // Step4: 构建响应体
        StoreCreateResponse response = new StoreCreateResponse();
        BeanUtils.copyProperties(createdStore, response);
        return ResponseBuilder.ok(response);
    }

    @GetMapping("/{storeId}")
    public CommonResponse getStoreById(@RequestBody StoreViewInfoRequest request) {
        // Step1: 查询店铺详情
        Long storeId = request.getStoreId();
        Store store = storeService.getStoreById(storeId);

        // Step2: 验证查询结果
        if (store == null) {
            return ResponseBuilder.fail("店铺不存在");
        }

        // Step3: 构建响应体
        StoreViewInfoResponse response = new StoreViewInfoResponse();
        BeanUtils.copyProperties(store, response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/update")
    public CommonResponse updateStore(@RequestBody StoreUpdateRequest request) {
        // Step1: 创建 Store 对象并设置 ID
        Store store = new Store();
        store.setId(request.getId());

        // Step2: 拷贝请求参数
        BeanUtils.copyProperties(request, store);

        // Step3: 调用 Service 更新数据
        boolean success = storeService.updateStore(store);

        // Step4: 处理更新结果
        if (!success) {
            return ResponseBuilder.fail("更新失败，请检查店铺 ID 是否存在");
        }

        // Step5: 构建响应体
        StoreUpdateResponse response = new StoreUpdateResponse();
        BeanUtils.copyProperties(store, response);
        return ResponseBuilder.ok(response);
    }

    @PutMapping("/{storeId}/status")
    public CommonResponse toggleStoreStatus(
            @PathVariable Long storeId,
            @RequestParam int status
    ) {
        // Step1: 验证状态值合法性
        if (status < 0 || status > 1) {
            return ResponseBuilder.fail("非法状态值，只能为 0 或 1");
        }

        // Step2: 执行状态切换
        boolean success = storeService.toggleStoreStatus(storeId, status);

        // Step3: 返回操作结果
        return success ?
                ResponseBuilder.ok("店铺状态更新成功") :
                ResponseBuilder.fail("状态更新失败");
    }

    @DeleteMapping("/{storeId}")
    public CommonResponse deleteStore(@RequestBody StoreDeleteRequest request) {
        // Step1: 创建 StoreDeleteRequest 对象
        Long storeId = request.getStoreId();

        // Step2: 执行删除操作
        boolean success = storeService.deleteStore(storeId);

        // Step3: 返回操作结果
        return success ?
                ResponseBuilder.ok("店铺删除成功") :
                ResponseBuilder.fail("删除失败，店铺可能不存在");
    }
}