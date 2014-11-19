package org.d3.demo.leveldb;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;

import static org.fusesource.leveldbjni.JniDBFactory.*;

import org.iq80.leveldb.Options;

public class LevelDBTest {

	public static void main(String[] args) throws IOException {
		
		Options options = new Options();
		options.createIfMissing(true);
		DB db = factory.open(new File("example"), options);
		try {
			db.put(bytes("Tampa"), bytes("rocks"));
			String value = asString(db.get(bytes("Tampa")));
			System.out.println(value);
			db.delete(bytes("Tampa"));
		} finally {
		  // Make sure you close the db to shutdown the 
		  // database and avoid resource leaks.
		  db.close();
		}
		
	}

}
