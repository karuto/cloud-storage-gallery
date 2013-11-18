package com.mycompany.cloudstoragegallery.client;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.appengine.api.files.FinalizationException;
import com.google.appengine.api.files.LockException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
  String greetServer(String name) throws IllegalArgumentException, FileNotFoundException, FinalizationException, LockException, IOException;
}
