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
 * Accepts documents incrementally to train a classifier. Only those documents added before
 * {@link #createModel()} is called will have any effect on the resulting {@link Model}. Calling
 * {@link #createModel()} multiple times or adding documents after creating a {@link Model} will
 * result in undefined behaviour.
 *
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public interface Trainer {

  /**
   * Adds a training document. The added document may be used to update the model immediately and
   * incrementally, or may be stored to be processed with all others when {@link #createModel()} is
   * called.
   *
   * @param document A training document to be considered by the model.
   * @param label The true classification label of the training document.
   */
  void add(ReadableDocument document, Object label) throws Exception;

  /**
   * Produces a trained classification model based on the documents previously added to the trainer.
   *
   * @return The trained classification model.
   * @throws Exception
   */
  Model createModel() throws Exception;
}