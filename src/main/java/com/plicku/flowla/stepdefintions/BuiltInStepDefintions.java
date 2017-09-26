package com.plicku.flowla.stepdefintions;

import com.plicku.flowla.anotations.operators.ForEach;
import com.plicku.flowla.anotations.operators.Repeat;
import com.plicku.flowla.anotations.parameters.DataTableParameter;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.model.DataTable;

@StepDefinitions
public class BuiltInStepDefintions {


    @ForEach("row in the following data table")
    public DataTable getEachRowInDataTable(@DataTableParameter DataTable dataTable)
    {
       return dataTable;
    }

    @Repeat("the following steps (\\d+) times")
    public int repeatSteps(int times)
    {
        return times;
    }


}
