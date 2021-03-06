/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.metrics2.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;

/**
 * JMX caches the beans that have been exported; even after the values are removed from hadoop's
 * metrics system the keys and old values will still remain.  This class stops and restarts the
 * Hadoop metrics system, forcing JMX to clear the cache of exported metrics.
 *
 * This class need to be in the o.a.h.metrics2.impl namespace as many of the variables/calls used
 * are package private.
 */
public class JmxCacheBuster {
  private static final Log LOG = LogFactory.getLog(JmxCacheBuster.class);

  /**
   * For JMX to forget about all previously exported metrics.
   */
  public static void clearJmxCache() {
    LOG.trace("Clearing JMX mbean cache.");

    // This is pretty extreme but it's the best way that
    // I could find to get metrics to be removed.

    try {
      DefaultMetricsSystem.INSTANCE.stop();
      DefaultMetricsSystem.INSTANCE.start();
    }  catch (Exception exception )  {
      LOG.debug("error clearing the jmx it appears the metrics system hasn't been started", exception);
    }
  }
}
