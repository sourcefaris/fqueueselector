package com.fqueue.common;

import java.util.Vector;

public class SpellNumbers {


	private static String baca(int x)
	{
	String[] bil = {" ","Satu", "Dua", "Tiga", "Empat", "Lima", "Enam", "Tujuh", "Delapan", "Sembilan", "Sepuluh", "Sebelas"};
	if (x < 12)
		return " " + bil[x];
	else if (x < 20)
		return baca(x - 10) + "belas";
	else if (x < 100)
		return baca(x / 10) + " puluh" + baca(x % 10);
	else if (x < 200)
		return " seratus" + baca(x - 100);
	else if (x < 1000)
		return baca(x / 100) + " ratus" + baca(x % 100);
	else if (x < 2000)
		return " seribu" + baca(x - 1000);
	else if (x < 1000000)
		return baca(x / 1000) + " ribu" + baca(x % 1000);
	else if (x < 1000000000)
		return baca(x / 1000000) + " juta" + baca(x % 1000000);
	return null;
	}
	
	public static void main(String[] args) {
//		System.out.println(baca(123456789));
		
		Vector v1 = new Vector();
		v1.add("1");
		v1.add("2");
		v1.add("3");
		v1.add("4");
		v1.add("5");
		Vector v2 = new Vector();
		v2.add("6");
		v2.add("7");
		v2.add("8");
		
		System.out.println("v1 size = " + v1.size());
		System.out.println("v2 size = " + v2.size());
		v1.addAll(v2);
		
		System.out.println("v1 new size = " + v1.size());
		
	}

}
