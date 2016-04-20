package net.elodina.mesos.api;

import net.elodina.mesos.util.Strings;
import org.junit.Test;

import java.util.Arrays;

import static net.elodina.mesos.api.Command.URI;
import static org.junit.Assert.*;

public class CommandTest {
    @Test
    public void init_parse() {
        // simple
        Command command = new Command("cmd");
        assertEquals("cmd", command.value());
        assertEquals(0, command.uris().size());
        assertEquals(0, command.env().size());

        // with uris
        command = new Command("cmd, uris:[http://host1, http://host2]");
        assertEquals(Arrays.asList(new URI("http://host1"), new URI("http://host2")), command.uris());

        // with env
        command = new Command("cmd, env:[a=1,b=2]");
        assertEquals(Strings.parseMap("a=1,b=2"), command.env());
    }

    @Test
    public void _toString() {
        assertEquals("cmd", "" + new Command("cmd"));
        assertEquals("cmd, uris:[uri1,uri2]", "" + new Command("cmd, uris:[uri1, uri2]"));
        assertEquals("cmd, env:[a=1,b=2]", "" + new Command("cmd, env:[a=1, b=2]"));
    }

    @Test
    public void proto0_to_from() {
        Command command = new Command("cmd, env:[a=1,b=2]");
        org.apache.mesos.Protos.CommandInfo message = command.proto0();

        Command read = new Command().proto0(message);
        assertEquals("" + command, "" + read);
    }

    @Test
    public void proto1_to_from() {
        Command command = new Command("cmd, env:[a=1,b=2]");
        org.apache.mesos.v1.Protos.CommandInfo message = command.proto1();

        Command read = new Command().proto1(message);
        assertEquals("" + command, "" + read);
    }

    @Test
    public void URI_parse() {
        // simple
        URI uri = new URI("http://localhost");
        assertEquals("http://localhost", uri.value());
        assertTrue(uri.extract());

        // with attrs
        uri = new URI("http://localhost, extract:false");
        assertEquals("http://localhost", uri.value());
        assertFalse(uri.extract());

        uri = new URI("http://localhost, extract:true");
        assertEquals("http://localhost", uri.value());
        assertTrue(uri.extract());
    }

    @Test
    public void URI_toString() {
        assertEquals("http://localhost", "" + new URI("http://localhost"));
        assertEquals("http://localhost", "" + new URI("http://localhost, extract:true"));
        assertEquals("http://localhost, extract:false", "" + new URI("http://localhost, extract:false"));
    }

    @Test
    public void URI_proto0_to_from() {
        URI uri = new URI("http://localhost, extract:false");
        org.apache.mesos.Protos.CommandInfo.URI message = uri.proto0();

        URI read = new URI().proto0(message);
        assertEquals(uri, read);
    }

    @Test
    public void URI_proto1_to_from() {
        URI uri = new URI("http://localhost, extract:false");
        org.apache.mesos.v1.Protos.CommandInfo.URI message = uri.proto1();

        URI read = new URI().proto1(message);
        assertEquals(uri, read);
    }
}
