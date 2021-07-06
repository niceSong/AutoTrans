import com.tyytogether.trans.BeanTrans;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Test {

    @org.junit.Test
    public void test() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        SourceItem sourceItem = new SourceItem("ss");
        ArrayList<SourceItem> sourceItemList = new ArrayList<SourceItem>(){{
            add(sourceItem);
        }};
        SourceInner inner = new SourceInner(1);
        Source source = new Source("ss", sourceItemList, inner);

//         自定义转换
//        Object diyTarget = BeanTrans.converter(source, Target.class)
//                .mapping("str", "new ss")
//                .mapping("str", ()->{return  "new ss:" + fun;})
//                .startTrans();
        // 自动转换
        BeanTrans<Target> bt = new BeanTrans<>(source, Target.class);
        Target target = bt.startTrans();
        System.out.println(target);
    }
}
