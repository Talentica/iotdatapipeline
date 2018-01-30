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

package com.talentica.iot.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 */
public class TempKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Sensor ID. Set as an affinity key in 'ignite-config.xml'.
	 */
	// TODO: for making a generic system. this id should be string device identifier which corresponds to a unique device_id in our db.
	private int sensorId;

	/**
	 * Timestamp of the record.
	 */
	private Date ts; // This should come from the sensor (ms UTC). generating this on server can cause analytic-error due to lag.
	
	private UUID uuid; // Why are we generating this?

	public TempKey() {
		this.uuid = UUID.randomUUID();
	}
	
	// Effectively we are using the default equals. every object is unique.
	// We should not de-duplicate data from sensors. data from sensors are expected to be unique by default.
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TempKey key = (TempKey) o;

		if (sensorId != key.sensorId)
			return false;
		return uuid != null ? uuid.equals(key.uuid) : key.uuid == null;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		int result = sensorId;
		result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
		return result;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public int getSensorId() {
		return sensorId;
	}

	public Date getTs() {
		return ts;
	}

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return "TempKey [sensorId=" + sensorId + ", ts=" + ts + ", uuid=" + uuid + "]";
	}
}
