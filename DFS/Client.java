/*!
	@files	Client.java, DFS.Java
	@author	Bryson Sherman, Hung Mach, Jimmy Lanh
	@date	4/3/2018
	@version 1.0
	Creators: Bryson Sherman
			  Hung Mach
			  Jimmy Lanh
		  
	Due Date: 4/3/2018
	
*/

import java.rmi.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.nio.file.*;


public class Client
{
    DFS dfs;
    public Client(int p) throws Exception {
        dfs = new DFS(p);
//        dfs.touch("test");
//        System.out.println();
        dfs.delete("File2");
            // User interface:
            // join, ls, touch, delete, read, tail, head, append, move
    }
    
    static public void main(String args[]) throws Exception
    {
        /*
        if (args.length < 1 ) {
            throw new IllegalArgumentException("Parameter: <port>");
        }
        
        Client client=new Client( Integer.parseInt(args[0]));
        */
        
        Client client = new Client(23245);
     } 
}
