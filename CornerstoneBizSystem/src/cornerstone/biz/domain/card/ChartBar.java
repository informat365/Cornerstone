package cornerstone.biz.domain.card;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author cs
 *
 */
public class ChartBar{
    //
    public static class ChartData{
    		public String name;
    		public long value;
    }
    //
    public List<ChartData> dataList;
    //
    public ChartBar() {
    		dataList=new ArrayList<>();
    }
}
