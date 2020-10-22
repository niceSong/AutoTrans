import com.tyytogether.trans.BeanTrans;

import java.util.ArrayList;

public class Test {

    @org.junit.Test
    public void test(){
        SourceItem sourceItem = new SourceItem("ss");
        ArrayList<SourceItem> sourceItemList = new ArrayList<SourceItem>(){{
            add(sourceItem);
        }};
        SourceInner inner = new SourceInner(1);
        Source source = new Source("ss", sourceItemList, inner);

        String fun = "fun";
        // 自定义转换
        Object diyTarget = BeanTrans.converter(source, Target.class)
                .mapping("str", "new ss")
                .mapping("str", ()->{return  "new ss:" + fun;})
                .startTrans();
        // 自动转换
        Object autoTarget = BeanTrans.converter(source, Target.class).startTrans();
    }
}
