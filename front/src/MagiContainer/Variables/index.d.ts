export interface VariablePlugin<T = any> {
    /**
     * 配置
     */
    config: VariablePluginConfig<T>;
    /**
     * dashboard的上下文
     */
    context: DashboardContext;
    /**
     * 获取变量的值，用于初始化变量的值 一起刷新后重新获取值
     * @param current - 当前变量的值
     */
    getValue(current?: any): any;
    /**
     * 准备变量的基础诗句
     * @param force - 强制更新
     */
    resolve(force?: boolean): Promise<any>;
    /**
     *
     * @param cfg
     */
    init(cfg: VariablePluginConfig<T>): void;
    /**
     * 变量如何渲染
     */
    view: React.ComponentType;
    /**
     * 变量渲染的props
     */
    viewProps?: any;
    /** 数值是否为数组, 主要是为了辅助query转换为变量 */
    isArray?: boolean;
}
