package net.elodina.mesos.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Constraint {
    private String expr;
    private Condition condition;

    public Constraint(String expr) {
        this.expr = expr;

        if (expr.startsWith("like:")) condition = new Like(expr.substring("like:".length()));
        else if (expr.startsWith("unlike:")) condition = new Constraint.Like(expr.substring("unlike:".length()), true);
        else if (expr.equals("unique")) condition = new Constraint.Unique();
        else if (expr.startsWith("cluster")) {
            String tail = expr.substring("cluster".length());
            String value = tail.startsWith(":") ? tail.substring(1) : null;
            condition = new Cluster(value);
        } else if (expr.startsWith("groupBy")) {
            String tail = expr.substring("groupBy".length());

            int groups = 1;
            if (tail.startsWith(":"))
                try { groups = Integer.valueOf(tail.substring(1)); }
                catch (NumberFormatException e) { throw new IllegalArgumentException(expr); }

            condition = new Constraint.GroupBy(groups);
        } else throw new IllegalArgumentException(expr);
    }

    public String expr() { return expr; }
    public Condition condition() { return condition; }

    public boolean matches(String value) { return matches(value, null); }
    public boolean matches(String value, Collection<String> values) { return condition.matches(value, values != null ? values : Collections.<String>emptyList()); }

    public int hashCode() { return expr.hashCode(); }
    public boolean equals(Object obj) {
        if (!(obj instanceof Constraint)) return false;
        Constraint other = (Constraint) obj;
        return expr.equals(other.expr);
    }

    public String toString() { return expr; }

    static abstract class Condition {
        public boolean matches(String value) { return matches(value, Collections.<String>emptyList()); }
        public abstract boolean matches(String value, Collection<String> values);
    }

    static class Like extends Condition {
        private String regex;
        private boolean negated;
        private Pattern pattern;

        Like(String regex) { this(regex, false); }
        Like(String regex, boolean negated) {
            this.regex = regex;
            this.negated = negated;

            try { pattern = Pattern.compile("^" + regex + "$"); }
            catch (PatternSyntaxException e) { throw new IllegalArgumentException("invalid " + name() + ": " + e.getMessage()); }

        }

        public String regex() { return regex; }
        public boolean negated() { return negated; }

        public String name() { return !negated ? "like" : "unlike"; }

        public boolean matches(String value, Collection<String> values) {
            return negated ^ pattern.matcher(value).find();
        }

        public String toString() {
            return name() + ":" + regex;
        }
    }

    static class Unique extends Condition {
        public boolean matches(String value, Collection<String> values) { return !values.contains(value); }
        public String toString() { return "unique"; }
    }

    static class Cluster extends Condition {
        private String value;

        Cluster() { this(null); }
        Cluster(String value) {
            this.value = value;
        }

        public String value() { return value; }

        public boolean matches(String value, Collection<String> values) {
            if (this.value != null) return value.equals(this.value);
            return values.isEmpty() || values.iterator().next().equals(value);
        }

        public String toString() { return "cluster" + (value != null ? ":" + value : ""); }
    }

    static class GroupBy extends Condition {
        private int groups;

        GroupBy() { this(1); }
        GroupBy(int groups) { this.groups = groups; }

        public int groups() { return groups; }

        public boolean matches(String value, Collection<String> values) {
            Map<String, Integer> counts = new HashMap<>();
            for (String v : values) {
                if (!counts.containsKey(v)) counts.put(v, 0);
                counts.put(v, counts.get(v) + 1);
            }

            if (counts.size() < groups) return !counts.containsKey(value);

            int minCount = counts.isEmpty() ? 0 : Integer.MAX_VALUE;
            for (int c : counts.values()) minCount = Math.min(c, minCount);

            int count = counts.containsKey(value) ? counts.get(value) : 0;
            return count == minCount;
        }

        public String toString() { return "groupBy" + (groups > 1 ? ":" + groups : ""); }
    }
}
