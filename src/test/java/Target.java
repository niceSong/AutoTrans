import java.util.List;

public class Target{
    String str;
    List<TargetItem> list;
    TargetInner inner;

    public Target() {
    }

    public Target(String test) {
        this.str = test;
    }

    public Target(List<TargetItem> list) {
        this.list = list;
    }

    public Target(TargetInner inner) {
        this.inner = inner;
    }
}
