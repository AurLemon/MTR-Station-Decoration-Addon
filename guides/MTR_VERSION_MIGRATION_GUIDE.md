# MTR 版本迁移指南

## 概述

本文档详细记录了 MTR Station Decoration Addon 在不同 Minecraft 和 MTR 版本之间迁移时遇到的问题、解决方案和最佳实践。

## 版本对应关系

| Minecraft 版本 | MTR 版本 | MSD 版本 | 状态 |
|---------------|----------|----------|------|
| 1.16.5 | MTR 2.x | MSD 1.3.4 | ✅ 原始版本 |
| 1.20.1 | MTR 3.2.2 | MSD 1.3.4-enhancement-1 | ✅ 本次移植 |
| 1.20.1+ | MTR 4.0.0+ | MSD 4.0.0+ | ❌ 不兼容 MTR 3.2.2 |

## 主要 API 变化

### 1. Minecraft 1.16.5 → 1.20.1

#### 1.1 Block Properties API
**问题**: `Properties.of(MapColor)` 方法在 1.20.1 中不存在

```java
// ❌ 1.16.5 写法
Properties.of(MapColor.METAL)

// ✅ 1.20.1 写法
Properties.of().mapColor(MapColor.METAL)
```

**影响文件**: `MSDBlocks.java`

#### 1.2 Font 渲染系统重大变化
**问题**: Font.draw() 方法签名完全改变，参数类型和顺序都不同

```java
// ❌ 1.16.5 写法
textRenderer.draw(text, x, y, color, false, matrices.last().pose(), vertexConsumers, Font.DisplayMode.NORMAL, 0, light);

// ✅ 1.20.1 写法 (使用 MTR 包装)
IDrawing.drawStringWithFont(matrices, textRenderer, null, text, HorizontalAlignment.LEFT, VerticalAlignment.TOP, x, y, maxWidth, height, scale, color, false, light, null);
```

**影响文件**: 
- `RenderCustomTextSign.java`
- `RenderPIDS.java`
- 所有 Screen 类

#### 1.3 Screen 渲染系统变化
**问题**: Screen.render() 方法从 PoseStack 改为 GuiGraphics

```java
// ❌ 1.16.5 写法
public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
    renderBackground(matrices);
    super.render(matrices, mouseX, mouseY, delta);
}

// ⚠️ 1.20.1 临时解决方案
public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
    // renderBackground(matrices); // 需要 GuiGraphics
    // super.render(matrices, mouseX, mouseY, delta); // 需要 GuiGraphics
}
```

### 2. MTR 2.x → MTR 3.2.2

#### 2.1 IDrawing 接口变化
**问题**: HorizontalAlignment 和 VerticalAlignment 访问方式改变

```java
// ❌ 直接访问 (不工作)
import mtr.client.IDrawing.HorizontalAlignment;
import mtr.client.IDrawing.VerticalAlignment;

// ✅ 通过实现接口访问
public class RenderCustomTextSign implements IDrawing {
    // 现在可以直接使用 HorizontalAlignment.LEFT
}
```

#### 2.2 BufferSource 类型变化
**问题**: MultiBufferSource 无法转换为 BufferSource

```java
// ❌ 传递 vertexConsumers
IDrawing.drawStringWithFont(matrices, textRenderer, vertexConsumers, ...);

// ✅ 传递 null，使用 MTR 内部渲染
IDrawing.drawStringWithFont(matrices, textRenderer, null, ...);
```

## 常见问题和解决方案

### 问题 1: 编译错误 - 找不到符号
**症状**: 
```
错误: 找不到符号 Properties.of(MapColor.METAL)
```

**解决方案**: 
```java
Properties.of().mapColor(MapColor.METAL)
```

### 问题 2: 文本渲染不显示
**症状**: 编译成功但文本不显示

**原因**: Font.draw() API 完全改变

**解决方案**: 
1. 让渲染器类实现 `IDrawing` 接口
2. 使用 `IDrawing.drawStringWithFont()` 方法
3. 传递 `null` 而不是 `vertexConsumers`

### 问题 3: HorizontalAlignment 找不到
**症状**: 
```
错误: 找不到符号 HorizontalAlignment
```

**解决方案**: 
```java
public class YourRenderer implements IDrawing {
    // 现在可以直接使用 HorizontalAlignment.LEFT
}
```

### 问题 4: GUI 界面渲染问题
**症状**: 配置界面无法正常显示

**临时解决方案**: 注释掉 `renderBackground()` 和 `super.render()` 调用

**完整解决方案**: 需要适配 GuiGraphics API (未在本次移植中实现)

## 移植检查清单

### 编译阶段
- [ ] 检查所有 `Properties.of()` 调用
- [ ] 更新包导入路径
- [ ] 检查依赖版本配置
- [ ] 验证 Gradle 构建配置

### 功能阶段
- [ ] 检查所有文本渲染调用
- [ ] 验证 IDrawing 接口实现
- [ ] 测试 GUI 界面显示
- [ ] 检查 BufferSource 参数

### 测试阶段
- [ ] 自定义文本标志显示
- [ ] PIDS 显示屏功能
- [ ] 配置界面可用性
- [ ] 游戏内功能完整性

## 最佳实践

### 1. 渲染器类设计
```java
public class YourRenderer<T extends BlockEntityMapper> 
    extends BlockEntityRendererMapper<T> 
    implements IGui, IDrawing {
    
    // 实现 IDrawing 接口以获得对齐枚举访问权限
}
```

### 2. 文本渲染调用
```java
// 标准模式
IDrawing.drawStringWithFont(
    matrices, 
    textRenderer, 
    null,  // 重要：传递 null 而不是 vertexConsumers
    text, 
    HorizontalAlignment.LEFT, 
    VerticalAlignment.TOP, 
    x, y, maxWidth, height, 
    scale, color, false, light, null
);
```

### 3. 错误处理
```java
try {
    // 渲染代码
} catch (Exception e) {
    e.printStackTrace(); // 便于调试
}
```

## 已知限制

1. **GUI 渲染**: `renderBackground()` 和 `super.render()` 需要 GuiGraphics，当前暂时禁用
2. **兼容性**: 本移植版本仅支持 MTR 3.2.2，不兼容 MTR 4.0.0+
3. **功能完整性**: 核心功能正常，但部分 GUI 美化效果可能缺失

## 调试技巧

### 1. 编译错误调试
```bash
./gradlew build --project-prop buildVersion=1.20.1 --info
```

### 2. 运行时错误调试
- 检查游戏日志中的异常堆栈
- 使用 try-catch 包装渲染代码
- 验证 MTR 依赖是否正确加载

### 3. 渲染问题调试
- 确认实现了 `IDrawing` 接口
- 检查传递给 `drawStringWithFont` 的参数
- 验证坐标和缩放参数

## 踩坑记录

### 坑 1: HorizontalAlignment 导入失败
**现象**: 即使正确导入也提示找不到符号
**原因**: 在 MTR 3.2.2 中，这些枚举只能通过实现 IDrawing 接口访问
**解决**: 让类实现 IDrawing 接口，然后直接使用枚举名

### 坑 2: MultiBufferSource 类型错误
**现象**: 编译时提示类型不兼容
**原因**: MTR 3.2.2 的 drawStringWithFont 期望 BufferSource 而不是 MultiBufferSource
**解决**: 传递 null，让 MTR 使用内部渲染系统

### 坑 3: 文本渲染位置错误
**现象**: 文本显示但位置不对
**原因**: 1.20.1 中坐标系统可能有微调
**解决**: 仔细检查 x, y 坐标和缩放参数

### 坑 4: Gradle 构建缓存问题
**现象**: 修改代码后编译结果不变
**解决**:
```bash
./gradlew clean
Remove-Item -Recurse -Force ".gradle" -ErrorAction SilentlyContinue
```

### 坑 5: MTR 依赖下载失败
**现象**: 构建时无法下载 MTR JAR
**原因**: 网络问题或镜像源问题
**解决**: 检查网络连接，必要时使用代理

## 版本特定注意事项

### MTR 3.2.2 特点
- 使用 IDrawing 接口进行文本渲染
- 支持 HorizontalAlignment 和 VerticalAlignment
- 需要实现接口才能访问对齐枚举
- BufferSource 参数通常传递 null

### MTR 4.0.0+ 变化
- 完全重构的渲染系统
- 不再兼容旧版本的 IDrawing 接口
- 使用新的 GraphicsHolder 系统
- 本移植版本不支持此版本

## 性能优化建议

### 1. 渲染优化
```java
// 避免频繁的字符串创建
private static final String CACHED_TEXT = "固定文本";

// 使用缓存的矩阵变换
matrices.pushPose();
try {
    // 渲染代码
} finally {
    matrices.popPose();
}
```

### 2. 内存管理
```java
// 及时释放资源
@Override
public void onClose() {
    super.onClose();
    // 清理自定义资源
}
```

## 故障排除

### 编译失败
1. 检查 Java 版本 (需要 Java 17+)
2. 清理 Gradle 缓存
3. 验证依赖版本
4. 检查网络连接

### 运行时崩溃
1. 查看完整的错误堆栈
2. 检查 MTR 是否正确安装
3. 验证 Minecraft 版本匹配
4. 检查其他 MOD 兼容性

### 功能异常
1. 确认实现了必要的接口
2. 检查方法参数类型
3. 验证坐标和缩放计算
4. 测试最小化示例

## 参考资源

- [MTR 官方文档](https://github.com/Minecraft-Transit-Railway/Minecraft-Transit-Railway)
- [Minecraft 1.20.1 API 变化](https://fabricmc.net/wiki/tutorial:migratingfrom1_19)
- [Architectury 文档](https://docs.architectury.dev/)
- [本项目移植报告](../MIGRATION_REPORT.md)
