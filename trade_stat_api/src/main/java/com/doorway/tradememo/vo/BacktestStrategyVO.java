package com.doorway.tradememo.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 回测策略VO
 * Author: liupeizhi
 * Date: 2026-02-06
 */
@Data
public class BacktestStrategyVO {
    /**
     * 策略名称
     */
    private String name;

    /**
     * 策略描述
     */
    private String description;

    /**
     * 策略参数列表
     */
    private List<StrategyParam> params;

    /**
     * 策略参数
     */
    @Data
    public static class StrategyParam {
        /**
         * 参数名称
         */
        private String name;

        /**
         * 参数描述
         */
        private String description;

        /**
         * 默认值
         */
        private String defaultValue;

        /**
         * 参数类型
         */
        private String type;

        /**
         * 可选值
         */
        private List<String> options;
    }
}
