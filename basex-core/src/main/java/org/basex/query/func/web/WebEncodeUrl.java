package org.basex.query.func.web;

import java.net.*;
import java.nio.charset.StandardCharsets;

import org.basex.query.*;
import org.basex.query.value.item.*;
import org.basex.util.*;

/**
 * Function implementation.
 *
 * @author BaseX Team 2005-22, BSD License
 * @author Christian Gruen
 */
public final class WebEncodeUrl extends WebFn {
  @Override
  public Str item(final QueryContext qc, final InputInfo ii) throws QueryException {
      return Str.get(URLEncoder.encode(toString(exprs[0], qc), StandardCharsets.UTF_8));
  }
}
