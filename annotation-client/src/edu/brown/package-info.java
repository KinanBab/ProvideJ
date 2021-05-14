@MultiJsonData ({
    @JsonData(className = "Json1", data = "annotation-client/samples/file1.json"),
    @JsonData(className = "Json2", data = "annotation-client/samples/file2.json"),
    @JsonData(className = "Json3", data = "annotation-client/samples/file3.json"),
    @JsonData(className = "Json4", data = "annotation-client/samples/file4.json"),
    @JsonData(className = "Json5", data = "annotation-client/samples/file5.json"),
    @JsonData(className = "Json6", data = "annotation-client/samples/file6.json"),
    @JsonData(className = "Json7", data = "annotation-client/samples/file7.json"),
    @JsonData(className = "Json8", data = "annotation-client/samples/file8.json"),
    @JsonData(className = "Json9", data = "annotation-client/samples/file9.json"),
    @JsonData(className = "Json10", data = "annotation-client/samples/file10.json"),
    @JsonData(className = "Json11", data = "annotation-client/samples/file11.json"),
    @JsonData(className = "Json12", data = "annotation-client/samples/file12.json"),
    @JsonData(className = "Json13", data = "annotation-client/samples/file13.json"),
})
@RowTypes({
    @RowType(className = "RName", type = "{ name: string, id: int }"),
    @RowType(className = "RProgram", type = "{ program: { department: string, degree: string } }"),
})
package edu.brown;

import edu.brown.providej.annotations.JsonData;
import edu.brown.providej.annotations.MultiJsonData;
import edu.brown.providej.annotations.RowType;
import edu.brown.providej.annotations.RowTypes;