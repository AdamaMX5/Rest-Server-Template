package de.freeschool.api.manager;

import de.freeschool.api.models.AppSetting;
import de.freeschool.api.repository.AppSettingRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Provides a clean interface to the settings stored in the AppSetting table.
 */
@Service
public class SettingManager {
    @Data
    @AllArgsConstructor
    public static class MaintenanceMode {
        private boolean enabled;
        private String message;
    }

    @Autowired
    AppSettingRepository appSettingRepository;

    private boolean getBooleanValue(String name, boolean defaultValue) {
        AppSetting setting = appSettingRepository.findById(name).orElse(null);
        if (setting == null) {
            return defaultValue;
        }
        return setting.getTheValue().equals("true");
    }

    private void setBooleanValue(String name, boolean value) {
        AppSetting setting = new AppSetting();
        setting.setName(name);
        setting.setTheValue(value ? "true" : "false");
        appSettingRepository.save(setting);
    }

    private String getStringValue(String name, String defaultValue) {
        AppSetting setting = appSettingRepository.findById(name).orElse(null);
        if (setting == null) {
            return defaultValue;
        }
        return setting.getTheValue();
    }

    private void setStringValue(String name, String value) {
        AppSetting setting = new AppSetting();
        setting.setName(name);
        setting.setTheValue(value);
        appSettingRepository.save(setting);
    }

    public MaintenanceMode getMaintenanceMode() {
        return new MaintenanceMode(getBooleanValue("maintenance.enabled", false),
                getStringValue("maintenance.message", "")
        );
    }

    public void setMaintenanceMode(MaintenanceMode maintenanceMode) {
        setBooleanValue("maintenance.enabled", maintenanceMode.enabled);
        setStringValue("maintenance.message", maintenanceMode.message);
    }
}
