# 文档索引

本项目包含以下重要文档，记录了 MTR Station Decoration Addon 1.20.1 移植的完整过程：

## 📋 主要文档

### 1. [README.md](../README.md)
- **用途**: 项目主页和快速入门指南
- **内容**: 
  - 项目介绍和功能列表
  - mtr-3.2.2 分支说明
  - 基于 MSD 1.3.4 的修改内容
  - 1.20.1 兼容性修改详情
  - 安装和构建说明
  - 兼容性信息

### 2. [MTR_VERSION_MIGRATION_GUIDE.md](./MTR_VERSION_MIGRATION_GUIDE.md)
- **用途**: MTR 版本间迁移的完整指南
- **内容**:
  - 版本对应关系表
  - 主要 API 变化详解
  - 常见问题和解决方案
  - 踩坑记录和避坑指南
  - 移植检查清单
  - 最佳实践和性能优化
  - 故障排除指南

### 3. [MIGRATION_REPORT.md](./MIGRATION_REPORT.md)
- **用途**: 本次移植项目的详细报告
- **内容**:
  - 移植过程的四个阶段
  - 每个阶段的问题和解决方案
  - 构建结果和验收标准
  - 技术实现细节



## 🎯 文档使用指南

### 对于新用户
1. 先阅读 [README.md](../README.md) 了解项目基本信息
2. 按照安装说明进行部署
3. 如遇问题，查看故障排除部分

### 对于开发者
1. 阅读 [MTR_VERSION_MIGRATION_GUIDE.md](./MTR_VERSION_MIGRATION_GUIDE.md) 了解技术细节
2. 查看移植检查清单确保代码质量
3. 参考最佳实践进行开发

### 对于维护者
1. 查看 [MIGRATION_REPORT.md](./MIGRATION_REPORT.md) 了解移植历史
2. 使用迁移指南处理版本升级
3. 更新文档记录新的问题和解决方案

## 🔧 技术要点总结

### 关键修改
1. **Block Properties**: `Properties.of(MapColor)` → `Properties.of().mapColor(MapColor)`
2. **Font Rendering**: `Font.draw()` → `IDrawing.drawStringWithFont()`
3. **Interface Implementation**: 添加 `IDrawing` 接口实现
4. **Buffer Source**: `MultiBufferSource` → `null`

### 重要文件
- `MSDBlocks.java` - Block Properties 修改
- `RenderCustomTextSign.java` - 文本渲染修改
- `RenderPIDS.java` - PIDS 渲染修改
- `CustomTextSignScreen.java` - GUI 渲染修改

### 构建命令
```bash
./gradlew build --project-prop buildVersion=1.20.1
```

## 📝 维护说明

### 更新文档时
1. 保持版本信息的准确性
2. 及时记录新发现的问题
3. 更新兼容性信息
4. 添加新的最佳实践

### 代码变更时
1. 更新相关文档
2. 测试所有功能
3. 记录 API 变化
4. 更新示例代码

---

**最后更新**: 2024年 (Minecraft 1.20.1 + MTR 3.2.2 移植完成)
