package com.projectmanagement.service;

import com.projectmanagement.model.PlanType;
import com.projectmanagement.model.Subscription;
import com.projectmanagement.model.User;

public interface SubcriptionService {

    Subscription createSubscription(User User);

    Subscription getUserSubscription(Long userId) throws Exception;

    Subscription upgradeSubscription(Long userId, PlanType planType);

    boolean isValid(Subscription subscription);
}
