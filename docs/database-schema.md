# 智能学习助手数据库设计

本项目运行环境使用 MySQL 数据库，启动时由 `schema.sql` 自动建表，由 `data.sql` 自动写入演示数据。

## 连接信息

- 建库脚本: `docs/mysql-create-database.sql`
- JDBC URL: `jdbc:mysql://localhost:3306/smart_learning_assistant?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true`
- 默认用户名: `root`
- 默认密码: `root`
- 建表脚本: `src/main/resources/schema.sql`
- 初始数据: `src/main/resources/data.sql`

连接信息可以通过环境变量覆盖：

- `MYSQL_URL`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`

## 核心表

| 表名 | 说明 |
| --- | --- |
| `user_accounts` | 用户账号，区分学习者和管理员 |
| `learning_profiles` | 学习档案，保存目标、水平、每日学习时长 |
| `profile_interests` | 学习兴趣，多值明细表 |
| `profile_weak_points` | 薄弱点，多值明细表 |
| `course_units` | 课程单元 |
| `course_objectives` | 课程目标，多值明细表 |
| `exercises` | 练习题主体 |
| `exercise_options` | 练习选项，多值明细表 |
| `exercise_results` | 用户提交和判分记录 |
| `learning_plans` | 学习计划 |
| `plan_tasks` | 计划任务，多值明细表 |
| `knowledge_items` | 知识库条目 |

## 需求对应关系

| 需求模块 | 数据表 |
| --- | --- |
| 用户与学习档案 | `user_accounts`, `learning_profiles`, `profile_interests`, `profile_weak_points` |
| 智能教学与练习 | `course_units`, `course_objectives`, `exercises`, `exercise_options`, `exercise_results` |
| 计划与复习 | `learning_plans`, `plan_tasks`, `exercise_results` |
| 知识库 | `knowledge_items` |
| 后台管理 | 对用户、课程、练习、计划、知识库表进行统计 |

## 持久化说明

首次运行前先在 MySQL 中创建库：

```sql
CREATE DATABASE IF NOT EXISTS smart_learning_assistant
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
```

应用启动后会自动执行 `schema.sql` 和 `data.sql`。测试环境使用 `src/test/resources` 下的 H2 兼容脚本，不影响正式 MySQL 配置。
