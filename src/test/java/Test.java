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
        Source source = new Source(sourceItemList, inner);

        String fun = "fun";
        Object target = BeanTrans.converter(source, Target.class)
                .mapping("str", "new ss")
                .mapping("str", ()->{return "new ss:" + fun;})
                .trans();
        System.out.println(target);
    }

}
