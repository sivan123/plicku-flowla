package com.plicku.flowla.model.vo;

import com.plicku.flowla.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.plicku.flowla.util.Constants.*;

@Data
@AllArgsConstructor
public class FlowContentEntry {

    String keyword;
    String stepName;
    String data;
    int depth;

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
}
