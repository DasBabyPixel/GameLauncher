package gamelauncher.test;

import java.math.BigInteger;

public class PerfCheck {

	public static void main(String[] args) {

		try {
			Thread.sleep(10000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		long start = System.currentTimeMillis();
		for (short i = 0; i != 20_000; i++) {
			Math.sin(Math.cos(Math.sin(1561617)));
		}
		
		long end = System.currentTimeMillis();
		System.out.println();
		System.out.println(end - start);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		start = System.currentTimeMillis();
		BigInteger i = new BigInteger("0");
		BigInteger max = new BigInteger("20000");
		for (; i.compareTo(max) == -1; i = i.add(BigInteger.ONE)) {
			Math.sin(Math.cos(Math.sin(1561617)));
		}
		System.out.println();
		System.out.println(System.currentTimeMillis() - start);
	}
}
