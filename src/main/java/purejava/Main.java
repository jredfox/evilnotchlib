package purejava;

import com.evilnotch.lib.main.skin.SkinCache;
import com.evilnotch.lib.util.JavaUtil;

public class Main {
	
	public static void main(String[] args)
	{
//		for(int i=0;i<1000;i++)
		System.out.println(SkinCache.getInstance().getMojangProfile("4223a2b1ed374577adb2d18eca6411f4"));
	}

}
