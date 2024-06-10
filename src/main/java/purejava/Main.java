package purejava;

import java.util.HashMap;

public class Main {
	
	public static ThreadLocal<HashMap> map = new ThreadLocal<HashMap>().withInitial(()->new HashMap());
	public static HashMap unsafe = new HashMap();
	public static void main(String[] args)
	{
		long ms = System.currentTimeMillis();
		for(int i=0;i<10000;i++)
		{
//			map.get().hashCode();
//			unsafe.hashCode();
//			new HashMap(50).hashCode();
		}
		System.out.println(System.currentTimeMillis() - ms);
	}

}
