package de.freeschool.api.repository;

import de.freeschool.api.models.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppSettingRepository extends JpaRepository<AppSetting, String> {
}
