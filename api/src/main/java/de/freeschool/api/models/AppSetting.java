package de.freeschool.api.models;

import jakarta.persistence.*;
import lombok.Data;

/**
 * This table stores all kind of settings (key/value pairs) that should be persisted.
 * Do not query or update directly but use the SettingManager instead!
 */
@Data
@Entity
@Table(name = "app_setting")
public class AppSetting {
    @Id
    private String name;
    private String theValue;  // cannot use 'value' because it's a reserved identifier in some SQL backends
}
