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
package uk.ac.ed.inf.setmac.classifiers;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.TokenSequence;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import uk.ac.ed.inf.ace.ClassifierBase;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.ReadableDocument;
import uk.ac.ed.inf.ace.Task;
import uk.ac.ed.inf.ace.Trainer;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public abstract class MalletClassifierBase<C extends uk.ac.ed.inf.ace.config.v1.ClassifierBase>
    extends ClassifierBase<uk.ac.ed.inf.setmac.Engine, C> {

  protected static abstract class MalletTrainerBase implements Trainer {

    private final Pipe pipe;
    private final Map<Object, InstanceList> instanceLists = Maps.newHashMap();

    protected MalletTrainerBase(Pipe pipe) {
      this.pipe = pipe;
    }

    protected Pipe getPipe() {
      return pipe;
    }

    protected Map<Object, InstanceList> getInstanceLists() {
      return ImmutableMap.copyOf(instanceLists);
    }

    @Override
    public void add(ReadableDocument document, Object label) {
      InstanceList instanceList = instanceLists.get(label);

      if (instanceList == null) {
        instanceList = new InstanceList(pipe);
        instanceLists.put(label, instanceList);
      }

      instanceList.addThruPipe(getInstance(document, label));
    }
  }

  protected static abstract class MalletModelBase implements Model {

    private final Pipe pipe;

    protected MalletModelBase(Pipe pipe) {
      this.pipe = pipe;
    }

    protected Pipe getPipe() {
      return pipe;
    }

    protected abstract Object classify(Instance instance) throws Exception;

    @Override
    public Object classify(ReadableDocument document, Object actualLabel, Task task) throws Exception {
      return classify(getInstance(document, actualLabel, pipe));
    }
  }

  protected MalletClassifierBase(Engine engine, C config) {
    super(engine, config);
  }

  protected static Instance getInstance(ReadableDocument document, Object label, Pipe pipe) {
    return pipe.instanceFrom(getInstance(document, label));
  }

  protected static Instance getInstance(ReadableDocument document, Object label) {
    TokenSequence tokenSequence = new TokenSequence();
    @SuppressWarnings("unchecked")
    Iterable<String> content = (Iterable<String>) document.getContent();

    for (String token : content) {
      tokenSequence.add(token);
    }

    return new Instance(tokenSequence, label, document.getId(), null);
  }
}