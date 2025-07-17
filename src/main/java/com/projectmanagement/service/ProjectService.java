package com.projectmanagement.service;

import com.projectmanagement.model.Chat;
import com.projectmanagement.model.Project;
import com.projectmanagement.model.User;

import java.util.List;

public interface ProjectService {

    Project createProject(Project project, User user) throws Exception;

    List<Project> getProjectByTeam(User user, String category, String tag) throws Exception;

    Project getProjectById(Long projectId) throws Exception;

    void deleteProject(Long projectId, Long userId) throws Exception;

    Project updateProject(Project updatedProject, Long Id) throws Exception;

    void addUserToProject(Long projectId, Long userId) throws Exception;

    void removeUserFromProject(Long projectId, Long userId) throws Exception;

    Chat getChatByProjectId(Long ProjectId) throws Exception;

    List<Project> searchProjects(String keyword, User user) throws Exception;
}

