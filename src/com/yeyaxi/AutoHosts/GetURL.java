package com.yeyaxi.AutoHosts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetURL {
	public static String getContent(String strUrl) {

		try {

			//			URL url = new URL(strUrl);
			//
			//			BufferedReader br = new BufferedReader(new InputStreamReader(url
			//
			//					.openStream()));
			//
			//			String s = "";
			//
			//			StringBuffer sb = new StringBuffer("");
			//
			//			while ((s = br.readLine()) != null) {
			//
			//				sb.append(s + "/r/n");
			String curLine = "";
			String content = "";
			URL server = new URL(strUrl);
			HttpURLConnection connection = (HttpURLConnection) server

					.openConnection();

			connection.connect();

			InputStream is = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			while ((curLine = reader.readLine()) != null) {

				content = content + curLine+ "\r\n";

			}
			try{
				// Create file 
				File file = new File("/sdcard/hosts");
				FileWriter fstream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(content);
				//Close the output stream
				out.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			//System.out.println("content= " + content);

			is.close();

			//			}
			//
			//			br.close();
			//
			//			return sb.toString();

		} catch (Exception e) {

			return "error open url:" + strUrl;

		}
		return strUrl;


		//String url = "https://kelvin-mirex-svn.googlecode.com/svn/trunk/ipv4-hosts/hosts";

		//System.out.println(getContent(url));

	}
}
