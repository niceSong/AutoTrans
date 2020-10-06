import obj.source.Source;
import obj.source.SourceInner;
import obj.source.SourceItem;
import obj.target.Target;
import com.tyytogether.trans.BeanTrans;

import java.util.ArrayList;

public class Test {

    @org.junit.Test
    public void test(){
        BeanTrans trans = new BeanTrans();

        SourceItem sourceItem = new SourceItem("ss");
        ArrayList<SourceItem> sourceItemList = new ArrayList<SourceItem>(){{
            add(sourceItem);
        }};
        SourceInner inner = new SourceInner(1);
        Source source = new Source(sourceItemList, inner);

        try{
            Object target = trans.mapping("test", "new ss").autoTrans(source, Target.class);
            System.out.println(target);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
