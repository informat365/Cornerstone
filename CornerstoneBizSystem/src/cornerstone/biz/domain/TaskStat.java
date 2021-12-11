package cornerstone.biz.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计
 * @author cs
 *
 */
public class TaskStat {
	//
	public static class TaskNumStat{
		public String name;
		public int totalNum;
		public int finishNum;
		public int delayNum;
	}

	/**
	 * 旭日图树状数据结构
	 */
	public static class TaskNumTreeStat {
		public String name;
		public long value;
		public List<TaskNumTreeStat> children;

		@Override
		public String toString() {
			return "TaskNumTreeStat{" +
					"name='" + name + '\'' +
					", value=" + value +
					", children=" + children +
					'}';
		}

		public TaskNumTreeStat(String name, long value){
			this.name = name;
			this.value = value;
			this.children = new ArrayList<>();
		}

		public TaskNumTreeStat(String name, long value,List<TaskNumTreeStat> children){
			this.name = name;
			this.value = value;
			this.children = children;
		}

		public TaskNumTreeStat increament(){
			this.value+=1;
			return this;
		}
	}

	//
	public int totalNum;

	public int finishNum;
	
	public int delayNum;

	public int maxDepartmentLevel;
	
	public List<TaskNumStat> statusStatList;
	
	public List<TaskNumStat> accountStatList;

	//饼图分类统计
	public List<TaskNumStat> categoryStatList;

	//部门统计
	public List<TaskNumStat> departmentStatList;

	//旭日图分类统计
	public List<TaskNumTreeStat> categoryStatSunList;

	
}
