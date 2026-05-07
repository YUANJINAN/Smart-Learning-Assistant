package com.example.smartlearningassistant.controller;

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
import com.example.smartlearningassistant.model.UserStatusUpdate;
import com.example.smartlearningassistant.service.LearningAssistantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LearningAssistantController {
    private final LearningAssistantService service;

    public LearningAssistantController(LearningAssistantService service) {
        this.service = service;
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return service.login(request);
    }

    @GetMapping("/users")
    public List<UserAccount> users() {
        return service.listUsers();
    }

    @PostMapping("/users")
    public UserAccount createUser(@RequestBody UserAccount user) {
        return service.createUser(user);
    }

    @PutMapping("/users/{userId}/status")
    public void updateUserStatus(@PathVariable Long userId, @RequestBody UserStatusUpdate update) {
        service.updateUserStatus(userId, update.getStatus());
    }

    @GetMapping("/users/{userId}/profile")
    public LearningProfile profile(@PathVariable Long userId) {
        return service.getProfile(userId);
    }

    @PutMapping("/users/{userId}/profile")
    public LearningProfile updateProfile(@PathVariable Long userId, @RequestBody LearningProfile profile) {
        return service.updateProfile(userId, profile);
    }

    @GetMapping("/users/{userId}/progress")
    public LearningProgress progress(@PathVariable Long userId) {
        return service.progress(userId);
    }

    @GetMapping("/users/{userId}/mistakes")
    public List<MistakeRecord> mistakes(@PathVariable Long userId) {
        return service.mistakes(userId);
    }

    @GetMapping("/courses")
    public List<CourseUnit> courses() {
        return service.listCourses();
    }

    @PostMapping("/courses")
    public CourseUnit saveCourse(@RequestBody CourseUnit course) {
        return service.saveCourse(course);
    }

    @GetMapping("/exercises")
    public List<Exercise> exercises(@RequestParam(required = false) Long courseId) {
        return service.listExercises(courseId);
    }

    @PostMapping("/exercises")
    public Exercise saveExercise(@RequestBody Exercise exercise) {
        return service.saveExercise(exercise);
    }

    @PostMapping("/exercises/submit")
    public ExerciseResult submitExercise(@RequestBody ExerciseSubmission submission) {
        return service.submitExercise(submission);
    }

    @GetMapping("/plans")
    public List<LearningPlan> plans(@RequestParam(required = false) Long userId) {
        return service.listPlans(userId);
    }

    @PostMapping("/plans")
    public LearningPlan savePlan(@RequestBody LearningPlan plan) {
        return service.savePlan(plan);
    }

    @GetMapping("/knowledge")
    public List<KnowledgeItem> knowledge(@RequestParam(required = false) String keyword) {
        return service.searchKnowledge(keyword);
    }

    @PostMapping("/knowledge")
    public KnowledgeItem saveKnowledge(@RequestBody KnowledgeItem item) {
        return service.saveKnowledge(item);
    }

    @PostMapping("/agents/ask")
    public AgentReply askAgents(@RequestBody AgentMessage message) {
        return service.askAgents(message);
    }

    @GetMapping("/admin/dashboard")
    public AdminDashboard dashboard() {
        return service.dashboard();
    }

    @GetMapping("/admin/audit-logs")
    public List<AuditLog> auditLogs() {
        return service.auditLogs();
    }

    @GetMapping("/admin/agent-configs")
    public List<AgentConfig> agentConfigs() {
        return service.listAgentConfigs();
    }

    @PostMapping("/admin/agent-configs")
    public AgentConfig saveAgentConfig(@RequestBody AgentConfig config) {
        return service.saveAgentConfig(config);
    }
}
