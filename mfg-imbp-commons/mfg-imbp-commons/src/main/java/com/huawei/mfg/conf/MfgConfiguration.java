package com.huawei.mfg.conf;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.huawei.mfg.util.MfgConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * Base MFG configuration which supports:
 *   - a configuration file(java properties file)
 *   - key value pairs
 *
 */
public class MfgConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MfgConfiguration.class);
    private static final String DEFAULT_CONFIG_FILE = "mfg-config.properties";
    private String configFile;
    private Properties props;

    protected MfgConfiguration() {
        this.props = new Properties();
    }

    protected MfgConfiguration(String configFile) {
        this.configFile = configFile;
    }

    protected void initialize() throws MfgConfigurationException {
        Preconditions.checkNotNull(this.configFile, "No configuration file is set");

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.configFile);
        Preconditions.checkNotNull(is, String.format("No configuration file (%s) available", this.configFile));
        logger.debug("Configuration InputStream: {}", is);

        this.props = new Properties();
        try {
            this.props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MfgConfigurationException(String.format("Error loading configuration (%s)"), e);
        }
    }

    protected void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    protected Properties getProperties() {
        return props;
    }

    public boolean containsKey(String prop) {
        return this.getProperties().containsKey(prop);
    }

    public String getString(String prop) {
        return this.getString(prop, null);
    }

    public String getString(String prop, String defaultVal) {
        return this.props.getProperty(prop, defaultVal);
    }

    public Integer getInteger(String prop) {
        return this.getInteger(prop, null);
    }

    public Integer getInteger(String prop, Integer ival) {
        String val = this.props.getProperty(prop);
        if (!Strings.isNullOrEmpty(val)) {
            try {
                return Integer.parseInt(val);
            }
            catch (NumberFormatException e) {
            }
        }
        return ival;
    }

    public Long getLong(String prop) {
        return this.getLong(prop, null);
    }

    public Long getLong(String prop, Long lval) {
        String val = this.props.getProperty(prop);
        if (!Strings.isNullOrEmpty(val)) {
            try {
                return Long.parseLong(val);
            }
            catch (NumberFormatException e) {
            }
        }
        return lval;
    }

    public Boolean getBoolean(String prop) {
        String val = this.props.getProperty(prop);
        return Boolean.parseBoolean(val);
    }

    public Boolean getBoolean(String prop, Boolean bval) {
        String val = this.props.getProperty(prop);
        if (!Strings.isNullOrEmpty(val)) {
            return Boolean.parseBoolean(val);
        }
        return bval;
    }

    public void setStringProperty(String prop, String val) {
        Preconditions.checkNotNull(prop, "Property name can't be empty");
        Preconditions.checkNotNull(val, "Property value can't be empty");
        this.setProperty(prop, val);
    }

    public void setIntegerProperty(String prop, Integer ival) {
        Preconditions.checkNotNull(prop, "Property name can't be empty");
        if (ival != null) {
            this.setProperty(prop, String.valueOf(ival));
        }
        else
            this.setProperty(prop, null);
    }

    public void setLongProperty(String prop, Long lval) {
        Preconditions.checkNotNull(prop, "Property name can't be empty");
        if (lval != null) {
            this.setProperty(prop, String.valueOf(lval));
        }
        else
            this.setProperty(prop, null);
    }

    public void setFloatProperty(String prop, Float fval) {
        Preconditions.checkNotNull(prop, "Property name can't be empty");
        if (fval != null) {
            this.setProperty(prop, String.valueOf(fval));
        }
        else
            this.setProperty(prop, null);
    }

    public void setDoubleProperty(String prop, Double dval) {
        Preconditions.checkNotNull(prop, "Property name can't be empty");
        if (dval != null) {
            this.setProperty(prop, String.valueOf(dval));
        }
        else
            this.setProperty(prop, null);
    }

    private void setProperty(String prop, String val) {
        Preconditions.checkNotNull(prop, "Property name can't be empty");
        Preconditions.checkNotNull(val, "Property value can't be empty");
        this.getProperties().setProperty(prop, val);
    }

    @Override
    public String toString() {
        return "MfgConfiguration{" +
                "configFile='" + configFile + '\'' +
                ", props=" + props +
                '}';
    }
}
