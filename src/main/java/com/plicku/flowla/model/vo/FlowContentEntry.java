package com.plicku.flowla.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import static com.plicku.flowla.util.Constants.*;


@Data
@AllArgsConstructor
public class FlowContentEntry {

    String keyword;
    String stepName;
    String data;
    String declaredVariable;
    int depth;
    int lineNumber;

    public boolean isEndIfOrElseIf()
    {
        if(IF.equals(keyword)|| END_IF.equals(keyword))
            return true;
        else
            return false;
    }
    public boolean isIf()
    {
        if(IF.equals(keyword))
            return true;
        else
            return false;
    }
    public boolean isEndIf()
    {
        if(END_IF.equals(keyword))
            return true;
        else
            return false;
    }
    public boolean isIfOrElseIf()
    {
        if(IF.equals(keyword)|| ELSE_IF.equals(keyword))
            return true;
        else
            return false;
    }

    public boolean isIfOrElseIfOrElse()
    {
        if(IF.equals(keyword)|| ELSE_IF.equals(keyword)|| OTHERWISE.equals(keyword))
            return true;
        else
            return false;
    }

    public boolean isNonValidKeyWord()
    {
        return !ALL_KEYWORDS.contains(this.keyword);
    }

    public boolean isElseIf() {
        if(ELSE_IF.equals(keyword))
            return true;
        else
            return false;
    }

    public FlowContentEntry(String keyword, String stepName, String data) {
        this.keyword = keyword;
        this.stepName = stepName;
        this.data = data;
    }

    public FlowContentEntry(String keyword, String stepName, String data,String declaredVariable, int depth) {
        this.keyword = keyword;
        this.data = data==null?null:data.trim();
        this.depth = depth;
        this.declaredVariable=declaredVariable;
        if(stepName!=null && !"".equals(stepName.trim())) {
            int index = stepName.lastIndexOf(LINE_NUM_SEPERATOR);
            this.lineNumber = Integer.parseInt(stepName.substring(index).replaceAll(LINE_NUM_SEPERATOR, "").trim());
            this.stepName = stepName.substring(0, index).trim();
        }
    }


    public FlowContentEntry(String keyword, String stepName, String data,int depth, int lineNumber) { //used for test only
        this.keyword = keyword;
        this.data = data;
        this.depth = depth;
        this.stepName = stepName;
        this.lineNumber = lineNumber;
        this.stepName = stepName;
    }



    public boolean isOtherWise() {
        if(OTHERWISE.equals(keyword))
            return true;
        else
            return false;
    }

    public boolean isForEach(){
        if(FOR_EACH.equals(keyword))
            return true;
        else
            return false;
    }
    public boolean isEndFor(){
        if(END_FOR.equals(keyword))
            return true;
        else
            return false;
    }

    public boolean isRepeatFor() {
        if(REPEAT_FOR.equals(keyword))
            return true;
        else
            return false;
    }
    public boolean isRepeatWhile() {
        if(REPEAT_WHILE.equals(keyword))
            return true;
        else
            return false;
    }

    public boolean isEndRepeat() {
        if(END_REPEAT.equals(keyword))
            return true;
        else
            return false;
    }
}
