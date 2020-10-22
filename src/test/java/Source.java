import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class Source{
    String str = "ss";
    List<SourceItem> list;
    SourceInner inner;
}