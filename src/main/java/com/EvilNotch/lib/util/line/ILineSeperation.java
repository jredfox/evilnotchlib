package com.evilnotch.lib.util.line;
/**
 * for line separation between modid:block so you don't have to manually parse it when using your line object later
 * @author jredfox
 *
 */
public interface ILineSeperation extends ILine{
	
	public char getSeprator();
	public char getQuote();

}
