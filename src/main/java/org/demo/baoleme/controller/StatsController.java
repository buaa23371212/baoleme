package org.demo.baoleme.controller;

import org.demo.baoleme.dto.request.salesStats.*;
import org.demo.baoleme.dto.response.salesStats.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/stats-store")
public class StatsController {
    private final SalesStatsService salesStatsService;

    public StatsController(SalesStatsService salesStatsService) {
        this.salesStatsService = salesStatsService;
    }

    @PostMapping("/overview")
    public SaleOverviewStatsResponse getSalesOverview(@Valid @RequestBody SaleOverviewStatsRequest request) {
        // Step 1: 参数验证由@Valid自动处理

        // Step 2: 获取时间范围对应的起止时间
        LocalDate[] dateRange = resolveTimeRange(request.getTimeRange());
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        // Step 3: 调用服务层获取数据（示例伪代码）
         double totalSales = salesStatsService.getTotalSales(request.getStoreId(), startDate, endDate);
         int orderCount = salesStatsService.getOrderCount(request.getStoreId(), startDate, endDate);
         List<Product> popularProducts = salesStatsService.getPopularProducts(request.getStoreId(), startDate, endDate);

        // Step 4: 封装响应（示例数据）
        SaleOverviewStatsResponse response = new SaleOverviewStatsResponse();
        response.setTotalSales(15000.0);      // 替换为实际数据
        response.setOrderCount(120);          // 替换为实际数据
        response.setPopularProducts(List.of()); // 替换为实际数据

        return response;
    }

    @PostMapping("/trend")
    public SaleTrendStatsResponse getSalesTrend(@Valid @RequestBody SaleTrendStatsRequest request) {
        // Step 1: 参数验证由@Valid自动处理

        // Step 2: 生成日期序列（示例伪代码）
        List<String> dateLabels = generateDateLabels(
                request.getType(),
                request.getNumOfRecentDays()
        );

        // Step 3: 查询每日销售额（示例伪代码）
         List<Integer> salesValues = salesStatsService.getSalesTrend(
             request.getStoreId(),
             request.getType(),
             request.getNumOfRecentDays()
         );

        // Step 4: 封装响应（示例数据）
        SaleTrendStatsResponse response = new SaleTrendStatsResponse();
        response.setDates(dateLabels);
        response.setValues(List.of(2000, 2500, 3000)); // 替换为实际数据

        return response;
    }

    // 辅助方法：解析时间范围枚举为具体日期
    private LocalDate[] resolveTimeRange(SaleOverviewStatsRequest.TimeRange range) {
        LocalDate now = LocalDate.now();
        return switch (range) {
            case TODAY -> new LocalDate[]{now, now};
            case THIS_WEEK -> new LocalDate[]{now.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)), now};
            case THIS_MONTH -> new LocalDate[]{now.withDayOfMonth(1), now};
        };
    }

    // 辅助方法：生成趋势图日期标签
    private List<String> generateDateLabels(SaleTrendStatsRequest.TimeAxis type, int days) {
        // 示例实现：生成最近N天的日期字符串（按类型聚合）
        return List.of("2023-10-01", "2023-10-02", "2023-10-03"); // 替换为实际逻辑
    }
}
