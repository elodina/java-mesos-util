/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.elodina.mesos.util;

public class Period {
    private long value;
    private String unit;
    private long ms;

    public Period(String s) {
        if (s.isEmpty()) throw new IllegalArgumentException(s);

        int unitIdx = s.length() - 1;
        if (s.endsWith("ms")) unitIdx -= 1;
        if (s.equals("0")) unitIdx = 1;

        try { value = Long.parseLong(s.substring(0, unitIdx)); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException(s); }

        unit = s.substring(unitIdx);
        if (s.equals("0")) unit = "ms";

        ms = value;
        switch (unit) {
            case "ms": ms *= 1; break;
            case "s": ms *= 1000; break;
            case "m": ms *= 60 * 1000; break;
            case "h": ms *= 60 * 60 * 1000; break;
            case "d": ms *= 24 * 60 * 60 * 1000; break;
            default: throw new IllegalArgumentException(s);
        }
    }

    public long value() { return value; }
    public String unit() { return unit; }
    public long ms() { return ms; }

    public boolean equals(Object obj) { return obj instanceof Period && ms == ((Period) obj).ms; }
    public int hashCode() { return new Long(ms).hashCode(); }
    public String toString() { return value + unit; }
}
