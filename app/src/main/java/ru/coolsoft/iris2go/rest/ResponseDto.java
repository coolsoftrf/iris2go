package ru.coolsoft.iris2go.rest;

import org.simpleframework.xml.*;

import java.util.List;

@Root(name = "xjx")
public class ResponseDto {
    @Root(name = "cmd")
    public static class CmdDto {
        @Attribute
        private String n, t, p;

        @Text(data = true)
        public String text;
    }

    @ElementList(inline = true)
    public List<CmdDto> cmd;
}
