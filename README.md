# TradeStat - 个人股票交易统计系统

<div align="center">

[![GitHub stars](https://img.shields.io/github/stars/liupeizhi/trade_stat)](https://github.com/liupeizhi/trade_stat/stargazers)
[![GitHub license](https://img.shields.io/github/license/liupeizhi/trade_stat)](https://github.com/liupeizhi/trade_stat/blob/main/LICENSE)

</div>

## 项目简介

TradeStat 是一个面向个人投资者的股票交易记录管理与分析平台，帮助投资者系统化管理交易数据、实时掌握持仓收益、深度分析投资策略。

### 解决什么问题

- **交易记录散乱** - 券商软件功能有限，历史数据难以整合分析
- **收益计算复杂** - 多次买卖、分红除权导致成本计算困难
- **策略评估缺失** - 缺乏量化指标，难以评估投资策略有效性
- **持仓跟踪不便** - 无法实时了解各股票的持仓成本和盈亏情况

## 核心功能

### 1. 交易记录管理
- 支持多券商交易记录导入（海通证券、招商证券、国信证券等）
- 手动添加买入/卖出交易
- 交易记录查询与筛选

### 2. 持仓管理
| 功能 | 说明 |
|------|------|
| 当前持仓 | 实时查看持仓股票、价格、数量、成本、盈亏 |
| 历史清仓 | 查询已卖出股票的完整交易记录和收益 |
| 持仓时间线 | 展示建仓、加仓、减仓、清仓全过程 |

### 3. 收益分析
- 实时收益计算（按股票、按账户）
- 收益率分析（当日收益率、持仓收益率）
- 资金流分析（买入/卖出金额统计）

### 4. 数据可视化
- **仪表盘** - 交易概览、收益统计、持仓分布
- **K线图** - 股票价格走势
- **成本线** - 持仓成本变化曲线

## 技术架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端 (React)                          │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │ Ant Design  │  │  Umi Request │  │     ECharts        │ │
│  │   Pro       │  │   网络请求    │  │    数据可视化       │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP/REST
┌────────────────────────┴────────────────────────────────────┐
│                      后端 (Spring Boot)                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │   MyBatis   │  │   Swagger   │  │    事件机制         │ │
│  │   持久层    │  │   API文档    │  │   持仓自动计算      │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────┴────────────────────────────────────┐
│                        数据库 (MySQL)                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐ │
│  │ 交易明细    │  │  持仓数据   │  │    股票行情         │ │
│  │TradeDetail  │  │StockPosition│  │  StockQuotation     │ │
│  └─────────────┘  └─────────────┘  └─────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 前端框架 | React + Umi | 企业级前端框架 |
| UI组件 | Ant Design Pro | 开箱即用的中台前端方案 |
| 数据可视化 | ECharts / G2 | 图表和数据可视化 |
| 后端框架 | Spring Boot | 简化 Spring 应用开发 |
| 持久层 | MyBatis | 数据库 ORM 框架 |
| 数据库 | MySQL 8.0 | 关系型数据库 |
| 外部数据 | 东方财富 API | 实时股票行情获取 |
| 容器化 | Docker | 环境一致性与快速部署 |

### 核心数据模型

```
┌─────────────────┐     ┌─────────────────┐
│   TradeDetail   │     │ StockPosition   │
├─────────────────┤     ├─────────────────┤
│ id              │     │ code            │
│ code (股票代码)  │─────│ vol (持仓数量)   │
│ tradeTime       │     │ price (当前价格) │
│ opt (买入/卖出)  │     │ currentCost     │
│ price (成交价)   │     │ lastClearTime   │
│ vol (成交量)     │     │ buildPositionTime│
│ commission      │     └─────────────────┘
│ tax            │
└─────────────────┘
```

## 快速开始

### 环境要求

- Node.js 14+
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 1. 克隆项目

```bash
git clone https://github.com/liupeizhi/trade_stat.git
cd trade_stat
```

### 2. 数据库设置

```bash
# 启动 MySQL 并创建数据库
mysql -u root -p < trade_stat_api/sql/init/trade_memo.sql
```

### 3. 后端启动

```bash
cd trade_stat_api

# 修改 src/main/resources/application.yml 中的数据库配置
# spring.datasource.url: jdbc:mysql://localhost:3306/trade_memo
# spring.datasource.username: your_username
# spring.datasource.password: your_password

# 启动服务
mvn spring-boot:run
# 或打包后运行
mvn clean package -DskipTests
java -jar target/trade_stat_api-0.0.1-SNAPSHOT.jar
```

后端服务运行在 http://localhost:8080

### 4. 前端启动

```bash
cd trade_stat_web

# 安装依赖
npm install

# 开发模式启动
npm run dev
```

前端服务运行在 http://localhost:8000

### 5. Docker 部署（推荐）

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

## 项目结构

```
trade_stat/
├── trade_stat_api/                 # 后端服务
│   ├── src/main/java/
│   │   └── com/doorway/tradememo/
│   │       ├── controller/         # REST API 控制器
│   │       │   ├── PositionController.java   # 持仓相关接口
│   │       │   ├── TradeDetailController.java  # 交易记录接口
│   │       │   └── DashboardController.java     # 仪表盘接口
│   │       ├── service/impl/      # 业务逻辑实现
│   │       │   ├── PositionServiceImpl.java     # 持仓计算逻辑
│   │       │   ├── ProfitServiceImpl.java       # 收益计算逻辑
│   │       │   └── StockQuotationServiceImpl.java  # 行情获取
│   │       ├── mapper/            # MyBatis 数据访问层
│   │       ├── domain/            # 实体类
│   │       └── config/            # 配置类
│   ├── sql/                       # 数据库脚本
│   │   ├── init/                  # 初始化脚本
│   │   └── data/                  # 测试数据
│   └── Dockerfile
│
├── trade_stat_web/                # 前端应用
│   ├── src/
│   │   ├── pages/
│   │   │   ├── position/          # 持仓页面
│   │   │   │   ├── current/       # 当前持仓
│   │   │   │   ├── history/       # 历史清仓
│   │   │   │   └── timeline/      # 持仓时间线
│   │   │   ├── trade/             # 交易页面
│   │   │   │   ├── details/       # 交易明细
│   │   │   │   └── upload/        # 记录上传
│   │   │   └── analysis/          # 分析页面
│   │   │       ├── dashboard/     # 仪表盘
│   │   │       └── score/         # 评分分析
│   │   ├── components/            # 公共组件
│   │   ├── services/              # API 服务封装
│   │   └── utils/                 # 工具函数
│   ├── config/                    # 配置文件
│   ├── public/                    # 静态资源
│   └── Dockerfile
│
└── docker-compose.yml             # Docker 编排配置
```

## 主要接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/position/current_positions` | GET | 获取当前持仓列表 |
| `/position/stock_history_positions` | POST | 查询历史清仓记录 |
| `/trade_details` | POST | 添加交易记录 |
| `/trade_details/upload_records` | POST | 上传交易记录文件 |
| `/dashboard` | GET | 获取仪表盘数据 |
| `/profit/history` | GET | 获取历史收益 |

完整的 API 文档请访问 http://localhost:8080/swagger-ui.html

## 系统截图

> TODO: 添加系统截图

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## 联系方式

- GitHub Issues: https://github.com/liupeizhi/trade_stat/issues

---

**TradeStat** - 让投资更清晰，让交易可追溯
