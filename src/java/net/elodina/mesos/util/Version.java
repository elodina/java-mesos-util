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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Version implements Comparable<Version> {
    private List<Integer> values;

    public Version(Integer... values) {
        this.values = Arrays.asList(values);
    }

    public Version(String s) {
        values = new ArrayList<>();
        if (!s.isEmpty())
            for (String part : s.split("\\.", -1))
                values.add(Integer.parseInt(part));
    }

    public List<Integer> values() { return Collections.unmodifiableList(values); }

    public int compareTo(Version v) {
        for (int i = 0; i < Math.min(values.size(), v.values.size()); i++) {
            int diff = values.get(i) - v.values.get(i);
            if (diff != 0) return diff;
        }

        return values.size() - v.values.size();
    }

    public int hashCode() { return values.hashCode(); }

    public boolean equals(Object obj) {
        return obj instanceof Version && values.equals(((Version) obj).values);
    }

    public String toString() { return Strings.join(values, "."); }
}
