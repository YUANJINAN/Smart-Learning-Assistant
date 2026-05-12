# 智能学习助手数据库设计

本项目使用 MySQL 作为本地与部署环境的业务数据库。Spring Boot 启动时会加载 `backend/src/main/resources/application.properties`、`backend/src/main/resources/schema.sql` 和 `backend/src/main/resources/data.sql`；根目录下的 `database/` 保留同样的脚本，便于手动初始化数据库。

## 连接信息

- 建库脚本：`docs/mysql-create-database.sql`
- JDBC URL：`jdbc:mysql://localhost:3306/smart_learning_assistant?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true`
- 默认数据库用户：`root`
- 默认数据库密码：空密码；如本机 MySQL 设置了密码，请通过环境变量 `MYSQL_PASSWORD` 覆盖。
- 学习者演示账号：`learner / learner123`
- 管理员演示账号：`admin / admin123`

数据库连接可以通过环境变量覆盖：

- `MYSQL_URL`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`

## 核心数据表

| 表名 | 用途 |
| --- | --- |
| `user_accounts` | 系统运行账号，区分学习者和管理员。 |
| `learning_profiles` | 学习档案，保存学习目标、水平、每日学习时长、兴趣和薄弱点。 |
| `chat_session`、`chat_message` | 智能问答会话和消息历史。 |
| `course_units`、`course_objectives` | 教学与练习流程使用的课程资源。 |
| `exercises`、`exercise_options`、`exercise_results` | 练习题、选项、提交记录和批改结果。 |
| `question`、`answer_record`、`wrong_question` | 设计文档中的题库、答题记录和错题本模型。 |
| `learning_plans`、`plan_tasks`、`learning_task` | 学习计划和可执行的每日任务。 |
| `learning_report` | 周报、月报、阶段复盘和下一步建议。 |
| `knowledge_items`、`knowledge_resource` | 可检索知识条目和后台维护的知识资源。 |
| `agent_configs`、`agent_consultations`、`agent_tasks` | 多智能体配置、咨询历史和后续任务。 |
| `admin_user`、`role`、`permission`、`role_permission` | 管理员账号和 RBAC 权限结构。 |
| `audit_logs`、`model_config` | 操作审计记录和 AI 模型策略配置。 |

## 部署脚本

- MySQL 建表脚本：`database/schema.sql`
- MySQL 初始化数据：`database/init_data.sql`
- H2 测试建表脚本：`backend/src/test/resources/test-schema.sql`
- H2 测试初始化数据：`backend/src/test/resources/test-data.sql`

当前实现使用 JDBC Repository 承担可执行持久层，`entity` 和 `mapper` 包保留设计文档中的分层边界，便于课程设计验收时说明数据模型与业务模型的对应关系。
