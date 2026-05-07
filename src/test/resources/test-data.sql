MERGE INTO user_accounts (id, username, display_name, role, status, created_at, last_login_at)
    KEY (id) VALUES (1, 'learner', '示例学习者', 'LEARNER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
MERGE INTO user_accounts (id, username, display_name, role, status, created_at, last_login_at)
    KEY (id) VALUES (2, 'admin', '系统管理员', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO learning_profiles (user_id, goal, level, daily_target_minutes)
    KEY (user_id) VALUES (1, '通过 Java Web 课程项目验收', '进阶', 60);

MERGE INTO course_units (id, title, subject, difficulty, summary)
    KEY (id) VALUES (1, 'Spring Boot REST 接口', 'Java Web', '进阶', '学习控制器、服务层和 JSON 接口设计。');

MERGE INTO exercises (id, course_id, question, answer, explanation)
    KEY (id) VALUES (1, 1, 'Spring Boot 中用于声明 REST 控制器的注解是？', 'B', '@RestController 会将返回对象序列化为 HTTP 响应体。');
MERGE INTO exercises (id, course_id, question, answer, explanation)
    KEY (id) VALUES (2, 1, '服务层通常负责什么？', 'B', '服务层承载业务规则，控制器负责请求响应协调。');
MERGE INTO exercises (id, course_id, question, answer, explanation)
    KEY (id) VALUES (3, 1, '控制器通常负责什么？', 'A', '控制器负责接收请求并返回响应。');
MERGE INTO exercises (id, course_id, question, answer, explanation)
    KEY (id) VALUES (4, 1, '知识库用于什么？', 'C', '知识库沉淀可检索的学习资料。');

MERGE INTO exercise_options (exercise_id, option_text, position) KEY (exercise_id, position) VALUES (1, 'A. @Service', 0);
MERGE INTO exercise_options (exercise_id, option_text, position) KEY (exercise_id, position) VALUES (1, 'B. @RestController', 1);
MERGE INTO exercise_options (exercise_id, option_text, position) KEY (exercise_id, position) VALUES (1, 'C. @Repository', 2);
MERGE INTO exercise_options (exercise_id, option_text, position) KEY (exercise_id, position) VALUES (1, 'D. @Configuration', 3);

MERGE INTO learning_plans (id, user_id, title, start_date, end_date, progress)
    KEY (id) VALUES (1, 1, 'Java Web 两周提升计划', CURRENT_DATE, DATEADD('DAY', 14, CURRENT_DATE), 35);
MERGE INTO learning_plans (id, user_id, title, start_date, end_date, progress)
    KEY (id) VALUES (2, 1, '智能学习助手演示准备', DATEADD('DAY', 1, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), 20);

MERGE INTO knowledge_items (id, title, category, content, updated_at)
    KEY (id) VALUES (1, 'REST 控制器', 'Java Web', '使用 @RestController 暴露 JSON API。', CURRENT_TIMESTAMP);
MERGE INTO knowledge_items (id, title, category, content, updated_at)
    KEY (id) VALUES (2, '艾宾浩斯复习节奏', '学习方法', '新知识学习后在 1 天、3 天、7 天进行复习。', CURRENT_TIMESTAMP);
MERGE INTO knowledge_items (id, title, category, content, updated_at)
    KEY (id) VALUES (3, '错题复盘模板', '智能练习', '记录错因、知识点和下次复习日期。', CURRENT_TIMESTAMP);

MERGE INTO agent_configs (id, name, responsibility, model_name, temperature, status, updated_at)
    KEY (id) VALUES (1, '教学智能体', '负责概念讲解、例题引导和答疑反馈。', 'rule-based-tutor', 0.30, 'ACTIVE', CURRENT_TIMESTAMP);
MERGE INTO agent_configs (id, name, responsibility, model_name, temperature, status, updated_at)
    KEY (id) VALUES (2, '规划智能体', '负责根据学习档案生成阶段计划和每日任务。', 'rule-based-planner', 0.20, 'ACTIVE', CURRENT_TIMESTAMP);
MERGE INTO agent_configs (id, name, responsibility, model_name, temperature, status, updated_at)
    KEY (id) VALUES (3, '复习智能体', '负责错题分析、知识点回顾和复习节奏提醒。', 'rule-based-reviewer', 0.25, 'ACTIVE', CURRENT_TIMESTAMP);
