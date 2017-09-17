package com.plicku.flowla.model.vo;

import com.plicku.flowla.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;

import static com.plicku.flowla.util.Constants.ELSE_IF;
import static com.plicku.flowla.util.Constants.END_IF;
import static com.plicku.flowla.util.Constants.IF;

@Data
@AllArgsConstructor
public class FlowContentEntry {

    String keyword;
    String stepName;
    String data;

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
    public boolean ifOrElseIf()
    {
        if(IF.equals(keyword)|| ELSE_IF.equals(keyword))
            return true;
        else
            return false;
    }

}
