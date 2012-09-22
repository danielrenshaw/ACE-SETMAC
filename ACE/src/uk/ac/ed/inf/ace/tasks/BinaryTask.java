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
package uk.ac.ed.inf.ace.tasks;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.util.Set;
import uk.ac.ed.inf.ace.BinaryEvaluator;
import uk.ac.ed.inf.ace.Engine;
import uk.ac.ed.inf.ace.Evaluator;
import uk.ac.ed.inf.ace.Experiment;
import uk.ac.ed.inf.ace.ReadableDocument;
import uk.ac.ed.inf.ace.TaskBase;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class BinaryTask<E extends Engine<?, ?>, C extends uk.ac.ed.inf.ace.config.v1.BinaryTaskBase> extends TaskBase<E, C> {

  private static final Set<Object> labels = ImmutableSet.<Object>of(true, false);
  private static final Set<Object> targetLabels = ImmutableSet.<Object>of(true);
  private final Object trueLabel;

  protected BinaryTask(E engine, C config, Function<String, Object> parseConfigLabel) {
    super(engine, config);
    this.trueLabel = parseConfigLabel.apply(config.getTrueLabel());
  }
  
  @Override
  public Object parseLabel(String label) {
    return Boolean.parseBoolean(label);
  }
  
  @Override
  public Set<Object> getLabels() {
    return labels;
  }

  @Override
  public Object label(ReadableDocument document) {
    return document.getProperties().get(getLabelPropertyKey()).equals(trueLabel);
  }

  @Override
  public Evaluator createEvaluator(Experiment experiment) {
    return new BinaryEvaluator(experiment);
  }

  @Override
  public Evaluator readEvaluator(Experiment experiment, File file) throws Exception {
    return BinaryEvaluator.read(experiment, file);
  }

  @Override
  public Iterable<Object> getTargetLabels() {
    return targetLabels;
  }
}