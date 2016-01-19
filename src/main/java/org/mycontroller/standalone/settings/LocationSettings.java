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

import org.mycontroller.standalone.NumericUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
@Builder
@ToString(includeFieldNames = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("locationSettings")
public class LocationSettings {
    public static final String KEY_LOCATION = "location";
    public static final String SKEY_NAME = "name";
    public static final String SKEY_LATITUDE = "latitude";
    public static final String SKEY_LONGITUDE = "longitude";
    public static final String SKEY_SUNRISE_TIME = "sunrise";
    public static final String SKEY_SUNSET_TIME = "sunset";

    private String name;
    private String latitude;
    private String longitude;
    private Long sunriseTime;
    private Long sunsetTime;

    public static LocationSettings get() {
        return LocationSettings.builder()
                .name(getValue(SKEY_NAME))
                .latitude(getValue(SKEY_LATITUDE))
                .longitude(getValue(SKEY_LONGITUDE))
                .sunriseTime(NumericUtils.getLong(getValue(SKEY_SUNRISE_TIME)))
                .sunsetTime(NumericUtils.getLong(getValue(SKEY_SUNSET_TIME))).build();
    }

    public void save() {
        updateValue(SKEY_NAME, this.name);
        updateValue(SKEY_LATITUDE, this.latitude);
        updateValue(SKEY_LONGITUDE, this.longitude);
    }

    private static String getValue(String subKey) {
        return SettingsUtils.getValue(KEY_LOCATION, subKey);
    }

    private static void updateValue(String subKey, Object value) {
        SettingsUtils.updateValue(KEY_LOCATION, subKey, value);
    }

    @JsonIgnore
    public void updateInternal() {
        updateValue(SKEY_SUNRISE_TIME, this.sunriseTime);
        updateValue(SKEY_SUNSET_TIME, this.sunsetTime);
    }
}
