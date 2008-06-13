package org.basex.query.fs;

import java.io.IOException;
import org.basex.core.Context;
import org.basex.io.PrintOutput;
import org.basex.util.GetOpts;

/**
 * Performs a rm command.
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Hannes Schwarz - Hannes.Schwarz@gmail.com
 *
 */
public final class RM {

  /** Data reference. */
  private final Context context;

  /** current dir. */
  private int curDirPre;

  /** PrintOutPutStream. */
  private PrintOutput out;

  /** Shows if an error occurs. */
  private boolean fError;
  
  /** Shows if job is done. */
  private boolean fAccomplished;

  /** Remove the all. */
  private boolean fRecursive;
  
  /**
   * Simplified Constructor.
   * @param ctx data context
   * @param output output stream
   */
  public RM(final Context ctx, final PrintOutput output) {
    this.context = ctx;
    curDirPre = ctx.current().pre[0];
    this.out = output;
  }

  /**
   * Performs an rm command.
   * 
   * @param cmd - command line
   * @throws IOException - in case of problems with the PrintOutput 
   */
  public void rmMain(final String cmd) 
  throws IOException {

    GetOpts g = new GetOpts(cmd, "Rh");
    // get all Options
    int ch = g.getopt();
    while (ch != -1) {
      switch (ch) {
        case 'h':
          printHelp();
          fAccomplished = true;
          break;
        case 'R':
          fRecursive = true;
          break;          
        case ':':         
          fError = true;
          out.print("rm: missing argument");
          break;  
        case '?':         
          fError = true;
          out.print("rm: illegal option");
          break;
      }      
      if(fError || fAccomplished) {
        return;
      }
      ch = g.getopt();
    }

    // if there is path expression remove it     
    if(g.getPath() != null) {      
      remove(g.getPath());
    } 
  }

  /**
   * Performs an rm command.
   *  
   *  @param path The name of the file
   *  @throws IOException in case of problems with the PrintOutput 
   */
  private void remove(final String path) throws IOException {

    String file = "";
    int beginIndex = path.lastIndexOf('/');
    if(beginIndex == -1) {
      file = path;
    } else {
      // noch zu machen...
      curDirPre = FSUtils.goToDir(context.data(), curDirPre, 
          path.substring(0, beginIndex));   
      if(curDirPre == -1) {
        out.print("rm: '" + path + "' No such file or directory");
      } else {
        file = path.substring(beginIndex + 1);
      }
    }
    
    int del = FSUtils.getSpecificFileOrDir(context.data(), curDirPre, 
        file.getBytes());

    if(del == -1) {
      out.print("rm: '" + file + "' No such file or directory");
      return;
    } else {
      if((FSUtils.isDir(context.data(), del) && fRecursive) ||
         (FSUtils.isFile(context.data(), del))) {
        try {
          context.data().delete(del);
          context.data().flush();
        } catch(Exception e) {
          e.printStackTrace();
        }     
      } else {
        out.print("rm: cannot remove '" + file + "': Is a directory");
      }
    }
  }


  /**
   * Print the help.
   * 
   * @throws IOException in case of problems with the PrintOutput
   */
  private void printHelp() throws IOException {
    out.print("rm [-Rh] file ...");

  }

}

