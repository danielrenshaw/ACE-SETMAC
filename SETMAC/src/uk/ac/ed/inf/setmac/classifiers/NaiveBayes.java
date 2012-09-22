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

import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.pipe.*;
import cc.mallet.types.*;
import cc.mallet.types.Multinomial.Logged;
import com.google.common.collect.MinMaxPriorityQueue;
import java.io.File;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.Set;
import uk.ac.ed.inf.ace.Model;
import uk.ac.ed.inf.ace.RandomSource;
import uk.ac.ed.inf.ace.Trainer;
import uk.ac.ed.inf.setmac.Engine;

/**
 * @author "Daniel Renshaw" &lt;d.renshaw@sms.ed.ac.uk&gt;
 */
public class NaiveBayes extends MalletClassifierBase<uk.ac.ed.inf.ace.config.v1.Classifier> {

  private class MalletTrainer extends MalletTrainerBase {

    private final Set<Object> labels;

    private MalletTrainer(Set<Object> labels) {
      super(new SerialPipes(new Pipe[]{
            new Target2Label(), // Target String -> class label
            new TokenSequence2FeatureSequence(), // Replace each Token with a feature index
            new FeatureSequence2FeatureVector(), // Collapse word order into a "feature vector"
          }));
      this.labels = labels;
    }

    @Override
    public Model createModel() {
      NaiveBayesTrainer trainer = new NaiveBayesTrainer();

      for (Object label : labels) {
        InstanceList instanceList = getInstanceLists().get(label);

        if (instanceList != null) {
          trainer.trainIncremental(instanceList);
        }
      }

      return new MalletModel(getPipe(), trainer.getClassifier());
    }
  }

  private static class ComparableIDSorter implements Comparable<ComparableIDSorter> {

    private final IDSorter idSorter;

    private ComparableIDSorter(IDSorter idSorter) {
      this.idSorter = idSorter;
    }

    @Override
    public int compareTo(ComparableIDSorter o2) {
      return idSorter.compareTo(o2.idSorter);
    }

    public int getID() {
      return idSorter.getID();
    }

    public double getWeight() {
      return idSorter.getWeight();
    }

    public void set(int id, double p) {
      idSorter.set(id, p);
    }

    @Override
    public int hashCode() {
      return idSorter.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final ComparableIDSorter other = (ComparableIDSorter) obj;
      if (!Objects.equals(this.idSorter, other.idSorter)) {
        return false;
      }
      return true;
    }

    @Override
    public String toString() {
      return idSorter.toString();
    }
  }

  private class MalletModel extends MalletModelBase {

    private final cc.mallet.classify.NaiveBayes classifier;

    private MalletModel(Pipe pipe, cc.mallet.classify.NaiveBayes classifier) {
      super(pipe);
      this.classifier = classifier;
    }

    @Override
    protected Object classify(Instance instance) throws Exception {
      return classifier.classify(instance).getLabeling().getBestLabel().getEntry();
    }

    @Override
    public void write(File outputDirectory, String outputFilePrefix) throws Exception {
      Logged priors = classifier.getPriors();
      Logged[] classesLikelihoods = classifier.getMultinomials();
      LabelAlphabet classes = classifier.getLabelAlphabet();

      try (PrintWriter printWriter = new PrintWriter(new File(outputDirectory,
              outputFilePrefix + "Model.xml"), "UTF-8")) {
        printWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<naiveBayesModel>\n");

        for (int classIndex = 0; classIndex < classes.size(); classIndex++) {
          printWriter.write(String.format("  <class label=\"%s\" prior=\"%f\" logPrior=\"%f\">\n",
              classes.lookupLabel(classIndex).getEntry(), priors.probability(classIndex),
              priors.logProbability(classIndex)));
          Logged likelihoods = classesLikelihoods[classIndex];
          Alphabet likelihoodsAlphabet = likelihoods.getAlphabet();
          MinMaxPriorityQueue<ComparableIDSorter> queue = MinMaxPriorityQueue.<ComparableIDSorter>create();

          for (int likelihoodIndex = 0; likelihoodIndex < likelihoods.size(); likelihoodIndex++) {
            queue.add(new ComparableIDSorter(new IDSorter(likelihoodIndex, likelihoods.logProbability(likelihoodIndex))));

            if (queue.size() > 200) {
              queue.removeLast();
            }
          }

          while (!queue.isEmpty()) {
            ComparableIDSorter idSorter = queue.removeFirst();
            printWriter.write(String.format("    <feature label=\"%s\" likelihood=\"%f\""
                + " logLikelihood=\"%f\" />\n", likelihoodsAlphabet.lookupObject(idSorter.getID()),
                Math.exp(idSorter.getWeight()), idSorter.getWeight()));
          }

          printWriter.write("  </class>\n");
        }

        printWriter.write("</naiveBayesModel>");
      }
    }
  }

  public NaiveBayes(Engine engine, uk.ac.ed.inf.ace.config.v1.Classifier config) {
    super(engine, config);
  }

  @Override
  public Trainer createTrainer(RandomSource randomSource, Set<Object> labels) {
    return new MalletTrainer(labels);
  }
}