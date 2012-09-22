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

import com.google.common.base.Objects;

/**
 * Based on {@code com.google.common.base.Function} but allows exceptions to be thrown.
 *
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public interface FunctionEx<F, T> {

  /**
   * Returns the result of applying this function to {@code input}. This method is <i>generally
   * expected</i>, but not absolutely required, to have the following properties:
   *
   * <ul> <li>Its execution does not cause any observable side effects. <li>The computation is
   * <i>consistent with equals</i>; that is, {@link Objects#equals
   *     Objects.equals}{@code (a, b)} implies that {@code Objects.equals(function.apply(a),
   *     function.apply(b))}. </ul>
   *
   * @throws NullPointerException if {@code input} is null and this function does not accept null
   * arguments
   */
  T apply(F input) throws Exception;
}