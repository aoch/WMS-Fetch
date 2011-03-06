package com.convergys.wmsfetch.logic;

import java.io.File;

public class ToolUtil {

	public void getCSMModule() {

		File[] roots = File.listRoots();

		System.out.println("asdf:");
		for (int i = 0; i < roots.length; i++) {
			System.out.println(roots[i]);

			if (roots[i].toString().equals("D:\\")) {
				System.out.println("\t" + "asdf:");

				File[] files = roots[i].listFiles();
				for (int j = 0; j < files.length; j++) {
					System.out.println("\t  " + files[j].getName());
				}
			}
		}
	}
}