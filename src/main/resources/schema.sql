CREATE TABLE IF NOT EXISTS user_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS learning_profiles (
    user_id BIGINT PRIMARY KEY,
    goal VARCHAR(500) NOT NULL,
    level VARCHAR(80) NOT NULL,
    daily_target_minutes INT NOT NULL,
    CONSTRAINT fk_learning_profiles_user FOREIGN KEY (user_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS profile_interests (
    user_id BIGINT NOT NULL,
    interest VARCHAR(120) NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (user_id, position),
    CONSTRAINT fk_profile_interests_profile FOREIGN KEY (user_id) REFERENCES learning_profiles(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS profile_weak_points (
    user_id BIGINT NOT NULL,
    weak_point VARCHAR(120) NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (user_id, position),
    CONSTRAINT fk_profile_weak_points_profile FOREIGN KEY (user_id) REFERENCES learning_profiles(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS course_units (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    subject VARCHAR(120) NOT NULL,
    difficulty VARCHAR(60) NOT NULL,
    summary VARCHAR(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS course_objectives (
    course_id BIGINT NOT NULL,
    objective VARCHAR(300) NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (course_id, position),
    CONSTRAINT fk_course_objectives_course FOREIGN KEY (course_id) REFERENCES course_units(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exercises (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    question VARCHAR(1000) NOT NULL,
    answer VARCHAR(20) NOT NULL,
    explanation VARCHAR(1000) NOT NULL,
    CONSTRAINT fk_exercises_course FOREIGN KEY (course_id) REFERENCES course_units(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exercise_options (
    exercise_id BIGINT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (exercise_id, position),
    CONSTRAINT fk_exercise_options_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exercise_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    submitted_answer VARCHAR(20) NOT NULL,
    correct BOOLEAN NOT NULL,
    correct_answer VARCHAR(20) NOT NULL,
    explanation VARCHAR(1000) NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_exercise_results_user FOREIGN KEY (user_id) REFERENCES user_accounts(id),
    CONSTRAINT fk_exercise_results_exercise FOREIGN KEY (exercise_id) REFERENCES exercises(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS learning_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    progress INT NOT NULL,
    CONSTRAINT fk_learning_plans_user FOREIGN KEY (user_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS plan_tasks (
    plan_id BIGINT NOT NULL,
    task VARCHAR(300) NOT NULL,
    position INT NOT NULL,
    PRIMARY KEY (plan_id, position),
    CONSTRAINT fk_plan_tasks_plan FOREIGN KEY (plan_id) REFERENCES learning_plans(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS knowledge_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    category VARCHAR(120) NOT NULL,
    content VARCHAR(2000) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS agent_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    responsibility VARCHAR(500) NOT NULL,
    model_name VARCHAR(120) NOT NULL,
    temperature DECIMAL(3,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(120) NOT NULL,
    detail VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_logs_user_id (user_id),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES user_accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
