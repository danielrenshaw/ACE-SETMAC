/*
 * Copyright 2012 Daniel Renshaw &lt;d.renshaw@sms.ed.ac.uk&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ed.inf.ace.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;

/**
 * Provides a means of encoding (mapping string tokens to identifiers) and decoding (mapping
 * identifiers to string tokens) document content. Instances of this class are thread safe.
 *
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Dictionary {

  private final Map<String, Integer> tokenToId = Maps.newHashMap();
  private final Map<Integer, String> idToToken = Maps.newHashMap();
  private int nextId;

  /**
   * If the token has been seen by the dictionary previously, returns the identifier it is already
   * associated with. If it's a new token, assigns, records, and returns a new identifier. Null
   * tokens are not supported. Computational complexity: O(1).
   *
   * @param token The token to be mapped to an identifier.
   * @return The identifier of the token.
   */
  public synchronized int get(String token) {
    Preconditions.checkNotNull(token);
    Integer id = tokenToId.get(token);

    if (id == null) {
      id = nextId++;
      tokenToId.put(token, id);
      idToToken.put(id, token);
    }

    return id;
  }

  /**
   * If the specified identifier is known to the dictionary, returns the token it is associated
   * with, otherwise returns {@code null}. Computational complexity: O(1).
   *
   * @param id The identifier to be mapped to a token.
   * @return The token associated with the identifier if known, otherwise {@code null}.
   */
  public synchronized String get(int id) {
    return idToToken.get(id);
  }

  /**
   * @return The number of tokens mapped to identifiers by this dictionary.
   */
  public synchronized int size() {
    assert tokenToId.size() == idToToken.size();
    return tokenToId.size();
  }

  /**
   * @return A view of the tokens mapped by this dictionary.
   */
  public synchronized Set<String> tokens() {
    return tokenToId.keySet();
  }

  /**
   * @return A view of the identifiers mapped by this dictionary.
   */
  public synchronized Set<Integer> ids() {
    return idToToken.keySet();
  }
}