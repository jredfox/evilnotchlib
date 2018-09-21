package com.evilnotch.lib.util.line;
/**
 * for anything that is not your basic line nor in the head section your meta data goes here
 * @author jredfox
 *
 */
public interface ILineMeta extends ILine{
	
	/**
	 * Don't return null return new String[]{""} instead if you have to
	 * @return An array of data that isn't the identifier and isn't after the equal sign
	 */
	public String[] getMetaData();
	public boolean equalsMeta(ILine other);

}
