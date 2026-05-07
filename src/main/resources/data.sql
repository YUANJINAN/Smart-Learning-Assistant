INSERT INTO user_accounts (id, username, display_name, role, status, created_at, last_login_at) VALUES
    (1, 'learner', '示例学习者', 'LEARNER', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'admin', '系统管理员', 'ADMIN', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    display_name = VALUES(display_name),
    role = VALUES(role),
    status = VALUES(status),
    last_login_at = VALUES(last_login_at);

INSERT INTO learning_profiles (user_id, goal, level, daily_target_minutes) VALUES
    (1, '通过 Java Web 课程项目验收', '进阶', 60),
    (2, '维护学习助手平台稳定运行', '管理员', 30)
ON DUPLICATE KEY UPDATE
    goal = VALUES(goal),
    level = VALUES(level),
    daily_target_minutes = VALUES(daily_target_minutes);

INSERT INTO profile_interests (user_id, interest, position) VALUES
    (1, 'Spring Boot', 0),
    (1, '智能学习', 1)
ON DUPLICATE KEY UPDATE interest = VALUES(interest);

INSERT INTO profile_weak_points (user_id, weak_point, position) VALUES
    (1, '接口设计', 0),
    (1, '错题复盘', 1)
ON DUPLICATE KEY UPDATE weak_point = VALUES(weak_point);

INSERT INTO course_units (id, title, subject, difficulty, summary) VALUES
    (1, 'Spring Boot REST 接口', 'Java Web', '进阶', '学习控制器、服务层和 JSON 接口设计。'),
    (2, '个性化学习计划', '学习方法', '基础', '根据目标、薄弱点和时间生成可执行计划。'),
    (3, '错题分析与知识库', '智能练习', '进阶', '沉淀错题解释、知识点标签和复习建议。')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    subject = VALUES(subject),
    difficulty = VALUES(difficulty),
    summary = VALUES(summary);

INSERT INTO course_objectives (course_id, objective, position) VALUES
    (1, '理解 REST 路由', 0),
    (1, '完成接口联调', 1),
    (1, '形成后端分层', 2),
    (2, '拆分学习目标', 0),
    (2, '安排复习节奏', 1),
    (3, '记录错因', 0),
    (3, '检索知识点', 1)
ON DUPLICATE KEY UPDATE objective = VALUES(objective);

INSERT INTO exercises (id, course_id, question, answer, explanation) VALUES
    (1, 1, 'Spring Boot 中用于声明 REST 控制器的注解是？', 'B', '@RestController 会将返回对象序列化为 HTTP 响应体。'),
    (2, 2, '学习计划的核心输入不包括哪一项？', 'D', '学习计划主要依赖目标、基础、薄弱点和时间资源。'),
    (3, 3, '错题复盘最应该沉淀的信息是？', 'B', '错因和知识点可用于后续复习和个性化推荐。'),
    (4, 1, '服务层通常负责什么？', 'B', '服务层承载业务规则，控制器负责请求响应协调。')
ON DUPLICATE KEY UPDATE
    course_id = VALUES(course_id),
    question = VALUES(question),
    answer = VALUES(answer),
    explanation = VALUES(explanation);

INSERT INTO exercise_options (exercise_id, option_text, position) VALUES
    (1, 'A. @Service', 0),
    (1, 'B. @RestController', 1),
    (1, 'C. @Repository', 2),
    (1, 'D. @Configuration', 3),
    (2, 'A. 学习目标', 0),
    (2, 'B. 薄弱点', 1),
    (2, 'C. 每日可用时间', 2),
    (2, 'D. 显卡型号', 3),
    (3, 'A. 答案截图', 0),
    (3, 'B. 错因和知识点', 1),
    (3, 'C. 浏览器版本', 2),
    (3, 'D. 随机编号', 3),
    (4, 'A. 页面样式', 0),
    (4, 'B. 业务规则', 1),
    (4, 'C. 图片压缩', 2),
    (4, 'D. 端口扫描', 3)
ON DUPLICATE KEY UPDATE option_text = VALUES(option_text);

INSERT INTO learning_plans (id, user_id, title, start_date, end_date, progress) VALUES
    (1, 1, 'Java Web 两周提升计划', CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 14 DAY), 35),
    (2, 1, '智能学习助手演示准备', DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), 20)
ON DUPLICATE KEY UPDATE
    user_id = VALUES(user_id),
    title = VALUES(title),
    start_date = VALUES(start_date),
    end_date = VALUES(end_date),
    progress = VALUES(progress);

INSERT INTO plan_tasks (plan_id, task, position) VALUES
    (1, '完成 REST 接口练习', 0),
    (1, '复盘错题', 1),
    (1, '补充知识库条目', 2),
    (2, '准备功能演示', 0),
    (2, '整理需求对应关系', 1),
    (2, '完成测试验证', 2)
ON DUPLICATE KEY UPDATE task = VALUES(task);

INSERT INTO knowledge_items (id, title, category, content, updated_at) VALUES
    (1, 'REST 控制器', 'Java Web', '使用 @RestController 暴露 JSON API，配合 @GetMapping、@PostMapping 等注解组织资源。', CURRENT_TIMESTAMP),
    (2, '艾宾浩斯复习节奏', '学习方法', '新知识学习后在 1 天、3 天、7 天进行复习，有助于降低遗忘率。', CURRENT_TIMESTAMP),
    (3, '错题复盘模板', '智能练习', '记录题目、错误答案、正确答案、错因、关联知识点和下次复习日期。', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    category = VALUES(category),
    content = VALUES(content),
    updated_at = VALUES(updated_at);

INSERT INTO agent_configs (id, name, responsibility, model_name, temperature, status, updated_at) VALUES
    (1, '教学智能体', '负责概念讲解、例题引导和答疑反馈。', 'rule-based-tutor', 0.30, 'ACTIVE', CURRENT_TIMESTAMP),
    (2, '规划智能体', '负责根据学习档案生成阶段计划和每日任务。', 'rule-based-planner', 0.20, 'ACTIVE', CURRENT_TIMESTAMP),
    (3, '复习智能体', '负责错题分析、知识点回顾和复习节奏提醒。', 'rule-based-reviewer', 0.25, 'ACTIVE', CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    responsibility = VALUES(responsibility),
    model_name = VALUES(model_name),
    temperature = VALUES(temperature),
    status = VALUES(status),
    updated_at = VALUES(updated_at);
