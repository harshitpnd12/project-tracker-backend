package com.projectmanagement.repository;

import java.util.List;

import com.projectmanagement.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IssueRepository extends JpaRepository<Issue, Long> {

    public List<Issue> findByProjectId(Long id);
}
