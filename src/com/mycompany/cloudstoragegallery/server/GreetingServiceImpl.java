package com.mycompany.cloudstoragegallery.server;

import com.mycompany.cloudstoragegallery.client.GreetingService;
import com.mycompany.cloudstoragegallery.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.FinalizationException;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;
import com.google.appengine.api.files.LockException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import javax.servlet.http.*;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
    GreetingService {
  public static final String BUCKETNAME = "galleryai2";
  public static final String FILENAME = "testzip";

  public String greetServer(String input) throws 
  IllegalArgumentException, FileNotFoundException, FinalizationException, LockException, IOException {

    FileService fileService = FileServiceFactory.getFileService();
    GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
       .setBucket(BUCKETNAME)
       .setKey(FILENAME)
       .setMimeType("text/html")
       .setAcl("public-read")
       .addUserMetadata("myfield1", "my field value");

    AppEngineFile writableFile =
         fileService.createNewGSFile(optionsBuilder.build());
    // Open a channel to write to it
     boolean lock = false;
     FileWriteChannel writeChannel =
         fileService.openWriteChannel(writableFile, lock);
     // Different standard Java ways of writing to the channel
     // are possible. Here we use a PrintWriter:
     PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
     out.println("The woods are lovely dark and deep.");
     out.println("But I have promises to keep.");
     // Close without finalizing and save the file path for writing later
     out.close();
     String path = writableFile.getFullPath();
     // Write more to the file in a separate request:
     writableFile = new AppEngineFile(path);
     // Lock the file because we intend to finalize it and
     // no one else should be able to edit it
     lock = true;
     writeChannel = fileService.openWriteChannel(writableFile, lock);
     // This time we write to the channel directly
     writeChannel.write(ByteBuffer.wrap
               ("And miles to go before I sleep.".getBytes()));

     // Now finalize
     writeChannel.closeFinally();
     String result = "";
     result += "Done writing...";
    
     return result;
    
//    // Verify that the input is valid. 
//    if (!FieldVerifier.isValidName(input)) {
//      // If the input is not valid, throw an IllegalArgumentException back to
//      // the client.
//      throw new IllegalArgumentException(
//          "Name must be at least 4 characters long");
//    }
//
//    String serverInfo = getServletContext().getServerInfo();
//    String userAgent = getThreadLocalRequest().getHeader("User-Agent");
//
//    // Escape data from the client to avoid cross-site script vulnerabilities.
//    input = escapeHtml(input);
//    userAgent = escapeHtml(userAgent);
//
//    return "Hello, " + input + "!<br><br>I am running " + serverInfo
//        + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   * 
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;");
  }
}
