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
package uk.ac.ed.inf.ace;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class TaskBase<E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.TaskBase>
    extends TypeableBase<E, C> implements Task {

  private final String labelPropertyKey;

  protected TaskBase(E engine, C config) {
    super(engine, config);
    this.labelPropertyKey = config.getLabelPropertyKey();
  }

  protected String getLabelPropertyKey() {
    return labelPropertyKey;
  }
}