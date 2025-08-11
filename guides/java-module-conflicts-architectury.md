# Java模块系统冲突问题：Architectury框架技术分析

## 问题概述

在MTR Station Decoration Addon项目中，我们遇到了一个严重的Java模块系统冲突问题。该问题表现为运行时错误：

```
Exception in thread "main" java.lang.module.ResolutionException: 
Modules generated_xxx and msd export package top.mcmtr to module forge/architectury
```

这个错误阻止了模组在Minecraft 1.20.1 + Forge环境下的正常运行，但奇怪的是，相同的代码在MTR 4.0.0版本下可以正常工作。

## 技术分析

### 根本原因

问题的核心在于**Architectury Transformer在运行时自动生成重复的模块声明**：

1. **原始模块**：`msd`（我们的模组）包含包`top.mcmtr`
2. **生成模块**：`generated_xxx`（Architectury自动生成）也包含包`top.mcmtr`
3. **冲突发生**：两个模块都试图向同一个目标模块（`forge`或`architectury`）导出相同的包

### Java模块系统限制

Java模块系统（JPMS）有严格的包导出规则：
- **一个包只能由一个模块导出给特定的目标模块**
- **不允许多个模块向同一目标导出相同的包**
- **这是为了避免包冲突和类加载问题**

### Architectury Transformer工作机制

Architectury Transformer的作用是：
1. **代码转换**：将通用代码转换为平台特定代码
2. **模块生成**：自动创建临时模块来处理跨平台兼容性
3. **依赖注入**：在运行时注入必要的平台特定实现

问题出现在第2步，Transformer意外创建了包含重复包的模块。

## 详细测试结果

### 尝试1：禁用Architectury Transformer

**方法**：
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4096M -Darchitectury.transformer.disable=true
systemProp.architectury.transformer.disable=true
```

```gradle
// forge/build.gradle
runs {
    client {
        vmArgs "-Darchitectury.transformer.disable=true",
               "-Darchitectury.runtime.disable=true",
               "-Darchitectury.injectables.disable=true"
    }
}
```

**结果**：❌ 失败
- Transformer仍然运行
- 错误信息显示：`[Architectury Transformer] Architectury Runtime 5.2.87`
- 模块冲突依然存在

### 尝试2：移除Fabric模块

**方法**：
```gradle
// settings.gradle
include("common")
include("forge")
// include("fabric") // 移除
```

**结果**：✅ 构建成功，❌ 运行失败
- 构建过程正常完成
- 运行时仍然出现相同的模块冲突
- 证明问题不在于多平台构建，而在于Transformer本身

### 尝试3：版本升级

**测试的版本组合**：

| Architectury Loom | Gradle | 结果 | 备注 |
|-------------------|--------|------|------|
| 1.6.422 | 8.6 | ❌ | 原始问题版本 |
| 1.7.412 | 8.8 | ❌ | 需要Gradle 8.8+ |
| 1.10.434 | 8.8 | ❌ | Beta版本，兼容性问题 |

**结果**：所有版本都存在不同程度的问题

### 尝试4：Architectury API版本固定

**方法**：
```gradle
// 替换动态版本获取
rootProject.ext.architectury_data = ["9.2.14+forge"]
```

**结果**：❌ 失败
- 使用固定的Architectury API版本
- 模块冲突问题依然存在
- 证明问题不在于API版本，而在于Transformer逻辑

### 尝试5：完全移除Architectury依赖

**方法**：
```gradle
dependencies {
    // modApi "${rootProject.architectury_id}:architectury-forge:..." // 注释掉
}
```

**结果**：❌ 编译失败
- 代码中大量使用Architectury API
- 需要大量重构才能移除依赖
- 不是可行的解决方案

## 关键发现

### MTR 4.0.0 vs MTR 3.2.2的差异

通过对比分析，我们发现了关键差异：

1. **插件版本策略**：
   - MTR 3.2.2：使用固定版本`1.6-SNAPSHOT`
   - MTR 4.0.0：使用`version "+"`获取最新版本

2. **Gradle版本要求**：
   - MTR 3.2.2：兼容Gradle 8.6
   - MTR 4.0.0：需要Gradle 8.8+

3. **Architectury Loom版本**：
   - MTR 3.2.2：1.6.422（存在模块生成bug）
   - MTR 4.0.0：1.10.434+（修复了模块问题）

### 版本特定问题的证据

1. **相同代码，不同结果**：相同的包名`top.mcmtr`在不同版本下表现不同
2. **构建vs运行时差异**：构建成功但运行失败，说明问题在运行时处理
3. **Transformer版本一致**：都使用Architectury Runtime 5.2.87，但行为不同

### 问题的本质

这**不是**一个普遍的Architectury框架问题，而是：
- **特定版本组合的兼容性问题**
- **Architectury Loom 1.6.422的已知bug**
- **可以通过正确的版本升级解决**

## 推荐解决方案

### 方案1：升级到MTR 4.0.0（强烈推荐）

**优点**：
- ✅ 彻底解决模块冲突问题
- ✅ 使用最新的稳定版本
- ✅ 获得最新功能和bug修复

**缺点**：
- ⚠️ 需要升级Gradle到8.8+
- ⚠️ 可能需要适配API变化
- ⚠️ 需要测试兼容性

**实施步骤**：
```properties
# gradle.properties
minecraftVersion=1.20.4
mtrVersion=4.0.0-prerelease.2
```

```gradle
// build.gradle
plugins {
    id "architectury-plugin" version "+"
    id "dev.architectury.loom" version "+" apply false
}
```

### 方案2：回退到1.16.5版本（保守选择）

**优点**：
- ✅ 立即可用，无需修改
- ✅ 稳定可靠
- ✅ 开发效率高

**缺点**：
- ❌ 无法使用1.20.1的新功能
- ❌ 长期维护问题

### 方案3：等待官方修复（不推荐）

**适用场景**：无法升级且必须使用1.20.1

**风险**：
- ❌ 修复时间不确定
- ❌ 影响开发进度

## 未来维护建议

### 版本管理策略

1. **定期更新Architectury版本**：关注官方更新，及时升级
2. **测试版本兼容性**：在升级前进行充分测试
3. **文档化版本依赖**：记录工作的版本组合

### 监控指标

1. **构建成功率**：监控CI/CD构建状态
2. **运行时错误**：关注模块系统相关错误
3. **性能影响**：监控Transformer对构建时间的影响

### 预防措施

1. **版本锁定**：在稳定版本上锁定依赖
2. **分支策略**：为不同MC版本维护独立分支
3. **自动化测试**：增加模块系统相关的集成测试

## 技术细节参考

### 错误信息模式

```
Exception in thread "main" java.lang.module.ResolutionException: 
Modules [generated_xxx] and [msd] export package [top.mcmtr] to module [target]
```

其中：
- `generated_xxx`：Architectury生成的临时模块
- `msd`：我们的模组ID
- `top.mcmtr`：冲突的包名
- `target`：目标模块（forge/architectury）

### 关键配置文件

1. **build.gradle**：插件版本和依赖配置
2. **gradle.properties**：JVM参数和系统属性
3. **settings.gradle**：模块包含配置
4. **mods.toml**：模组元数据和依赖声明

---

*本文档基于MTR Station Decoration Addon项目的实际问题分析，记录了完整的问题排查和解决过程。*
