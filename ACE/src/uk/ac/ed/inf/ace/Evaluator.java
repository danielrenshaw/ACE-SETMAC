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

import java.io.File;
import uk.ac.ed.inf.ace.utils.ConfusionMatrix.Result;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public interface Evaluator extends Iterable<Result> {

  void add(Object inputLabel, Object outputLabel);

  void add(Result result);

  void output(File file) throws Exception;

  Experiment getExperiment();

  double getAccuracy();

  double getPrecision();

  double getRecall();

  double getFScore();

  double getSpecificity();
  
  double getNegativePredictiveValue();
}