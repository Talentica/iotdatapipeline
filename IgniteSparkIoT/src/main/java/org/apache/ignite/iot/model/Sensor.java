/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.iot.model;

import java.io.Serializable;

import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.json.JSONObject;

/**
 *
 */
public class Sensor implements Serializable {
    /** */
    private String name;

    /** */
    private double latitude;

    /** */
    private double longitude;
    
    //private JSONObject sensorJson;

    /**
     * @param name
     * @param latitude
     * @param longitude
     */
    public Sensor(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.sensorJson = sensorJson;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return
     */
    public double getLongitude() {
        return longitude;
    }
    
    //public JSONObject getSensonJson() {
    	//return sensorJson;
    //}
}
