package cornerstone.biz.domain;

/**
 * 
 * @author cs
 *
 */
public class MachineSimpleInfo {

	/***/
	public String name;

	/**
	 * 
	 * @param machine
	 * @return
	 */
	public static MachineSimpleInfo create(Machine machine) {
		MachineSimpleInfo info=new MachineSimpleInfo();
		info.name=machine.name;
		return info;
	}
}
