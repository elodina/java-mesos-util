package net.elodina.mesos.util;

import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static net.elodina.mesos.util.Constraint.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class ConstraintTest {
    @Test
    public void init() {
        class O {
            @SuppressWarnings("unchecked")
            <T extends Condition> T c(String s) { return (T) new Constraint(s).condition(); }
        }
        O o = new O();

        // like
        Like like = o.c("like:1");
        assertEquals("1", like.regex());

        // unlike
        like = o.c("unlike:1");
        assertEquals("1", like.regex());
        assertTrue(like.negated());

        // unique
        assertEquals(Unique.class, o.c("unique").getClass());

        // cluster
        Cluster cluster = o.c("cluster");
        assertNull(cluster.value());

        cluster = o.c("cluster:123");
        assertEquals("123", cluster.value());

        // groupBy
        GroupBy groupBy = o.c("groupBy");
        assertEquals(1, groupBy.groups());

        groupBy = o.c("groupBy:2");
        assertEquals(2, groupBy.groups());

        // unsupported
        try { o.c("unsupported"); fail(); }
        catch (IllegalArgumentException e) { assertTrue("" + e, e.getMessage().contains("unsupported")); }
    }

    @Test
    public void matches() {
        // smoke tests
        assertTrue(new Constraint("like:abc").matches("abc"));
        assertFalse(new Constraint("like:abc").matches("abc1"));

        assertTrue(new Constraint("like:a.*").matches("abc"));
        assertFalse(new Constraint("like:a.*").matches("bc"));

        assertTrue(new Constraint("unique").matches("a"));
        assertFalse(new Constraint("unique").matches("a", Arrays.asList("a")));

        assertTrue(new Constraint("cluster").matches("a"));
        assertFalse(new Constraint("cluster").matches("b", Arrays.asList("a")));

        assertTrue(new Constraint("groupBy").matches("a", Arrays.asList("a")));
        assertFalse(new Constraint("groupBy").matches("a", Arrays.asList("b")));
    }

    @Test
    public void _toString() {
        assertEquals("like:abc", "" + new Constraint("like:abc"));
        assertEquals("groupBy", "" + new Constraint("groupBy"));
        assertEquals("groupBy:2", "" + new Constraint("groupBy:2"));
    }

    @Test
    public void Like_matches() {
        Like like = new Like("1.*2");
        assertTrue(like.matches("12"));
        assertTrue(like.matches("1a2"));
        assertTrue(like.matches("1ab2"));

        assertFalse(like.matches("a1a2"));
        assertFalse(like.matches("1a2a"));

        like = new Like("1", true);
        assertFalse(like.matches("1"));
        assertTrue(like.matches("2"));
    }

    @Test
    public void Like_toString() {
        assertEquals("like:1", "" + new Constraint.Like("1"));
        assertEquals("unlike:1", "" + new Constraint.Like("1", true));
    }

    @Test
    public void Unique_matches() {
        Unique unique = new Unique();
        assertTrue(unique.matches("1"));
        assertTrue(unique.matches("2", Arrays.asList("1")));
        assertTrue(unique.matches("3", Arrays.asList("1", "2")));

        assertFalse(unique.matches("1", Arrays.asList("1", "2")));
        assertFalse(unique.matches("2", Arrays.asList("1", "2")));
    }

    @Test
    public void Cluster_matches() {
        Cluster cluster = new Cluster();
        assertTrue(cluster.matches("1"));
        assertTrue(cluster.matches("2"));

        assertTrue(cluster.matches("1", Arrays.asList("1")));
        assertTrue(cluster.matches("1", Arrays.asList("1", "1")));
        assertFalse(cluster.matches("2", Arrays.asList("1")));

        cluster = new Constraint.Cluster("1");
        assertTrue(cluster.matches("1"));
        assertFalse(cluster.matches("2"));

        assertTrue(cluster.matches("1", Arrays.asList("1")));
        assertTrue(cluster.matches("1", Arrays.asList("1", "1")));
        assertFalse(cluster.matches("2", Arrays.asList("1")));
    }

    @Test
    public void GroupBy_matches() {
        GroupBy groupBy = new GroupBy();
        assertTrue(groupBy.matches("1"));
        assertTrue(groupBy.matches("1", Arrays.asList("1")));
        assertTrue(groupBy.matches("1", Arrays.asList("1", "1")));
        assertFalse(groupBy.matches("1", Arrays.asList("2")));

        groupBy = new GroupBy(2);
        assertTrue(groupBy.matches("1"));
        assertFalse(groupBy.matches("1", Arrays.asList("1")));
        assertFalse(groupBy.matches("1", Arrays.asList("1", "1")));
        assertTrue(groupBy.matches("2", Arrays.asList("1")));

        assertTrue(groupBy.matches("1", Arrays.asList("1", "2")));
        assertTrue(groupBy.matches("2", Arrays.asList("1", "2")));

        assertFalse(groupBy.matches("1", Arrays.asList("1", "1", "2")));
        assertTrue(groupBy.matches("2", Arrays.asList("1", "1", "2")));
    }
}
