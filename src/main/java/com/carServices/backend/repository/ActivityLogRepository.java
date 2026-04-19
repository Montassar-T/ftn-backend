package com.carServices.backend.repository;

import com.carServices.backend.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Long>, JpaSpecificationExecutor<ActivityLog> {}
