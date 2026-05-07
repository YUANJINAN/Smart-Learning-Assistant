package com.example.smartlearningassistant.service;

import com.example.smartlearningassistant.model.AgentConfig;
import com.example.smartlearningassistant.model.AuditLog;
import com.example.smartlearningassistant.model.CourseUnit;
import com.example.smartlearningassistant.model.Exercise;
import com.example.smartlearningassistant.model.ExerciseResult;
import com.example.smartlearningassistant.model.KnowledgeItem;
import com.example.smartlearningassistant.model.LearningPlan;
import com.example.smartlearningassistant.model.LearningProfile;
import com.example.smartlearningassistant.model.MistakeRecord;
import com.example.smartlearningassistant.model.UserAccount;
import com.example.smartlearningassistant.model.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class LearningAssistantRepository {
    private final JdbcTemplate jdbc;

    public LearningAssistantRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<UserAccount> findUsers() {
        return jdbc.query("""
                SELECT id, username, display_name, role, status, created_at, last_login_at
                FROM user_accounts
                ORDER BY id
                """, (rs, rowNum) -> mapUser(rs));
    }

    public Optional<UserAccount> findUserByUsername(String username) {
        List<UserAccount> accounts = jdbc.query("""
                SELECT id, username, display_name, role, status, created_at, last_login_at
                FROM user_accounts
                WHERE username = ?
                """, (rs, rowNum) -> mapUser(rs), username);
        return accounts.stream().findFirst();
    }

    public boolean userExists(Long userId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM user_accounts WHERE id = ?", Integer.class, userId);
        return count != null && count > 0;
    }

    public UserAccount insertUser(UserAccount user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO user_accounts (username, display_name, role, status, created_at, last_login_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getDisplayName());
            statement.setString(3, user.getRole().name());
            statement.setString(4, user.getStatus());
            statement.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            statement.setTimestamp(6, Timestamp.valueOf(user.getLastLoginAt()));
            return statement;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public void updateUserStatus(Long userId, String status) {
        jdbc.update("UPDATE user_accounts SET status = ? WHERE id = ?", status, userId);
    }

    public void touchLastLogin(Long userId) {
        jdbc.update("UPDATE user_accounts SET last_login_at = ? WHERE id = ?", Timestamp.valueOf(LocalDateTime.now()), userId);
    }

    public Optional<LearningProfile> findProfile(Long userId) {
        List<LearningProfile> profiles = jdbc.query("""
                SELECT user_id, goal, level, daily_target_minutes
                FROM learning_profiles
                WHERE user_id = ?
                """, (rs, rowNum) -> new LearningProfile(
                rs.getLong("user_id"),
                rs.getString("goal"),
                rs.getString("level"),
                findStrings("profile_interests", "interest", "user_id", userId),
                findStrings("profile_weak_points", "weak_point", "user_id", userId),
                rs.getInt("daily_target_minutes")
        ), userId);
        return profiles.stream().findFirst();
    }

    public LearningProfile saveProfile(LearningProfile profile) {
        jdbc.update("""
                INSERT INTO learning_profiles (user_id, goal, level, daily_target_minutes)
                VALUES (?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    goal = VALUES(goal),
                    level = VALUES(level),
                    daily_target_minutes = VALUES(daily_target_minutes)
                """, profile.getUserId(), profile.getGoal(), profile.getLevel(), profile.getDailyTargetMinutes());
        replaceStrings("profile_interests", "interest", "user_id", profile.getUserId(), profile.getInterests());
        replaceStrings("profile_weak_points", "weak_point", "user_id", profile.getUserId(), profile.getWeakPoints());
        return profile;
    }

    public List<CourseUnit> findCourses() {
        return jdbc.query("""
                SELECT id, title, subject, difficulty, summary
                FROM course_units
                ORDER BY id
                """, (rs, rowNum) -> new CourseUnit(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("subject"),
                rs.getString("difficulty"),
                rs.getString("summary"),
                findStrings("course_objectives", "objective", "course_id", rs.getLong("id"))
        ));
    }

    public boolean courseExists(Long courseId) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM course_units WHERE id = ?", Integer.class, courseId);
        return count != null && count > 0;
    }

    public CourseUnit saveCourse(CourseUnit course) {
        if (course.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO course_units (title, subject, difficulty, summary)
                        VALUES (?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, course.getTitle());
                statement.setString(2, course.getSubject());
                statement.setString(3, course.getDifficulty());
                statement.setString(4, course.getSummary());
                return statement;
            }, keyHolder);
            course.setId(keyHolder.getKey().longValue());
        } else {
            jdbc.update("""
                    INSERT INTO course_units (id, title, subject, difficulty, summary)
                    VALUES (?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        title = VALUES(title),
                        subject = VALUES(subject),
                        difficulty = VALUES(difficulty),
                        summary = VALUES(summary)
                    """, course.getId(), course.getTitle(), course.getSubject(), course.getDifficulty(), course.getSummary());
        }
        replaceStrings("course_objectives", "objective", "course_id", course.getId(), course.getObjectives());
        return course;
    }

    public List<Exercise> findExercises(Long courseId) {
        return jdbc.query("""
                SELECT id, course_id, question, answer, explanation
                FROM exercises
                WHERE ? IS NULL OR course_id = ?
                ORDER BY id
                """, (rs, rowNum) -> new Exercise(
                rs.getLong("id"),
                rs.getLong("course_id"),
                rs.getString("question"),
                findStrings("exercise_options", "option_text", "exercise_id", rs.getLong("id")),
                rs.getString("answer"),
                rs.getString("explanation")
        ), courseId, courseId);
    }

    public Optional<Exercise> findExercise(Long exerciseId) {
        List<Exercise> items = jdbc.query("""
                SELECT id, course_id, question, answer, explanation
                FROM exercises
                WHERE id = ?
                """, (rs, rowNum) -> new Exercise(
                rs.getLong("id"),
                rs.getLong("course_id"),
                rs.getString("question"),
                findStrings("exercise_options", "option_text", "exercise_id", rs.getLong("id")),
                rs.getString("answer"),
                rs.getString("explanation")
        ), exerciseId);
        return items.stream().findFirst();
    }

    public Exercise saveExercise(Exercise exercise) {
        if (exercise.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO exercises (course_id, question, answer, explanation)
                        VALUES (?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, exercise.getCourseId());
                statement.setString(2, exercise.getQuestion());
                statement.setString(3, exercise.getAnswer());
                statement.setString(4, exercise.getExplanation());
                return statement;
            }, keyHolder);
            exercise.setId(keyHolder.getKey().longValue());
        } else {
            jdbc.update("""
                    INSERT INTO exercises (id, course_id, question, answer, explanation)
                    VALUES (?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        course_id = VALUES(course_id),
                        question = VALUES(question),
                        answer = VALUES(answer),
                        explanation = VALUES(explanation)
                    """, exercise.getId(), exercise.getCourseId(), exercise.getQuestion(), exercise.getAnswer(), exercise.getExplanation());
        }
        replaceStrings("exercise_options", "option_text", "exercise_id", exercise.getId(), exercise.getOptions());
        return exercise;
    }

    public void insertExerciseResult(Long userId, String submittedAnswer, ExerciseResult result) {
        jdbc.update("""
                INSERT INTO exercise_results (user_id, exercise_id, submitted_answer, correct, correct_answer, explanation, submitted_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, userId, result.getExerciseId(), submittedAnswer, result.isCorrect(), result.getCorrectAnswer(), result.getExplanation(), Timestamp.valueOf(result.getSubmittedAt()));
    }

    public long countSubmittedResults(Long userId) {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM exercise_results WHERE user_id = ?", Long.class, userId);
        return count == null ? 0 : count;
    }

    public long countCorrectResults(Long userId) {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM exercise_results WHERE user_id = ? AND correct = TRUE", Long.class, userId);
        return count == null ? 0 : count;
    }

    public int averagePlanProgress(Long userId) {
        Number progress = jdbc.queryForObject("SELECT COALESCE(AVG(progress), 0) FROM learning_plans WHERE user_id = ?", Number.class, userId);
        return progress == null ? 0 : Math.round(progress.floatValue());
    }

    public List<MistakeRecord> findMistakes(Long userId) {
        return jdbc.query("""
                SELECT er.exercise_id, e.question, er.submitted_answer, er.correct_answer, er.explanation, er.submitted_at
                FROM exercise_results er
                JOIN exercises e ON er.exercise_id = e.id
                WHERE er.user_id = ? AND er.correct = FALSE
                ORDER BY er.submitted_at DESC
                """, (rs, rowNum) -> new MistakeRecord(
                rs.getLong("exercise_id"),
                rs.getString("question"),
                rs.getString("submitted_answer"),
                rs.getString("correct_answer"),
                rs.getString("explanation"),
                rs.getTimestamp("submitted_at").toLocalDateTime()
        ), userId);
    }

    public List<LearningPlan> findPlans(Long userId) {
        return jdbc.query("""
                SELECT id, user_id, title, start_date, end_date, progress
                FROM learning_plans
                WHERE ? IS NULL OR user_id = ?
                ORDER BY id
                """, (rs, rowNum) -> new LearningPlan(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("title"),
                rs.getDate("start_date").toLocalDate(),
                rs.getDate("end_date").toLocalDate(),
                findStrings("plan_tasks", "task", "plan_id", rs.getLong("id")),
                rs.getInt("progress")
        ), userId, userId);
    }

    public LearningPlan savePlan(LearningPlan plan) {
        if (plan.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO learning_plans (user_id, title, start_date, end_date, progress)
                        VALUES (?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                statement.setLong(1, plan.getUserId());
                statement.setString(2, plan.getTitle());
                statement.setDate(3, Date.valueOf(plan.getStartDate()));
                statement.setDate(4, Date.valueOf(plan.getEndDate()));
                statement.setInt(5, plan.getProgress());
                return statement;
            }, keyHolder);
            plan.setId(keyHolder.getKey().longValue());
        } else {
            jdbc.update("""
                    INSERT INTO learning_plans (id, user_id, title, start_date, end_date, progress)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        user_id = VALUES(user_id),
                        title = VALUES(title),
                        start_date = VALUES(start_date),
                        end_date = VALUES(end_date),
                        progress = VALUES(progress)
                    """, plan.getId(), plan.getUserId(), plan.getTitle(), plan.getStartDate(), plan.getEndDate(), plan.getProgress());
        }
        replaceStrings("plan_tasks", "task", "plan_id", plan.getId(), plan.getTasks());
        return plan;
    }

    public List<KnowledgeItem> searchKnowledge(String keyword) {
        String key = "%" + keyword.toLowerCase() + "%";
        return jdbc.query("""
                SELECT id, title, category, content, updated_at
                FROM knowledge_items
                WHERE LOWER(title) LIKE ? OR LOWER(category) LIKE ? OR LOWER(content) LIKE ?
                ORDER BY id
                """, (rs, rowNum) -> new KnowledgeItem(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("category"),
                rs.getString("content"),
                rs.getTimestamp("updated_at").toLocalDateTime()
        ), key, key, key);
    }

    public KnowledgeItem saveKnowledge(KnowledgeItem item) {
        LocalDateTime updatedAt = item.getUpdatedAt() == null ? LocalDateTime.now() : item.getUpdatedAt();
        item.setUpdatedAt(updatedAt);
        if (item.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO knowledge_items (title, category, content, updated_at)
                        VALUES (?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, item.getTitle());
                statement.setString(2, item.getCategory());
                statement.setString(3, item.getContent());
                statement.setTimestamp(4, Timestamp.valueOf(updatedAt));
                return statement;
            }, keyHolder);
            item.setId(keyHolder.getKey().longValue());
        } else {
            jdbc.update("""
                    INSERT INTO knowledge_items (id, title, category, content, updated_at)
                    VALUES (?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        title = VALUES(title),
                        category = VALUES(category),
                        content = VALUES(content),
                        updated_at = VALUES(updated_at)
                    """, item.getId(), item.getTitle(), item.getCategory(), item.getContent(), Timestamp.valueOf(updatedAt));
        }
        return item;
    }

    public List<AgentConfig> findAgentConfigs() {
        return jdbc.query("""
                SELECT id, name, responsibility, model_name, temperature, status, updated_at
                FROM agent_configs
                ORDER BY id
                """, (rs, rowNum) -> new AgentConfig(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("responsibility"),
                rs.getString("model_name"),
                rs.getDouble("temperature"),
                rs.getString("status"),
                rs.getTimestamp("updated_at").toLocalDateTime()
        ));
    }

    public AgentConfig saveAgentConfig(AgentConfig config) {
        LocalDateTime updatedAt = LocalDateTime.now();
        config.setUpdatedAt(updatedAt);
        if (config.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement statement = connection.prepareStatement("""
                        INSERT INTO agent_configs (name, responsibility, model_name, temperature, status, updated_at)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, config.getName());
                statement.setString(2, config.getResponsibility());
                statement.setString(3, config.getModelName());
                statement.setDouble(4, config.getTemperature());
                statement.setString(5, config.getStatus());
                statement.setTimestamp(6, Timestamp.valueOf(updatedAt));
                return statement;
            }, keyHolder);
            config.setId(keyHolder.getKey().longValue());
        } else {
            jdbc.update("""
                    INSERT INTO agent_configs (id, name, responsibility, model_name, temperature, status, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        name = VALUES(name),
                        responsibility = VALUES(responsibility),
                        model_name = VALUES(model_name),
                        temperature = VALUES(temperature),
                        status = VALUES(status),
                        updated_at = VALUES(updated_at)
                    """, config.getId(), config.getName(), config.getResponsibility(), config.getModelName(), config.getTemperature(), config.getStatus(), Timestamp.valueOf(updatedAt));
        }
        return config;
    }

    public void insertAuditLog(Long userId, String action, String detail) {
        jdbc.update("""
                INSERT INTO audit_logs (user_id, action, detail, created_at)
                VALUES (?, ?, ?, ?)
                """, userId, action, detail, Timestamp.valueOf(LocalDateTime.now()));
    }

    public List<AuditLog> findAuditLogs() {
        return jdbc.query("""
                SELECT id, user_id, action, detail, created_at
                FROM audit_logs
                ORDER BY created_at DESC, id DESC
                LIMIT 50
                """, (rs, rowNum) -> new AuditLog(
                rs.getLong("id"),
                rs.getObject("user_id", Long.class),
                rs.getString("action"),
                rs.getString("detail"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }

    public long count(String tableName) {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM " + tableName, Long.class);
        return count == null ? 0 : count;
    }

    public Map<String, Long> agentStatus() {
        Map<String, Long> status = new LinkedHashMap<>();
        for (AgentConfig config : findAgentConfigs()) {
            status.put(config.getName(), "ACTIVE".equalsIgnoreCase(config.getStatus()) ? 1L : 0L);
        }
        return status;
    }

    private UserAccount mapUser(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new UserAccount(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("display_name"),
                UserRole.valueOf(rs.getString("role")),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("last_login_at").toLocalDateTime()
        );
    }

    private List<String> findStrings(String tableName, String valueColumn, String ownerColumn, Long ownerId) {
        String sql = "SELECT " + valueColumn + " FROM " + tableName + " WHERE " + ownerColumn + " = ? ORDER BY position";
        return jdbc.query(sql, (rs, rowNum) -> rs.getString(valueColumn), ownerId);
    }

    private void replaceStrings(String tableName, String valueColumn, String ownerColumn, Long ownerId, List<String> values) {
        jdbc.update("DELETE FROM " + tableName + " WHERE " + ownerColumn + " = ?", ownerId);
        List<String> safeValues = values == null ? List.of() : values;
        for (int i = 0; i < safeValues.size(); i++) {
            jdbc.update("INSERT INTO " + tableName + " (" + ownerColumn + ", " + valueColumn + ", position) VALUES (?, ?, ?)",
                    ownerId, safeValues.get(i), i);
        }
    }
}
