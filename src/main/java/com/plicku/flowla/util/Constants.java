package com.plicku.flowla.util;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String AND="And ";
    public static final String WHEN="When ";
    public static final String GIVEN="Given ";
    public static final String THEN="Then ";
    public static final String BUT="But ";
    public static final String FOR_EACH="For Each ";
    public static final String END_FOR = "EndFor";
    public static final String IF="If ";
    public static final String ELSE_IF="Else If ";
    public static final String END_IF="EndIf";
    public static final String OTHERWISE ="Otherwise";
    public static final String REPEAT_FOR ="Repeat Step(s) For ";
    public static final String REPEAT_WHILE ="Repeat Step(s) while ";
    public static final String END_REPEAT="EndRepeat";
    public static final List<String> PROCESS_KEYWORDS = Arrays.asList(AND,WHEN,GIVEN,THEN,BUT);
    public static final List<String> CONDITIONAL_KEYWORDS = Arrays.asList(IF,END_IF,ELSE_IF);
    public static final List<String> LOOPING_KEYWORDS = Arrays.asList(FOR_EACH);
    public static final List<String> ALL_KEYWORDS= Arrays.asList(AND,WHEN,GIVEN,THEN,BUT,END_IF,IF,OTHERWISE,ELSE_IF,FOR_EACH,END_FOR);
    public static final String COMMENT="#";
    public static final String KEYWD_BEGIN_PTTN ="((^\\s{0,100})|^|(\n\\s{0,100})|\n)";
    public static final String SUCCESS="success";
    public static final String SKIPPED="skipped";
    public static final String FAILED="failed";
    public static Long MAX_REPEAT_CNT_ALLOWED =10000L;
    public static final String LINE_NUM_SEPERATOR="LINE_NUM";

}
