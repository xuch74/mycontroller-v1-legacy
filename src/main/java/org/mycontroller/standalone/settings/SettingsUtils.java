/**
 * Copyright (C) 2015-2016 Jeeva Kandasamy (jkandasa@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mycontroller.standalone.settings;

import org.mycontroller.standalone.db.DaoUtils;
import org.mycontroller.standalone.db.tables.Settings;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.1
 */
public class SettingsUtils {

    private SettingsUtils() {

    }

    public static String getValue(String key) {
        return getValue(key, key);
    }

    public static String getValue(String key, String subKey) {
        Settings settings = DaoUtils.getSettingsDao().get(key, subKey);
        return settings != null ? settings.getValue() : null;
    }

    public static Settings getSettings(String key, String subKey) {
        return DaoUtils.getSettingsDao().get(key, subKey);
    }

    public static void updateValue(String key, Object value) {
        updateValue(key, key, value);
    }

    public static void updateValue(String key, String subKey, Object value) {
        DaoUtils.getSettingsDao().update(key, subKey, value != null ? String.valueOf(value) : null);
    }

    public static void updateValue(String key, String subKey, Object value, Object altValue) {
        DaoUtils.getSettingsDao().update(key, subKey, value != null ? String.valueOf(value) : null,
                altValue != null ? String.valueOf(altValue) : null);
    }
}
