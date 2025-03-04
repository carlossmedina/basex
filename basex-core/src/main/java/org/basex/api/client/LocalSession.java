package org.basex.api.client;

import java.io.*;

import org.basex.core.*;
import org.basex.core.cmd.*;
import org.basex.core.parse.*;
import org.basex.query.*;

/**
 * This class offers methods to locally execute database commands.
 *
 * @author BaseX Team 2005-24, BSD License
 * @author Christian Gruen
 */
public class LocalSession extends Session {
  /** Database context. */
  private final Context ctx;

  /**
   * Default constructor.
   * @param context context
   */
  public LocalSession(final Context context) {
    this(context, null);
  }

  /**
   * Constructor, specifying an output stream.
   * @param context context
   * @param output client output; if set to {@code null}, results will be returned as strings
   */
  public LocalSession(final Context context, final OutputStream output) {
    super(output);
    ctx = new Context(context);
  }

  @Override
  public void create(final String name, final InputStream input) throws BaseXException {
    execute(new CreateDB(name), input);
  }

  @Override
  public void add(final String path, final InputStream input) throws BaseXException {
    execute(new Add(path), input);
  }

  @Override
  public void put(final String path, final InputStream input) throws BaseXException {
    execute(new Put(path), input);
  }

  @Override
  public void putBinary(final String path, final InputStream input) throws BaseXException {
    execute(new BinaryPut(path), input);
  }

  /**
   * Executes a command, passing the specified input.
   * @param cmd command
   * @param input input stream
   * @throws BaseXException database exception
   */
  private void execute(final Command cmd, final InputStream input) throws BaseXException {
    cmd.setInput(input);
    cmd.execute(ctx);
    info = cmd.info();
  }

  @Override
  public LocalQuery query(final String query) {
    return new LocalQuery(query, ctx, out);
  }

  @Override
  public synchronized void close() {
    Close.close(ctx);
  }

  @Override
  protected void execute(final String command, final OutputStream output) throws BaseXException {
    try {
      execute(CommandParser.get(command, ctx).parseSingle(), output);
    } catch(final QueryException ex) {
      throw new BaseXException(ex);
    }
  }

  @Override
  protected void execute(final Command command, final OutputStream output) throws BaseXException {
    command.execute(ctx, output);
    info = command.info();
  }

  /**
   * Returns the associated database context.
   * Called from the XQJ driver.
   * @return database context
   */
  public Context context() {
    return ctx;
  }
}
