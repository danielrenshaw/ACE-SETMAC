/*
 * Copyright 2012 Daniel.
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
package uk.ac.ed.inf.acedemo.classifiers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import uk.ac.ed.inf.ace.ReadableDocument;
import uk.ac.ed.inf.ace.SimpleEngine;
import uk.ac.ed.inf.ace.classifiers.WekaClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class Classifier extends WekaClassifier<SimpleEngine, uk.ac.ed.inf.ace.config.v1.WekaClassifier> {
  public Classifier(SimpleEngine engine, uk.ac.ed.inf.ace.config.v1.WekaClassifier config) {
    super(engine, config);
  }

  @Override
  protected Instances constructInstances() {
    ArrayList<Attribute> attributes = Lists.newArrayList();
    attributes.add(new Attribute("IsSpam", ImmutableList.of("true", "false")));
    
    for (int index = 0; index < 57; index++) {
      attributes.add(new Attribute("Attribute" + index));
    }
    
    Instances instances = new Instances("Data", attributes, 1);
    instances.setClassIndex(0);
    return instances;
  }

  @Override
  protected Instance constructInstance(ReadableDocument document) {
    Instance instance = new DenseInstance(58);
    double[] values = (double[])document.getContent();
    
    for (int index = 0; index < values.length; index++) {
      instance.setValue(index + 1, values[index]);
    }

    return instance;
  }
}
