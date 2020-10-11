package source;

import java.util.List;

public class Source{
    String str = "ss";
    List<SourceItem> list;
    SourceInner inner;

    public Source() {
    }

    public Source(String str) {
        this.str = str;
    }

    public Source(List<SourceItem> list) {
        this.list = list;
    }

    public Source(SourceInner inner) {
        this.inner = inner;
    }

    public Source(List<SourceItem> list, SourceInner inner) {
        this.list = list;
        this.inner = inner;
    }
}