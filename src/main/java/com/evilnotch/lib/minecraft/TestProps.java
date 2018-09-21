package com.evilnotch.lib.minecraft;

import java.security.PublicKey;

import com.mojang.authlib.properties.Property;

public class TestProps extends Property{

	public TestProps(String name, String value, String signature) {
		super(name, value, signature);
	}
	
	 public boolean isSignatureValid(final PublicKey publicKey) {
		 return true;
	 }

}
