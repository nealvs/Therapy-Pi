package com.spriton.therapypi.database;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="config_value")
public class ConfigValue {

    @Id
    @Column(name="config_key")
    private String configKey;
    @Column(name="configValue")
    private String configValue;

    @Column(name="created")
    private Date created;
    @Column(name="updated")
    private Date updated;
    @Column(name="deleted")
    private Date deleted;

    public ConfigValue() {}

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}
