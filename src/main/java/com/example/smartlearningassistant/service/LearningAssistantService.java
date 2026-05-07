package com.example.smartlearningassistant.service;

import com.example.smartlearningassistant.model.AdminDashboard;
import com.example.smartlearningassistant.model.AgentConfig;
import com.example.smartlearningassistant.model.AgentMessage;
import com.example.smartlearningassistant.model.AgentReply;
import com.example.smartlearningassistant.model.AuditLog;
import com.example.smartlearningassistant.model.CourseUnit;
import com.example.smartlearningassistant.model.Exercise;
import com.example.smartlearningassistant.model.ExerciseResult;
import com.example.smartlearningassistant.model.ExerciseSubmission;
import com.example.smartlearningassistant.model.KnowledgeItem;
import com.example.smartlearningassistant.model.LearningPlan;
import com.example.smartlearningassistant.model.LearningProfile;
import com.example.smartlearningassistant.model.LearningProgress;
import com.example.smartlearningassistant.model.LoginRequest;
import com.example.smartlearningassistant.model.LoginResponse;
import com.example.smartlearningassistant.model.MistakeRecord;
import com.example.smartlearningassistant.model.UserAccount;
import com.example.smartlearningassistant.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class LearningAssistantService {
    private final LearningAssistantRepository repository;

    public LearningAssistantService(LearningAssistantRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = defaultValue(request.getUsername(), "learner");
        UserAccount account = repository.findUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalArgumentException("账号已被停用");
        }
        repository.touchLastLogin(account.getId());
        repository.insertAuditLog(account.getId(), "LOGIN", "用户登录系统");
        return new LoginResponse(account);
    }

    public List<UserAccount> listUsers() {
        return repository.findUsers();
    }

    @Transactional
    public UserAccount createUser(UserAccount user) {
        UserRole role = user.getRole() == null ? UserRole.LEARNER : user.getRole();
        UserAccount account = new UserAccount(
                null,
                defaultValue(user.getUsername(), "learner" + System.currentTimeMillis()),
                defaultValue(user.getDisplayName(), "新学习者"),
                role,
                defaultValue(user.getStatus(), "ACTIVE"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        UserAccount saved = repository.insertUser(account);
        repository.saveProfile(new LearningProfile(saved.getId(), "建立个性化学习路径", "入门", List.of("Java"), List.of("错题复盘"), 45));
        repository.insertAuditLog(saved.getId(), "CREATE_USER", "创建用户 " + saved.getUsername());
        return saved;
    }

    @Transactional
    public void updateUserStatus(Long userId, String status) {
        ensureUserExists(userId);
        String normalized = defaultValue(status, "ACTIVE").toUpperCase();
        if (!List.of("ACTIVE", "DISABLED").contains(normalized)) {
            throw new IllegalArgumentException("用户状态只能是 ACTIVE 或 DISABLED");
        }
        repository.updateUserStatus(userId, normalized);
        repository.insertAuditLog(userId, "UPDATE_USER_STATUS", "更新用户状态为 " + normalized);
    }

    public LearningProfile getProfile(Long userId) {
        ensureUserExists(userId);
        return repository.findProfile(userId)
                .orElseGet(() -> repository.saveProfile(new LearningProfile(userId, "完善学习目标", "入门", List.of(), List.of(), 30)));
    }

    @Transactional
    public LearningProfile updateProfile(Long userId, LearningProfile profile) {
        ensureUserExists(userId);
        profile.setUserId(userId);
        LearningProfile saved = repository.saveProfile(profile);
        repository.insertAuditLog(userId, "UPDATE_PROFILE", "更新学习档案");
        return saved;
    }

    public LearningProgress progress(Long userId) {
        ensureUserExists(userId);
        long submitted = repository.countSubmittedResults(userId);
        long correct = repository.countCorrectResults(userId);
        long mistakes = submitted - correct;
        int accuracy = submitted == 0 ? 0 : Math.round((correct * 100f) / submitted);
        int planProgress = repository.averagePlanProgress(userId);
        String suggestion;
        if (submitted == 0) {
            suggestion = "先完成一组练习，系统会根据结果生成复习建议。";
        } else if (accuracy < 70) {
            suggestion = "正确率偏低，建议优先复盘错题并回看相关知识点。";
        } else {
            suggestion = "当前掌握情况较稳定，可以继续推进下一阶段学习计划。";
        }
        return new LearningProgress(submitted, correct, mistakes, accuracy, planProgress, suggestion);
    }

    public List<MistakeRecord> mistakes(Long userId) {
        ensureUserExists(userId);
        return repository.findMistakes(userId);
    }

    public List<CourseUnit> listCourses() {
        return repository.findCourses();
    }

    @Transactional
    public CourseUnit saveCourse(CourseUnit course) {
        CourseUnit saved = repository.saveCourse(course);
        repository.insertAuditLog(null, "SAVE_COURSE", "保存课程 " + saved.getTitle());
        return saved;
    }

    public List<Exercise> listExercises(Long courseId) {
        return repository.findExercises(courseId);
    }

    @Transactional
    public Exercise saveExercise(Exercise exercise) {
        if (!repository.courseExists(exercise.getCourseId())) {
            throw new IllegalArgumentException("课程不存在");
        }
        return repository.saveExercise(exercise);
    }

    @Transactional
    public ExerciseResult submitExercise(ExerciseSubmission submission) {
        ensureUserExists(submission.getUserId());
        Exercise exercise = repository.findExercise(submission.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("练习不存在"));
        String submittedAnswer = defaultValue(submission.getAnswer(), "").trim();
        boolean correct = exercise.getAnswer().equalsIgnoreCase(submittedAnswer);
        ExerciseResult result = new ExerciseResult(exercise.getId(), correct, exercise.getAnswer(), exercise.getExplanation(), LocalDateTime.now());
        repository.insertExerciseResult(submission.getUserId(), submittedAnswer, result);
        repository.insertAuditLog(submission.getUserId(), "SUBMIT_EXERCISE", "提交练习 " + exercise.getId() + "，结果：" + (correct ? "正确" : "错误"));
        return result;
    }

    public List<LearningPlan> listPlans(Long userId) {
        if (userId != null) {
            ensureUserExists(userId);
        }
        return repository.findPlans(userId);
    }

    @Transactional
    public LearningPlan savePlan(LearningPlan plan) {
        ensureUserExists(plan.getUserId());
        plan.setProgress(Math.max(0, Math.min(100, plan.getProgress())));
        LearningPlan saved = repository.savePlan(plan);
        repository.insertAuditLog(plan.getUserId(), "SAVE_PLAN", "保存学习计划 " + saved.getTitle());
        return saved;
    }

    public List<KnowledgeItem> searchKnowledge(String keyword) {
        return repository.searchKnowledge(defaultValue(keyword, ""));
    }

    @Transactional
    public KnowledgeItem saveKnowledge(KnowledgeItem item) {
        item.setUpdatedAt(LocalDateTime.now());
        KnowledgeItem saved = repository.saveKnowledge(item);
        repository.insertAuditLog(null, "SAVE_KNOWLEDGE", "保存知识库条目 " + saved.getTitle());
        return saved;
    }

    public List<AgentConfig> listAgentConfigs() {
        return repository.findAgentConfigs();
    }

    @Transactional
    public AgentConfig saveAgentConfig(AgentConfig config) {
        config.setStatus(defaultValue(config.getStatus(), "ACTIVE").toUpperCase());
        config.setTemperature(Math.max(0.0, Math.min(1.0, config.getTemperature())));
        AgentConfig saved = repository.saveAgentConfig(config);
        repository.insertAuditLog(null, "SAVE_AGENT_CONFIG", "保存智能体配置 " + saved.getName());
        return saved;
    }

    public List<AuditLog> auditLogs() {
        return repository.findAuditLogs();
    }

    public AgentReply askAgents(AgentMessage message) {
        ensureUserExists(message.getUserId());
        LearningProfile profile = getProfile(message.getUserId());
        String topic = defaultValue(message.getTopic(), "综合学习");
        String question = defaultValue(message.getQuestion(), "请生成学习建议");
        List<String> resources = searchKnowledge(topic).stream()
                .map(KnowledgeItem::getTitle)
                .limit(3)
                .toList();
        if (resources.isEmpty()) {
            resources = List.of("课程知识库", "错题复盘记录", "阶段学习计划");
        }
        String weakPoints = profile.getWeakPoints() == null || profile.getWeakPoints().isEmpty()
                ? "当前未填写薄弱点"
                : String.join("、", profile.getWeakPoints());
        return new AgentReply(
                "教学智能体：围绕“" + question + "”，先用例题建立概念，再通过练习检查掌握程度。",
                "规划智能体：结合目标“" + profile.getGoal() + "”，建议每天投入 " + profile.getDailyTargetMinutes() + " 分钟，优先处理薄弱点 " + weakPoints + "。",
                "复习智能体：今天完成新知学习后，1 天、3 天、7 天分别安排回顾，并把错题加入知识库标签。",
                resources
        );
    }

    public AdminDashboard dashboard() {
        return new AdminDashboard(
                repository.count("user_accounts"),
                repository.count("course_units"),
                repository.count("exercises"),
                repository.count("learning_plans"),
                repository.count("knowledge_items"),
                new LinkedHashMap<>(repository.agentStatus())
        );
    }

    private void ensureUserExists(Long userId) {
        if (userId == null || !repository.userExists(userId)) {
            throw new IllegalArgumentException("用户不存在");
        }
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
