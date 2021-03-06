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
import java.util.List;

public class Range {
    private int start = -1;
    private int end = -1;

    public Range(int start) { this(start, start); }
    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Range(String s) {
        int idx = s.indexOf("..");

        if (idx == -1) {
            start = Integer.parseInt(s);
            end = start;
            return;
        }

        start = Integer.parseInt(s.substring(0, idx));
        end = Integer.parseInt(s.substring(idx + 2));
        if (start > end) throw new IllegalArgumentException("start > end");
    }

    public int start() { return start; }
    public int end() { return end; }

    @SuppressWarnings("SuspiciousNameCombination")
    public Range overlap(Range r) {
        Range x = this;
        Range y = r;

        if (x.start > y.start) {
            Range t = x;
            x = y;
            y = t;
        }
        assert x.start <= y.start;

        if (y.start > x.end) return null;
        assert y.start <= x.end;

        int start = y.start;
        int end = Math.min(x.end, y.end);
        return new Range(start, end);
    }

    public boolean contains(int p) { return start <= p && p <= end; }

    public List<Range> split(int p) {
        if (!contains(p)) throw new IllegalArgumentException("point not in range");

        List<Range> result = new ArrayList<>();
        if (start < p) result.add(new Range(start, p - 1));
        if (p < end) result.add(new Range(p + 1, end));

        return result;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) return false;
        Range range = (Range) obj;
        return start == range.start && end == range.end;
    }

    public int hashCode() { return 31 * start + end; }

    public String toString() { return start == end ? "" + start : start + ".." + end; }
}
